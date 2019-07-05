package mocean.logs.service;

import mocean.logs.domain.GslbLogsBean;
import mocean.logs.hiveutil.StringUtil;
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
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class HBaseGslbSparkQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    public Log logger = LogFactory.getLog( HBaseGslbSparkQuery.class );


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

    public ArrayList<GslbLogsBean> start(PageBean pageBean) {
        //初始化sparkContext，
        SparkConf sparkConf = new SparkConf().setAppName( "HBaseGslb" ).setMaster( "local[*]" );
        sparkConf.set( "spark.serializer", "org.apache.spark.serializer.KryoSerializer" );
        sparkConf.set( "spark.io.compression.codec", "snappy" );
        sparkConf.set( "spark.driver.allowMultipleContexts", "true" );
        sparkConf.set( "spark.kryoserializer.buffer.max", "256m" );
        sparkConf.set( "spark.kryoserializer.buffer", "64m" );
        JavaSparkContext sc = new JavaSparkContext( sparkConf );

        Configuration conf = HBaseConfiguration.create();
        //设置查询条件，这里值返回用户的等级

        String keys = getRowKeys( pageBean );

        String startkey = keys.split( "=" )[0];
        String stopkey = keys.split( "=" )[1];
        logger.info( startkey+"startkey"+ stopkey);

        Scan scan = new Scan();
        scan.setStartRow( Bytes.toBytes( startkey ) );
        scan.setStopRow( Bytes.toBytes( stopkey ) );
        scan.addFamily( Bytes.toBytes( "f1" ) );
        scan.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "gslblogs" ) );
        Map<String, Map<String, Long>> maps = new HashedMap();
        try {
            //需要读取的hbase表名
            String tableName = "ns1:gslblogs";
            conf.set( TableInputFormat.INPUT_TABLE, tableName );
            conf.set( TableInputFormat.SCAN, convertScanToString( scan ) );

            //获得hbase查询结果Result
            JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD( conf,
                    TableInputFormat.class, ImmutableBytesWritable.class,
                    Result.class );
//            logger.info( hBaseRDD.first() + " hBaseRDD++++===" + hBaseRDD.count() );
            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss SSS" );
            logger.info( dateFormat.format( new Date() ) + " getRating Result " );
            JavaRDD<String>  valuess = hBaseRDD.map( new Function<Tuple2<ImmutableBytesWritable, Result>, String>() {
                private static final long serialVersionUID = 1L;

                public String call(Tuple2<ImmutableBytesWritable, Result> t) throws Exception {
                    String value = Bytes.toString( t._2.getValue( Bytes.toBytes( "f1" ), Bytes.toBytes( "gslblogs" ) ) );
                    return value;
                }
            } );
            List<String> Str = valuess.collect();

            ArrayList<GslbLogsBean> list = new ArrayList<>(  );
            if(Str.size()==0){
                return list;
            }
            for (String values:Str) {
                GslbLogsBean gs = new GslbLogsBean();
                gs.setServer( values.split( "," )[0] );
                gs.setProtocol( values.split( "," )[1] );
                gs.setAddress( values.split( "," )[2] );
                gs.setNote( values.split( "," )[3] );
                gs.setUser_agent( values.split( "," )[4] );
                gs.setRedirect_url( values.split( "," )[5] );
                gs.setCurrent_time( values.split( "," )[6] );
                list.add( gs );
            }

            return list;

        } catch (Exception e) {
            logger.warn( e );
        } finally {
            if (sc != null)
                sc.stop();
        }
        return  new ArrayList<>(  );

    }

    private String getRowKeys(PageBean pageBean) {
        boolean server = "".equals( pageBean.getServer_address() );
        boolean address = "".equals( pageBean.getAddress() );
        boolean startTime = null == (pageBean.getStartTime());
        boolean endTime = null == (pageBean.getEndTime());
        if (server && address && startTime) {
            String startkey = "1," + StringUtil.date( 10 ) + ",";
            String stopkey = "1," + StringUtil.datenow() + ",";
            return startkey + "=" + stopkey;
        }
        if(!server){
            String startkey = "2," + pageBean.getServer_address() + ",";
            String stopkey = "2," + pageBean.getServer_address() + "|";
            return startkey + "=" + stopkey;
        }
        if(!address){
            String startkey = "3," + pageBean.getAddress() + ",";
            String stopkey = "3," + pageBean.getAddress() + "|";
            return startkey + "=" + stopkey;
        }
        if(!endTime){
            String startkey = "1," + pageBean.getStartTime() + ",";
            String stopkey = "1," + pageBean.getEndTime() + "|";
            return startkey + "=" + stopkey;
        }
        return "=";
    }


    /**
     * spark如果计算没写在main里面,实现的类必须继承Serializable接口，<br>
     * </>否则会报 Task not serializable: java.io.NotSerializableException 异常
     */

    public static void main(String[] args) throws InterruptedException {
//        new HBaseSparkQuery().start();
    }
}
