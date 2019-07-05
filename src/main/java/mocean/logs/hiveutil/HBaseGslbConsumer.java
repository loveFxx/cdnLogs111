package mocean.logs.hiveutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import mocean.logs.common.Common;
import mocean.logs.domain.CdnChannelBean;
import mocean.logs.domain.CdnLogsBean;
import mocean.logs.domain.GslbLogsBean;
import mocean.logs.service.CdnChannelService;
import mocean.logs.util.DateUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//import mocean.logs.controller.CdnLogsBean;

/**
 * 原生数据消费者
 */
public class HBaseGslbConsumer {


    private final ConsumerConnector consumerConn;
    private final String topic_hive = "gslblogs";
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    public HBaseGslbConsumer() {
        Properties props = new Properties();
        props.put( "zookeeper.connect", "185.180.221.108:2181" );
        props.put( "group.id", "cdnlogss" );//test-consumer-group
        props.put( "auto.offset.reset", "largest" );
        props.put( "session.timeout.ms", "30000" );
        props.put( "zookeeper.sync.time.ms", "2500" );
        props.put( "enable.auto.commit", "true" );
        props.put( "max.poll.records", "100" );
        props.put( "auto.commit.interval.ms", "15000" );
        props.put( "fetch.message.max.bytes","214748357" );//2147483571 1048576000
        props.put( "fetch.max.bytes","214748357" );//2147483571 1048576000
        props.put( "receive.buffer.bytes","102400" );//2147483571 1048576000
        props.put( "send.buffer.bytes","102400" );//2147483571 1048576000
        props.put( "max.partition.fetch.bytes","214748357" );//2147483571 1048576000

        // 创建消费者连接器
        consumerConn = Consumer.createJavaConsumerConnector( new ConsumerConfig( props ) );
    }

    /**
     * 处理log
     */
    public synchronized void processLog() {
        // 指定消费的主题

        Map<String, Integer> topicCount = new HashMap<String, Integer>();


        topicCount.put( topic_hive, new Integer( 1 ) );
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumerConn.createMessageStreams( topicCount );
        Integer a = Integer.MAX_VALUE;
        List<Put> putList = new ArrayList<>();
        // 的到指定主题的消息列表
        while (true) {
            List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get( topic_hive );
            Configuration conf = HBaseConfiguration.create();
            conf.set( "hbase.zookeeper.quorum", "185.2.81.153:2181" );
            Connection conn = null;
            HTable table = null;
            try {
                conn = ConnectionFactory.createConnection( conf );
                TableName tname = TableName.valueOf( "ns1:gslblogs" );
                table = (HTable) conn.getTable( tname );
//             table.setWriteBufferSize( 1 * 1024 * 1024 );
                table.setAutoFlush( false );
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (final KafkaStream stream : streams) {
                ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
                //迭代日志消息
                while (consumerIte.hasNext()) {
                    byte[] msg = consumerIte.next().message();
                    String log = new String( msg );
                    try {
                        JSONObject jsonObject = JSON.parseObject( log );
                        JSONArray jsonArray = jsonObject.getJSONArray( "clients" );
                        String server = jsonObject.getString( "server" );
                        List<GslbLogsBean> list = JSON.parseArray( jsonArray.toJSONString(), GslbLogsBean.class );

                        for (GslbLogsBean gs : list) {
                            gs.setServer( server );
                            gs.setCurrent_time( DateUtil.getTransTimeByTimeStamp( Long.valueOf( gs.getCurrent_time() ) * 1000 ) );
                            String newMsg = gs.getServer() + "," + gs.getProtocol() + ","
                                    + gs.getAddress() + "," + gs.getNote() + ","
                                    + gs.getUser_agent() + "," + gs.getRedirect_url() + ","
                                    + gs.getCurrent_time() ;
                            String rowkey = "1" + "," + gs.getCurrent_time()  + ","+System.currentTimeMillis();
                            String newRowKey_status3 = "2"+","+gs.getServer()+ ","+System.currentTimeMillis();
                            String newRowKey_sn4 = "3"+","+gs.getAddress()+ ","+System.currentTimeMillis();

                            //存放数据
                            Put putcdnLogs = new Put( Bytes.toBytes( rowkey ) );
                            putcdnLogs.setWriteToWAL( false );
                            putcdnLogs.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "gslblogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( putcdnLogs );

                            Put new3 = new Put( Bytes.toBytes( newRowKey_status3 ) );
                            new3.setWriteToWAL( false );
                            new3.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "gslblogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( new3 );

                            Put new4 = new Put( Bytes.toBytes( newRowKey_sn4 ) );
                            new4.setWriteToWAL( false );
                            new4.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "gslblogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( new4 );

                            if (putList.size() >= 5) {
                                logger.info( putList.size()+" gslblogs success" );
                                table.put( putList );
                                table.flushCommits();
                                putList = new ArrayList<>();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            try {
                if (table != null) {
                    table.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                consumerConn.commitOffsets();
                System.out.println( "-------------------------------" + Common.getCountcon() );
            }
        }
    }
}
