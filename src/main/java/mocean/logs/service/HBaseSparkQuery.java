package mocean.logs.service;

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
import java.util.*;


public class HBaseSparkQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    public Log logger = LogFactory.getLog( HBaseSparkQuery.class );


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

    public Map<String,Map<String,Long>> start(boolean IsStart, String startTime,String stopTime) {
        //初始化sparkContext，
        SparkConf sparkConf = new SparkConf().setAppName( "HBaseTest" ).setMaster( "local[*]" );
        sparkConf.set( "spark.serializer", "org.apache.spark.serializer.KryoSerializer" );
        sparkConf.set( "spark.io.compression.codec","snappy" );
        sparkConf.set( "spark.driver.allowMultipleContexts", "true" );
        sparkConf.set( "spark.kryoserializer.buffer.max", "256m" );
        sparkConf.set( "spark.kryoserializer.buffer", "64m" );
        JavaSparkContext sc = new JavaSparkContext( sparkConf );

        //使用HBaseConfiguration.create()生成Configuration
        // 必须在项目classpath下放上hadoop以及hbase的配置文件。
        Configuration conf = HBaseConfiguration.create();
        //设置查询条件，这里值返回用户的等级
        Scan scan = new Scan();
        scan.setStartRow( Bytes.toBytes( "TV" ) );
        scan.setStopRow( Bytes.toBytes( "TV|" ) );
//        scan.setBatch( 1000 );
//        scan.setCacheBlocks( false );
        scan.addFamily( Bytes.toBytes( "f1" ) );

        scan.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "rating" ) );

        JavaRDD<String> valuess=null;
        Map<String,Map<String,Long>> maps = new HashedMap(  );

        try {
            //需要读取的hbase表名
            String tableName = "ns1:rating";
            conf.set( TableInputFormat.INPUT_TABLE, tableName );
            conf.set( TableInputFormat.SCAN, convertScanToString( scan ) );

            //获得hbase查询结果Result
            JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD( conf,
                    TableInputFormat.class, ImmutableBytesWritable.class,
                    Result.class );
//            logger.info( hBaseRDD.first() + " hBaseRDD++++===" + hBaseRDD.count() );
            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss SSS" );
            logger.info( dateFormat.format( new Date() ) + " getRating Result " );
            valuess = hBaseRDD.map( new Function<Tuple2<ImmutableBytesWritable, Result>, String>() {
                private static final long serialVersionUID = 1L;
                public String call(Tuple2<ImmutableBytesWritable, Result> t) throws Exception {
                    String value = Bytes.toString( t._2.getValue( Bytes.toBytes( "f1" ), Bytes.toBytes( "rating" ) ) );
                    String[] fields = value.split(",");
                    if (IsStart) {
                        if (startTime.compareTo( fields[3] ) > 0 || stopTime.compareTo( fields[2] ) < 0) {

                        } else {
                            return fields[0]+","+fields[1];
                        }
                    } else {
                        return fields[0]+","+fields[1];
                    }
                    return "";
                }
            } );
            JavaRDD<String> valuefilter = valuess.filter( new Function<String, Boolean>() {
                private static final long serialVersionUID = 3L;
                @Override
                public Boolean call(String s) throws Exception {
                    return !"".equals( s );
                }
            } );
            valuefilter.persist( StorageLevel.MEMORY_ONLY() );

            logger.info( dateFormat.format( new Date() ) + " getRating persist " );
            JavaPairRDD<String, Integer> ones = valuefilter.mapToPair( new PairFunction<String, String, Integer>() {
                private static final long serialVersionUID = 2L;
                public Tuple2<String, Integer> call(String s) {
                    if(!"".equals( s )){
                        return new Tuple2<String, Integer>(s.split( "," )[0], 1);
                    }
                     return new Tuple2<String, Integer>("", 0);


                }
            });
            JavaPairRDD<String, Integer> onedis = valuefilter.distinct().mapToPair( new PairFunction<String, String, Integer>() {
                private static final long serialVersionUID = 2L;
                public Tuple2<String, Integer> call(String s) {
                    if(!"".equals( s )){
                        return new Tuple2<String, Integer>(s.split( "," )[0], 1);
                    }
                    return new Tuple2<String, Integer>("", 0);
//                    return new Tuple2<String, Integer>(s.split( "," )[0], 1);
                }
            });

            Map<String,Long> map = ones.countByKey();
            maps.put( "map" ,map);
            Map<String,Long> mapdis = onedis.countByKey();
            maps.put( "mapdis" ,mapdis);



        } catch (Exception e) {
            logger.warn( e );
        } finally {
            if (sc != null)
                sc.stop();
        }
        return maps;

    }


    /**
     * spark如果计算没写在main里面,实现的类必须继承Serializable接口，<br>
     * </>否则会报 Task not serializable: java.io.NotSerializableException 异常
     */

    public static void main(String[] args) throws InterruptedException {
//        new HBaseSparkQuery().start();
    }
}
