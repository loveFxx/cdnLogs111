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
//import mocean.logs.controller.CdnLogsBean;

import mocean.logs.domain.CdnChannelBean;
import mocean.logs.domain.CdnLogsBean;
import mocean.logs.service.CdnChannelService;
import mocean.logs.util.DateUtil;
import org.apache.commons.collections.map.HashedMap;
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

/**
 * 原生数据消费者
 */
public class HBaseConsumer {

    @Resource
    private CdnChannelService cdnChannelService;

    private final ConsumerConnector consumerConn;
    private final String topic_hive = "cdnlog108";
    private Logger logger = LoggerFactory.getLogger( this.getClass() );
    final int a_numThreads = 3;

    public HBaseConsumer() {
        Properties props = new Properties();
//        props.put( "zookeeper.connect", "185.180.221.108:2181" );
//        props.put( "group.id", "cdnlogss" );//test-consumer-group
//        props.put( "auto.offset.reset", "largest" );
//        props.put( "session.timeout.ms", "3000" );
//        props.put( "zookeeper.sync.time.ms", "2500" );
//        props.put( "enable.auto.commit", "true" );
//        props.put( "max.poll.records", "150" );
//        props.put( "auto.commit.interval.ms", "6000" );
//        props.put( "fetch.message.max.bytes","1048576000" );
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
        List<Put> putListrating = new ArrayList<>();
        // 的到指定主题的消息列表
        while (true) {
            List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get( topic_hive );
            Configuration conf = HBaseConfiguration.create();
            conf.set( "hbase.zookeeper.quorum", "185.2.81.153:2181" );
            Connection conn = null;
            HTable table = null;
            HTable tablerating = null;
            try {
                conn = ConnectionFactory.createConnection( conf );
                TableName tname = TableName.valueOf( "ns1:cdnlog" );
                table = (HTable) conn.getTable( tname );
//             table.setWriteBufferSize( 1 * 1024 * 1024 );
                table.setAutoFlush( false );

                TableName tnamerating = TableName.valueOf( "ns1:rating" );
                tablerating = (HTable) conn.getTable( tnamerating );
                tablerating.setAutoFlush( false );

            } catch (Exception e) {
                e.printStackTrace();
            }

            for (final KafkaStream stream : streams) {
                //
                ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
                //迭代日志消息
                String newMsg = "";
                while (consumerIte.hasNext()) {
                    byte[] msg = consumerIte.next().message();
                    String log = new String( msg );
//                    logger.info( "~~~~~~~~~" + log );
                    try {
                        JSONObject jsonObject = JSON.parseObject( log );
                        JSONArray jsonArray = jsonObject.getJSONArray( "clients" );
                        String server = jsonObject.getString( "server" );
                        List<CdnLogsBean> list = JSON.parseArray( jsonArray.toJSONString(), CdnLogsBean.class );

//                        if (Common.countcon != 0) {
//                            Common.setCount( Common.getCountcon() + list.size() );
//                        }

//                        System.out.println( "3333333333333333333333" );
                        for (CdnLogsBean cdnLogs : list) {
                            cdnLogs.setServer_address( server );
                            cdnLogs.setConnect_Times( DateUtil.getTransTimeByTimeStamp( Long.valueOf( cdnLogs.getConnect_time() ) * 1000 ) );
                            cdnLogs.setCurrent_Times( DateUtil.getTransTimeByTimeStamp( Long.valueOf( cdnLogs.getCurrent_time() ) * 1000 ) );
//                            System.out.println( "---------------=" );
                            newMsg = cdnLogs.getServer_address() + "," + cdnLogs.getProtocol() + ","
                                    + cdnLogs.getAddress() + "," + cdnLogs.getChannel_id() + ","
                                    + cdnLogs.getAuthinfo() + "," + cdnLogs.getSn() + ","
                                    + cdnLogs.getNote() + "," + cdnLogs.getUser_agent() + ","
                                    + cdnLogs.getResponse_code() + "," + cdnLogs.getCurrent_time() + ","
                                    + cdnLogs.getConnect_status() + "," + cdnLogs.getConnect_time() + ","
                                    + cdnLogs.getConnect_Times() + "," + cdnLogs.getStream_duration() + ","
                                    + cdnLogs.getStream_speed() + "," + cdnLogs.getStream_total_len() + ","
                                    + cdnLogs.getStream_lose_len() + "," + cdnLogs.getCurrent_Times();
//                            System.out.println( cdnLogs.getCurrent_Times() );
                            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                            Date date = sdf.parse( cdnLogs.getCurrent_Times() );
                            SimpleDateFormat localSDF = new SimpleDateFormat( "yyyy/MM/dd/HH/mm", Locale.US );
//                            System.out.println( date + "=" + localSDF.format( date ) );
                            long ts1 = System.currentTimeMillis();
                            long hashcode = (newMsg + ts1).hashCode();
//                            Configuration conf = HBaseConfiguration.create();
//                            conf.set( "hbase.zookeeper.quorum","185.180.221.108:2181" );
                            String newMsghash = cdnLogs.getConnect_status() + "," + cdnLogs.getConnect_status() + "," + cdnLogs.getNote() + "," +
                                    cdnLogs.getSn() + "," + StringUtil.ipFormat( cdnLogs.getServer_address() ) + "," +
                                    StringUtil.ipFormatClinet( cdnLogs.getAddress() ) + "," + cdnLogs.getCurrent_Times() + ","
                                    + cdnLogs.getChannel_id();
                            int hash = newMsghash.hashCode();
                            hash = (hash & Integer.MAX_VALUE) % 100;
                            //hash区域号
                            DecimalFormat df = new DecimalFormat();
                            df.applyPattern( "00" );
                            String regNo = df.format( hash );
//                            conn = ConnectionFactory.createConnection( conf );
//
//                            TableName tname = TableName.valueOf( "ns1:cdnlogtest" );
//                            table = (HTable)conn.getTable( tname );
////                            table.setWriteBufferSize( 6 * 1024 * 1024 );
//                            table.setAutoFlush( false );
                            String rowkey = "1" + "," + cdnLogs.getConnect_status() + "," + cdnLogs.getNote() + "," +
                                    cdnLogs.getSn() + "," + StringUtil.ipFormat( cdnLogs.getServer_address() ) + "," +
                                    StringUtil.ipFormatClinet( cdnLogs.getAddress() ) + "," + cdnLogs.getCurrent_Times() + ","
                                    + cdnLogs.getChannel_id() + ",";
//                            System.out.println( "++++++++++++++++rowkey===" + rowkey );
                            if ("".equals( cdnLogs.getSn() )) {
//                                System.out.println( "++++++++++++++++++" );
                                rowkey = rowkey.replaceFirst( "1", "2" );//sn为空异常
                            }
//                            rowkey=regNo+","+ rowkey;
//                            System.out.println( "rowkey==" + rowkey );

                            if("2".equals( cdnLogs.getConnect_status() )){
                                long duration = Integer.parseInt(cdnLogs.getStream_duration());
                                Map<String, CdnChannelBean> map1 = Common.getMap1();
//                                if (!map1.containsKey( cdnLogs.getChannel_id() )) {
//                                    List<CdnChannelBean> cdnChannelBean = cdnChannelService.getForChannelName( cdnLogs.getChannel_id() );
//                                    if (cdnChannelBean.size() != 0) {
//                                        map1.put( cdnLogs.getChannel_id(), cdnChannelBean.get( 0 ) );
//                                        Common.setMap1( map1 );
//                                    }
//                                }
                                if(duration>180){
                                    String rowkeyrating = cdnLogs.getChannel_id()+","+System.currentTimeMillis();
                                    String newMsgRating = cdnLogs.getChannel_id()+","+ cdnLogs.getSn() + ","
                                            + DateUtil.getTransTimeByTimeStamp( Long.valueOf( cdnLogs.getConnect_time() ) * 1000 ) + ","+
                                            DateUtil.getTransTimeByTimeStamp( Long.valueOf( cdnLogs.getCurrent_time() ) * 1000 ) + ","
                                            + cdnLogs.getStream_duration();
                                    Put putRating = new Put( Bytes.toBytes( rowkeyrating ) );
                                    putRating.setWriteToWAL( false );
                                    putRating.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "rating" ), Bytes.toBytes( newMsgRating ) );
                                    putListrating.add( putRating );

                                }

                            }


                            //存放数据
                            Put putcdnLogs = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
                            putcdnLogs.setWriteToWAL( false );
                            putcdnLogs.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "cdnLogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( putcdnLogs );


                            String newRowKey_status3 = "3"+","+cdnLogs.getNote()+ ","+System.currentTimeMillis();
                            String newRowKey_sn4 = "4"+","+cdnLogs.getSn()+ ","+System.currentTimeMillis();
                            String newRowKey_add5 = "5"+","+cdnLogs.getServer_address()+ ","+System.currentTimeMillis();
                            String newRowKey_server6 = "6"+","+cdnLogs.getAddress()+ ","+System.currentTimeMillis();
                            String newRowKey7_ = "7"+","+cdnLogs.getCurrent_Times()+ ","+System.currentTimeMillis();
                            String newRowKey_server8 = "8"+","+cdnLogs.getChannel_id()+ ","+System.currentTimeMillis();

                            Put new3 = new Put( Bytes.toBytes( newRowKey_status3 ) );
                            new3.setWriteToWAL( false );
                            new3.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "cdnLogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( new3 );

                            Put new4 = new Put( Bytes.toBytes( newRowKey_sn4 ) );
                            new4.setWriteToWAL( false );
                            new4.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "cdnLogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( new4 );

                            Put new5 = new Put( Bytes.toBytes( newRowKey_add5 ) );
                            new5.setWriteToWAL( false );
                            new5.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "cdnLogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( new5 );

                            Put new6 = new Put( Bytes.toBytes( newRowKey_server6 ) );
                            new6.setWriteToWAL( false );
                            new6.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "cdnLogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( new6 );

                            Put new7 = new Put( Bytes.toBytes( newRowKey7_ ) );
                            new7.setWriteToWAL( false );
                            new7.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "cdnLogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( new7 );

                            Put new8 = new Put( Bytes.toBytes( newRowKey_server8 ) );
                            new8.setWriteToWAL( false );
                            new8.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "cdnLogs" ), Bytes.toBytes( newMsg ) );
                            putList.add( new8 );

//                            table.put( putList );
//                            System.out.println( "-------------------------------" );
                            if (putList.size() >= 400000) {
//                                System.out.println( "-------putList size------------" + putList.size() );
                                if(putListrating.size()!=0){
                                    logger.info( putListrating.size()+" rating success" );
                                    tablerating.put( putListrating );
                                    tablerating.flushCommits();
                                    putListrating = new ArrayList<>();
                                }
                                logger.info( putList.size()+" putList success" );
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
                if (tablerating != null) {
                    tablerating.close();
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

    @Test
    public void put() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        Connection conn = ConnectionFactory.createConnection( conf );
        List<Put> putList = new ArrayList<>();

        TableName tname = TableName.valueOf( "ns1:hello" );
        Table table = conn.getTable( tname );
        String rowkey = "1,1,902,171115016825,190.002.145.151,196.064.007.036:11175,2019-04-19 09:55:43,TV6052,";
        long ts1 = System.currentTimeMillis();
        long hashcode1 = (rowkey + ts1).hashCode();
        System.out.println( hashcode1 + "rowkey==" + rowkey );
        Put put = new Put( Bytes.toBytes( rowkey + hashcode1 + "," + ts1 ) );
        byte[] f1 = Bytes.toBytes( "f1" );
        byte[] id = Bytes.toBytes( "sn" );
        byte[] value = Bytes.toBytes( "172225016825" );
        put.addColumn( f1, id, value );
        putList.add( put );

//        table.put( put );
        String rowkey2 = "1,1,902,171115016825,190.002.145.151,196.064.007.036:11175,2019-04-19 09:55:43,TV6052,";
        long ts2 = System.currentTimeMillis();
        long hashcode2 = (rowkey + ts2).hashCode();
        Put put2 = new Put( Bytes.toBytes( rowkey + hashcode2 + "," + ts2 ) );
        byte[] f2 = Bytes.toBytes( "f1" );
        byte[] id2 = Bytes.toBytes( "protocol" );
        byte[] value2 = Bytes.toBytes( "HTTP" );
        put2.addColumn( f2, id2, value2 );
        putList.add( put2 );

        table.put( putList );
        System.out.println( hashcode1 + "-------------------------------" + hashcode2 );

    }


    @Test
    public void get() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        Connection conn = ConnectionFactory.createConnection( conf );
        TableName tname = TableName.valueOf( "ns1:cdnlogtest" );
        Table table = conn.getTable( tname );
        Scan scan = new Scan();
        String startkey = "3"+","+200+",";
        scan.setStartRow( Bytes.toBytes( startkey ) );
        String stopkey = "3"+","+200+"|";
        scan.setStopRow( Bytes.toBytes( stopkey ) );

        ResultScanner rs = table.getScanner( scan );
        Iterator<Result> it = rs.iterator();

        List<CdnLogsBean> list = new ArrayList<>();
        Map<String, CdnLogsBean> mapc = new HashMap<>();
        CdnLogsBean cdnLogsBean = null;

        while (it.hasNext()) {
            Result r = it.next();
            NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = r.getMap();

            for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
                //得到列族
                String f = Bytes.toString( entry.getKey() );
                Map<byte[], NavigableMap<Long, byte[]>> colDataMap = entry.getValue();
                for (Map.Entry<byte[], NavigableMap<Long, byte[]>> ets : colDataMap.entrySet()) {
                    String c = Bytes.toString( ets.getKey() );
                    Map<Long, byte[]> tsValueMap = ets.getValue();
                    for (Map.Entry<Long, byte[]> e : tsValueMap.entrySet()) {
                        Long ts = e.getKey();
                        String value = Bytes.toString( e.getValue() );
                        System.out.println( f + "/" + c + "/" + ts + "=" + value );


                    }
                }
            }
            System.out.println();
//            list.add( cdnLogsBean );
        }
        if (!mapc.isEmpty()) {
            for (CdnLogsBean cdnLogsBean1 : mapc.values()) {
                list.add( cdnLogsBean1 );
            }
        }
        System.out.println( list.size() + "---------cdnLogsBean---------" + list.toString() );
    }


    @Test//分页过滤器,按行输出比如说有五行，pageSize=3，就会输出两页
    public void testPageFilter() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        Connection conn = ConnectionFactory.createConnection( conf );
        TableName tname = TableName.valueOf( "ns1:hello" );
        Table table = conn.getTable( tname );
        String starrow = "1";
        Scan scan = new Scan();
        int newpage = 2;
        int page = 1;
        try {

            byte[] POSTFIX = new byte[]{0x00};//长度为零的字节数组
//            PageFilter filter = new PageFilter(10);//设置一页所含的行数

            //我们要了解分页过滤的机制
            //通过分页过滤，它会保证每页会存在所设置的行数（我们假设为3），也就是说如果把首行设置为一个不存在的行键也没关系
            //这样就会将该行键后的3行放到页中，而如果首行存在，这时就会将首行和该行后面的两行放到一页中
            int totalRows = 0;//总行数
            byte[] lastRow = Bytes.toBytes( starrow );//该页的最后一行
//            int localRows = 0;
            while (true) {
//                scan.setFilter(filter);
                //如果不是第一页
                if (lastRow != null) {
                    // 因为不是第一页，所以我们需要设置其实位置，我们在上一页最后一个行键后面加了一个零，来充当上一页最后一个行键的下一个
                    if (new String( lastRow ).equals( starrow )) {
                        System.out.println( "aaaaaaaaaaaaaa" );
                        scan.setStartRow( lastRow );
                    } else {
                        byte[] startRow = Bytes.add( lastRow, POSTFIX );
                        System.out.println( "start row: " + Bytes.toStringBinary( startRow ) );
                        scan.setStartRow( startRow );
                    }
                    page++;
                }
                ResultScanner scanner = table.getScanner( scan );
                int localRows = 0;
                Result result;
                while ((result = scanner.next()) != null) {
                    System.out.println( localRows++ + ": " + result );
                    totalRows++;
                    lastRow = result.getRow();//获取最后一行的行键
                }
                scanner.close();
                //最后一页，查询结束
                if (localRows == 0)
                    break;
//                if (page-1 == newpage)
//                    break;
            }
            System.out.println( "total rows: " + totalRows );
            table.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test//分页过滤器,按行输出比如说有五行，pageSize=3，就会输出两页
    public void testPageFilter1() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        Connection conn = ConnectionFactory.createConnection( conf );
        TableName tname = TableName.valueOf( "ns1:hello" );
        Table table = conn.getTable( tname );
//        String starrow = "1";
        Scan scan = new Scan();
        List<CdnLogsBean> list = new ArrayList<>();
        Map<String, CdnLogsBean> mapc = new HashMap<>();
        CdnLogsBean cdnLogsBean = null;

        String startkey = "0";
        String stopkey = "3";
        String starrow = startkey;


        int newpage = 2;
        int page = 1;
        PageFilter filter = new PageFilter( 10 );
        try {

            byte[] POSTFIX = new byte[]{0x00};//长度为零的字节数组


            //我们要了解分页过滤的机制
            //通过分页过滤，它会保证每页会存在所设置的行数（我们假设为3），也就是说如果把首行设置为一个不存在的行键也没关系
            //这样就会将该行键后的3行放到页中，而如果首行存在，这时就会将首行和该行后面的两行放到一页中

//            System.out.println("0000000000000000000="+scan.getFilter());
            int totalRows = 0;//总行数
            byte[] lastRow = Bytes.toBytes( starrow );//该页的最后一行
            while (true) {
//                scan.setFilter( filter );
                //如果不是第一页
                if (stopkey.compareTo( new String( lastRow ) ) > 0) {
                    // 因为不是第一页，所以我们需要设置其实位置，我们在上一页最后一个行键后面加了一个零，来充当上一页最后一个行键的下一个
                    if (new String( lastRow ).equals( starrow )) {
                        System.out.println( "aaaaaaaaaaaaaa" );
                        scan.setStartRow( lastRow );
                    } else {
                        byte[] startRow = Bytes.add( lastRow, POSTFIX );
                        System.out.println( "start row: " + Bytes.toStringBinary( startRow ) );
                        scan.setStartRow( startRow );
                    }

                    page++;
                    System.out.println( "page======= " + page );

//                    mapc = new HashMap<>();
                }
                ResultScanner scanner = table.getScanner( scan );
                int localRows = 0;
                Result r;
//                Iterator<Result> it = scanner.iterator();
                while ((r = scanner.next()) != null) {

//                    totalRows++;

                    String rowkeys = Bytes.toString( r.getRow() ).split( "," )[8];
                    if (!mapc.containsKey( rowkeys )) {
                        lastRow = r.getRow();//获取最后一行的行键
                        System.out.println( localRows++ + "------------ " );
                        totalRows++;
                        cdnLogsBean = new CdnLogsBean();
                        mapc.put( rowkeys, cdnLogsBean );
                        if (stopkey.compareTo( new String( lastRow ) ) > 0) {
                            // 因为不是第一页，所以我们需要设置其实位置，我们在上一页最后一个行键后面加了一个零，来充当上一页最后一个行键的下一个
                            if (new String( lastRow ).equals( starrow )) {
                                System.out.println( "aaaaaaaaaaaaaa" );
                                scan.setStartRow( lastRow );
                            } else {
                                byte[] startRow = Bytes.add( lastRow, POSTFIX );
                                System.out.println( "start row: " + Bytes.toStringBinary( startRow ) );
                                scan.setStartRow( startRow );
                            }

                            page++;
                            System.out.println( "page======= " + page );

//                    mapc = new HashMap<>();
                        }
                    }
                    NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = r.getMap();

                    for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
                        //得到列族
                        String f = Bytes.toString( entry.getKey() );
                        Map<byte[], NavigableMap<Long, byte[]>> colDataMap = entry.getValue();
                        for (Map.Entry<byte[], NavigableMap<Long, byte[]>> ets : colDataMap.entrySet()) {
                            String c = Bytes.toString( ets.getKey() );
                            Map<Long, byte[]> tsValueMap = ets.getValue();
                            for (Map.Entry<Long, byte[]> e : tsValueMap.entrySet()) {
                                Long ts = e.getKey();
                                String value = Bytes.toString( e.getValue() );
//                                System.out.print( f + "/" + c + "/" + ts + "=" + value );
                                if ("server_address".equals( c )) {
                                    mapc.get( rowkeys ).setServer_address( value );
                                }
                                if ("address".equals( c )) {
                                    mapc.get( rowkeys ).setAddress( value );
                                }
                                if ("protocol".equals( c )) {
                                    mapc.get( rowkeys ).setProtocol( value );
                                }
                                if ("authinfo".equals( c )) {
                                    mapc.get( rowkeys ).setAuthinfo( value );
                                }
                                if ("sn".equals( c )) {
                                    mapc.get( rowkeys ).setSn( value );
                                }
                                if ("note".equals( c )) {
                                    mapc.get( rowkeys ).setNote( value );
                                }
                                if ("user_agent".equals( c )) {
                                    mapc.get( rowkeys ).setUser_agent( value );
                                }
                                if (!"".equals( c ) && "connect_status".equals( c )) {
                                    mapc.get( rowkeys ).setConnect_status( value );
                                }
                                if ("connect_Times".equals( c )) {
                                    mapc.get( rowkeys ).setConnect_Times( value );
                                }
                                if (!"".equals( c ) && "stream_duration".equals( c )) {
                                    mapc.get( rowkeys ).setStream_duration( value );
                                }
                                if (!"".equals( c ) && "stream_speed".equals( c )) {
                                    mapc.get( rowkeys ).setStream_speed( value );
                                }
                                if (!"".equals( c ) && "stream_total_len".equals( c )) {
                                    mapc.get( rowkeys ).setStream_total_len( value );
                                }
                                if (!"".equals( c ) && "stream_lose_len".equals( c )) {
                                    mapc.get( rowkeys ).setStream_lose_len( value );
                                }
                                if (!"".equals( c ) && "current_Times".equals( c )) {
                                    mapc.get( rowkeys ).setCurrent_Times( value );
                                }
                                if ("channel_id".equals( c )) {
                                    mapc.get( rowkeys ).setChannel_id( value );
                                }


                            }
                        }
                    }

                }

                scanner.close();
                //最后一页，查询结束
                if (localRows == 0) {
                    if (!mapc.isEmpty()) {
                        for (CdnLogsBean cdnLogsBean1 : mapc.values()) {
                            list.add( cdnLogsBean1 );
                        }

                    }
                    break;
                }


//                if (page - 1 == newpage) {
//                    if (!mapc.isEmpty()) {
//                        for (CdnLogsBean cdnLogsBean1 : mapc.values()) {
//                            list.add( cdnLogsBean1 );
//                        }
//                        break;
//                    }
//                }

            }
            double pageindex = Math.ceil( ((double) totalRows) / 10 );
            System.out.println( "pageindex=" + pageindex );
            System.out.println( "total rows: " + totalRows );
            int page1 = 2;
            int j = 0 + (page1 - 1) * 9;
            int m = 10 + (page1 - 1) * 9;
            for (int i = j; i < m; i++) {
                CdnLogsBean cdnLogsBean1 = list.get( i );
            }


            table.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HBaseConsumer consumer = new HBaseConsumer();
        consumer.processLog();
    }

    public static String hexStringToString(String s) {
        if (s == null || s.equals( "" )) {
            return null;
        }
        s = s.replace( " ", "" );
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt( s.substring( i * 2, i * 2 + 2 ), 16 ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String( baKeyword, "UTF-8" );
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    private void tablePut(Table table, CdnLogsBean cdnLogs, String rowkey) throws IOException {
        List<Put> putList = new ArrayList<>();

        HColumnDescriptor hcd = new HColumnDescriptor( Bytes.toBytes( "f1" ) );
        hcd.setCompressionType( Compression.Algorithm.SNAPPY );

        Put putServer_address = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putServer_address.setWriteToWAL( false );
        putServer_address.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "server_address" ), Bytes.toBytes( cdnLogs.getServer_address() ) );
        putList.add( putServer_address );

        Put putAddress = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putAddress.setWriteToWAL( false );
        putAddress.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "address" ), Bytes.toBytes( cdnLogs.getAddress() ) );
        putList.add( putAddress );

