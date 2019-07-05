package mocean.logs.service;

import mocean.logs.domain.*;
import mocean.logs.hiveutil.StringUtil;
import mocean.logs.hiveutil.getRowKey;
import mocean.logs.util.PageBean;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class HBaseClientSparkQuery implements Serializable {

    private static final long serialVersionUID =3L;

    public Log logger = LogFactory.getLog( HBaseClientSparkQuery.class );


    /**
     * 将scan编码，该方法copy自 org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil
     *
     * @param scan
     * @return
     * @throws IOException
     */

    static String convertScanToString(Scan scan) throws IOException {
        ClientProtos.Scan proto = ProtobufUtil.toScan( scan );
        return Base64.encodeBytes( proto.toByteArray() );
    }

    public ArrayList<ClientLogsBean> start(PageBean pageBean) {
        //初始化sparkContext，
        SparkConf sparkConf = new SparkConf().setAppName( "HBaseClient" ).setMaster( "local[*]" );
        sparkConf.set( "spark.serializer", "org.apache.spark.serializer.KryoSerializer" );
        sparkConf.set( "spark.io.compression.codec", "snappy" );
        sparkConf.set( "spark.driver.allowMultipleContexts", "true" );
        sparkConf.set( "spark.kryoserializer.buffer.max", "256m" );
        sparkConf.set( "spark.kryoserializer.buffer", "64m" );
        JavaSparkContext sc = new JavaSparkContext( sparkConf );

        Configuration conf = HBaseConfiguration.create();
        //设置查询条件，这里值返回用户的等级

//        String keys = getRowKeys( pageBean );
//        String rows = getRowKey.getStartOrStopClient( pageBean );
//        String startkey = rows.split( "=" )[0];
//        String stopkey = rows.split( "=" )[1];
//        logger.info( startkey + "client startkey" + stopkey );

        Scan scan = new Scan();
        String startkey ;
        String stopkey ;
        if(!"".equals( pageBean.getSn() )){
            startkey = pageBean.getSn() ;
            stopkey = pageBean.getSn() + "|" ;
        }else if (!"0".equals( pageBean.getStartTime() )){
            startkey = pageBean.getStartTime() ;
            stopkey = pageBean.getEndTime() + "|" ;
        }else {
            startkey =  StringUtil.date( 10 );
            stopkey =  StringUtil.datenow() + "|";
        }
        logger.info(  "pageBean============" + pageBean.toString() );

        scan.setStartRow( Bytes.toBytes( startkey ) );
        scan.setStopRow( Bytes.toBytes( stopkey ) );
        scan.addFamily( Bytes.toBytes( "f1" ) );
        scan.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "clients" ) );
        Map<String, Map<String, Long>> maps = new HashedMap();
        try {
            //需要读取的hbase表名
            String tableName = "ns1:client";
            conf.set( TableInputFormat.INPUT_TABLE, tableName );
            conf.set( TableInputFormat.SCAN, convertScanToString( scan ) );
            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss SSS" );
            logger.info( dateFormat.format( new Date() ) + "findClientLogs start " );
            //获得hbase查询结果Result
            JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD( conf,
                    TableInputFormat.class, ImmutableBytesWritable.class,
                    Result.class );
//

            JavaRDD<String> valuess = hBaseRDD.map( new Function<Tuple2<ImmutableBytesWritable, Result>, String>() {
                private static final long serialVersionUID = 1L;

                public String call(Tuple2<ImmutableBytesWritable, Result> t) throws Exception {
                    String value = Bytes.toString( t._2.getValue( Bytes.toBytes( "f1" ), Bytes.toBytes( "clients" ) ) );
//                    logger.info( " hBaseRDDvalue++++===" + value );
                    return value;
                }
            } );
            System.out.println( valuess.count()+ "valuess++++===" +valuess.first() );
            List<String> Str;
            if(valuess.count()>50){
                 Str = valuess.top( 50 );
            }else {
                Str = valuess.collect();
            }
            logger.info( " hBaseRDD++++===" + Str.size() );
            ArrayList<ClientLogsBean> list = new ArrayList<>();
            for (String values : Str) {
                ClientLogsBean clientLogs = new ClientLogsBean();
                String client = values.split( "#" )[0];
                String login = values.split( "#" )[1];
                String program = "";
                if (values.split( "#" ).length == 3) {
                    program = values.split( "#" )[2];
                }
                clientLogs.setUserId( client.split( "," )[0] );
                clientLogs.setSendTime( client.split( "," )[1] );
                clientLogs.setApkVersion( client.split( "," )[2] );
                clientLogs.setDevicePlatform( client.split( "," )[3] );
                clientLogs.setSystemVersion( client.split( "," )[4] );
                clientLogs.setPid( client.split( "," )[5] );
                clientLogs.setMid( client.split( "," )[6] );
                clientLogs.setCid( client.split( "," )[7] );
                clientLogs.setTotalCpu( client.split( "," )[8] );
                clientLogs.setFtvCpu( client.split( "," )[9] );
                clientLogs.setTotalMemory( client.split( "," )[10] );
                clientLogs.setFreeMemory( client.split( "," )[11] );
                if (client.split( "," ).length == 12) {
                    clientLogs.setFtvMemory( "" );
                } else if (client.split( "," ).length > 12) {
                    clientLogs.setFtvMemory( client.split( "," )[12] );
                }
//                                clientLogs.setFtvMemory( client.split( "," )[12]);
                if (client.split( "," ).length == 13) {
                    clientLogs.setPingResult( "" );
                } else if (client.split( "," ).length > 13) {
                    clientLogs.setPingResult( client.split( "," )[13] );
                }

                List<ClientLogsLoginBean> loginlist = new ArrayList<>();
                if ("".equals( login ) || null != login) {
                    int login_len = login.split( "," ).length;
                    for (int a = 0; a < login_len; a = a + 5) {
                        ClientLogsLoginBean clientLogsLoginBean = new ClientLogsLoginBean();
                        clientLogsLoginBean.setStartTime( login.split( "," )[a] );
                        clientLogsLoginBean.setEndTime( login.split( "," )[a + 1] );
                        clientLogsLoginBean.setRequestName( login.split( "," )[a + 2] );
                        clientLogsLoginBean.setReturnCode( Integer.parseInt( login.split( "," )[a + 3] ) );
                        clientLogsLoginBean.setUrl( login.split( "," )[a + 4] );
                        loginlist.add( clientLogsLoginBean );
                    }
                }
                clientLogs.setLogin( loginlist );

                List<ClientLogsProgramBean> programlist = new ArrayList<>();
                if (values.split( "#" ).length == 3) {
                    int programlen = program.split( "\\$" ).length;

                    for (int i = 0; i < programlen; i = i + 1) {
                        ClientLogsProgramBean program1 = new ClientLogsProgramBean();
                        program1.setName( program.split( "\\$" )[i].split( "\\|" )[0] );

                        List<VideoInfoBean> videoList = new ArrayList<>();
                        String videolist = "";
                        if (program.split( "\\$" )[i].split( "\\|" ).length == 2) {
                            videolist = program.split( "\\$" )[i].split( "\\|" )[1];

                        }
//                                        System.out.println(videolist);
                        if (program.split( "\\$" )[i].split( "\\|" ).length == 2) {
                            int videolen = videolist.split( "," ).length;
                            for (int j = 0; j < videolen; j = j + 11) {
                                VideoInfoBean videoInfoBean = new VideoInfoBean();
                                videoInfoBean.setPlayurl( videolist.split( "," )[j] );
                                videoInfoBean.setCdnAddress( videolist.split( "," )[j + 1] );
                                videoInfoBean.setSpeed( videolist.split( "," )[j + 2] );
                                videoInfoBean.setResolution( videolist.split( "," )[j + 3] );
                                videoInfoBean.setVideoFormat( videolist.split( "," )[j + 4] );
                                videoInfoBean.setAudioFormat( videolist.split( "," )[j + 5] );
                                videoInfoBean.setStartSwitchTime( videolist.split( "," )[j + 6] );
                                videoInfoBean.setStartSetDataSourceTime( videolist.split( "," )[j + 7] );
                                videoInfoBean.setStartPlayTime( videolist.split( "," )[j + 8] );
                                videoInfoBean.setPlayErrorCode( videolist.split( "," )[j + 9] );
                                videoInfoBean.setProgramId( Integer.parseInt( videolist.split( "," )[j + 10] ) );
//                                                System.out.println("videoInfoBean="+videoInfoBean.toString());
                                videoList.add( videoInfoBean );
                            }
                        }
                        program1.setVideoInfo( videoList );
//                                        System.out.println( program1.getName());
                        programlist.add( program1 );
                    }
                }
                clientLogs.setProgram( programlist );
                list.add( clientLogs );
                /*if(list.size()>=500){
                    return list;
                }*/
            }

            return list;

        } catch (Exception e) {
            logger.warn( e );
        } finally {
            if (sc != null)
                sc.stop();
        }
        return new ArrayList<>();

    }

    private String getRowKeys(PageBean pageBean) {
        boolean server = "".equals( pageBean.getServer_address() );
        boolean address = "".equals( pageBean.getAddress() );
        boolean startTime = null == (pageBean.getStartTime());
        boolean endTime = null == (pageBean.getEndTime());
        if (server && address && startTime) {
            String startkey = "1," + StringUtil.date( 10 ) + ",";
            String stopkey = "1," + StringUtil.datenow() + ",";
            return startkey + "," + stopkey;
        }
        if (!server) {
            String startkey = "2," + pageBean.getServer_address() + ",";
            String stopkey = "2," + pageBean.getServer_address() + "|";
            return startkey + "," + stopkey;
        }
        if (!address) {
            String startkey = "3," + pageBean.getAddress() + ",";
            String stopkey = "3," + pageBean.getAddress() + "|";
            return startkey + "," + stopkey;
        }
        if (!endTime) {
            String startkey = "1," + pageBean.getStartTime() + ",";
            String stopkey = "1," + pageBean.getEndTime() + "|";
            return startkey + "," + stopkey;
        }
        return "";
    }


    /**
     * spark如果计算没写在main里面,实现的类必须继承Serializable接口，<br>
     * </>否则会报 Task not serializable: java.io.NotSerializableException 异常
     */

    public static void main(String[] args) throws InterruptedException {
//        new HBaseSparkQuery().start();
    }
}