        Put putChannel_id = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putChannel_id.setWriteToWAL( false );
        putChannel_id.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "channel_id" ), Bytes.toBytes( cdnLogs.getChannel_id() ) );
        putList.add( putChannel_id );

        Put putAuthinfo = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putAuthinfo.setWriteToWAL( false );
        putAuthinfo.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "authinfo" ), Bytes.toBytes( cdnLogs.getAuthinfo() ) );
        putList.add( putAuthinfo );

        Put putSn = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putSn.setWriteToWAL( false );
        putSn.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "sn" ), Bytes.toBytes( cdnLogs.getSn() ) );
        putList.add( putSn );

        Put putNote = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putNote.setWriteToWAL( false );
        putNote.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "note" ), Bytes.toBytes( cdnLogs.getNote() ) );
        putList.add( putNote );


        Put putUser_agent = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putUser_agent.setWriteToWAL( false );
        putUser_agent.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "user_agent" ), Bytes.toBytes( cdnLogs.getUser_agent() ) );
        putList.add( putUser_agent );

        Put putConnect_status = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putConnect_status.setWriteToWAL( false );
        putConnect_status.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "connect_status" ), Bytes.toBytes( cdnLogs.getConnect_status() ) );
        putList.add( putConnect_status );

        Put putConnect_Times = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putConnect_Times.setWriteToWAL( false );
        putConnect_Times.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "connect_Times" ), Bytes.toBytes( cdnLogs.getConnect_Times() ) );
        putList.add( putConnect_Times );

        Put putStream_duration = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putStream_duration.setWriteToWAL( false );
        putStream_duration.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "stream_duration" ), Bytes.toBytes( cdnLogs.getStream_duration() ) );
        putList.add( putStream_duration );

        Put stream_speed = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        stream_speed.setWriteToWAL( false );
        stream_speed.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "stream_speed" ), Bytes.toBytes( cdnLogs.getStream_speed() ) );
        putList.add( stream_speed );

        Put stream_total_len = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        stream_total_len.setWriteToWAL( false );
        stream_total_len.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "stream_total_len" ), Bytes.toBytes( cdnLogs.getStream_total_len() ) );
        putList.add( stream_total_len );

        Put stream_lose_len = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        stream_lose_len.setWriteToWAL( false );
        stream_lose_len.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "stream_lose_len" ), Bytes.toBytes( cdnLogs.getStream_lose_len() ) );
        putList.add( stream_lose_len );

        Put current_Times = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        current_Times.setWriteToWAL( false );
        current_Times.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "current_Times" ), Bytes.toBytes( cdnLogs.getCurrent_Times() ) );
        putList.add( current_Times );

        Put putProtocol = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
        putProtocol.setWriteToWAL( false );
        putProtocol.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "protocol" ), Bytes.toBytes( cdnLogs.getProtocol() ) );
        putList.add( putProtocol );


        table.put( putList );
    }
}
