package mocean.logs.hiveutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.producer.KeyedMessage;
import mocean.logs.common.Common;
import mocean.logs.domain.*;
import mocean.logs.util.DateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//import mocean.logs.controller.CdnLogsBean;

/**
 * 原生数据消费者
 */
public class HBaseClientConsumer {
    private final ConsumerConnector consumerConn;
    private final String topic_hive = "clienttest";
    private Logger logger = LoggerFactory.getLogger( this.getClass() );
    final int a_numThreads = 3;

    public HBaseClientConsumer() {
        Properties props = new Properties();
        props.put( "zookeeper.connect", "185.180.221.108:2181" );
        props.put( "group.id", "cdnlogss" );//test-consumer-group
        props.put( "auto.offset.reset", "largest" );
        props.put( "session.timeout.ms", "30000" );
        props.put( "zookeeper.sync.time.ms", "2500" );
        props.put( "enable.auto.commit", "true" );
        props.put( "max.poll.records", "50" );
        props.put( "auto.commit.interval.ms", "15000" );
        props.put( "fetch.message.max.bytes", "214748357" );//2147483571 1048576000
        props.put( "fetch.max.bytes", "214748357" );//2147483571 1048576000
        props.put( "receive.buffer.bytes", "102400" );//2147483571 1048576000
        props.put( "send.buffer.bytes", "102400" );//2147483571 1048576000
        props.put( "max.partition.fetch.bytes", "214748357" );//2147483571 1048576000

        // 创建消费者连接器
        consumerConn = Consumer.createJavaConsumerConnector( new ConsumerConfig( props ) );
    }

    /**
     * 处理log
     */
    public void processLog() {
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
            conf.set( "hbase.client.keyvalue.maxsize", "209715200" );//200M
            Connection conn = null;
            HTable table = null;
            try {
                conn = ConnectionFactory.createConnection( conf );
                TableName tname = TableName.valueOf( "ns1:client" );
                table = (HTable) conn.getTable( tname );
//             table.setWriteBufferSize( 1 * 1024 * 1024 );
                table.setAutoFlush( false );

            } catch (Exception e) {
                e.printStackTrace();
            }

            for (final KafkaStream stream : streams) {
                //
                ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
                //迭代日志消息

                while (consumerIte.hasNext()) {
                    byte[] msg = consumerIte.next().message();
                    String log = new String( msg );
//                    if (log.contains( "\\" )) {
//                        log = log.replace( "\\", "" );
//                    }
//                    ClientLogsBean clientLogs;
                    try {
                        if((msg.length/(8*1024)==0)||(msg.length/(8*1024)==1)){
//                            logger.info( "====msglength1====" +msg.length/(8*1024)+"KB==" );
                        }else {
                            log = log.substring( 0, log.lastIndexOf( "},{" ) );
                            if (log.endsWith( "}]" )) {
                                log = log + "}]}";
                            } else {
                                log = log + "}]}]}";
                            }
//                            logger.info( "====msglength====" +msg.length/(8*1024)+"KB==" );
//                            clientLogs = JSON.parseObject( log, ClientLogsBean.class );
                        }
//                        ClientLogsBean clientLogs = JSON.parseObject( log, new TypeReference<ClientLogsBean>() {
//                            } );
                        if(log.contains( "\\" )){
                            log = log.replace( "\\", "" );
                        }

                        ClientLogsBean clientLogs = JSON.parseObject( log,ClientLogsBean.class);
//                        logger.info( "====clientLogs====" +clientLogs.getUserId() );

                        if (whenCpuOutOfRange( clientLogs ) || whenMemoryOutOfRange( clientLogs ) || whenVideoPlayError( clientLogs ) || whenLoginInfoError( clientLogs )) {
                            clientLogs.setIsException( -1 );
                        }
                        List<ClientLogsLoginBean> loginlist = clientLogs.getLogin();
                        List<ClientLogsProgramBean> programlist = clientLogs.getProgram();

                        String newMsg  = clientLogs.getUserId() + "," + clientLogs.getSendTime() + "," +
                                clientLogs.getApkVersion() + "," + clientLogs.getDevicePlatform() + "," +
                                clientLogs.getSystemVersion() + "," + clientLogs.getPid() + "," +
                                clientLogs.getMid() + "," + clientLogs.getCid() + "," +
                                clientLogs.getTotalCpu() + "," + clientLogs.getFtvCpu() + "," +
                                clientLogs.getTotalMemory() + "," + clientLogs.getFreeMemory() + "," +
                                clientLogs.getFtvMemory() + "," + clientLogs.getPingResult() + "#";
                        String loginMsg = "";

                        if (loginlist.size() != 0) {
                            for (ClientLogsLoginBean login : loginlist) {
                                loginMsg = loginMsg + login.getStartTime() + "," + login.getEndTime() + ","
                                        + login.getRequestName() + "," + login.getReturnCode() + "," +
                                        login.getUrl() + ",";
                            }
                            loginMsg = loginMsg.substring( 0, loginMsg.length() - 1 );
                        }

                        loginMsg = loginMsg + "#";

                        String programMsg = "";
//                        String video="";
                        if (programlist.size() != 0) {
                            for (ClientLogsProgramBean program : programlist) {
                                programMsg = programMsg + program.getName() + "|";
                                String video = "";
                                List<VideoInfoBean> videoList = program.getVideoInfo();
                                if (videoList != null) {
                                    for (VideoInfoBean videoInfoBean : videoList) {
                                        video = video + videoInfoBean.toString() + ",";
                                    }
                                    video = video.substring( 0, video.length() - 1 );
                                }
                                programMsg = programMsg + video + "$";
                            }
                            programMsg = programMsg.substring( 0, programMsg.length() - 1 );
                        }
                        newMsg = newMsg + loginMsg + programMsg;

                        String rowkey = clientLogs.getUserId() + "," + clientLogs.getPid() + "," +
                                clientLogs.getMid() + "," + clientLogs.getCid() + ",";
//                        logger.info( "newMsg  " + newMsg );
//                        logger.info( "rowkeyclient  " + rowkey );
                        //存放数据
                        Put putcdnLogs = new Put( Bytes.toBytes( rowkey + System.currentTimeMillis() ) );
                        putcdnLogs.setWriteToWAL( false );
                        putcdnLogs.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "clients" ), Bytes.toBytes( newMsg ) );
                        putList.add( putcdnLogs );


                        String newRowKey_status3 = clientLogs.getSendTime() + "," + System.currentTimeMillis();
//
                        Put new3 = new Put( Bytes.toBytes( newRowKey_status3 ) );
                        new3.setWriteToWAL( false );
                        new3.addColumn( Bytes.toBytes( "f1" ), Bytes.toBytes( "clients" ), Bytes.toBytes( newMsg ) );
                        putList.add( new3 );

                        if (putList.size() >= 1000) {
                            logger.info( "client success " + putList.size() );
                            table.put( putList );
                            table.flushCommits();
                            putList = new ArrayList<>();

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
            } finally {
                consumerConn.commitOffsets();
                System.out.println( "-------------------------------" + Common.getCountcon() );
            }


        }

    }

    @Test
    public void getClient() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        Connection conn = ConnectionFactory.createConnection( conf );
        TableName tname = TableName.valueOf( "ns1:client" );
        Table table = conn.getTable( tname );
        Scan scan = new Scan();
        String startkey = "180123015216,";
        scan.setStartRow( Bytes.toBytes( startkey ) );
        String stopkey = "180123015216|";
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

    private boolean whenCpuOutOfRange(ClientLogsBean clientLogs) {
        int ftvCpu = Integer.valueOf( clientLogs.getFtvCpu().split( "%" )[0] );
        return ftvCpu > 10;
    }

    private boolean whenMemoryOutOfRange(ClientLogsBean clientLogs) {
        if ("".equals( clientLogs.getFtvMemory().split( "M" )[0] )) {
            return 0 > 150;
        }
        int ftvMemory = Integer.valueOf( clientLogs.getFtvMemory().split( "M" )[0] );
        return ftvMemory > 150;
    }

    private void whenLogsIsFeedback(ClientLogsBean clientLogs) {
        if (StringUtils.isNotBlank( clientLogs.getPingResult() )) {
            long time = System.currentTimeMillis();
            logger.info( "" + time );
            Iterator<Map<String, String>> it = Common.getSNLIST().iterator();
            while (it.hasNext()) {
                Map<String, String> tempMap = it.next();
                if (clientLogs.getUserId().equals( tempMap.get( "sn" ) )) {
                    logger.info( clientLogs.getUserId() );
                    long interval = Long.valueOf( tempMap.get( "interval" ) );
                    long current = Long.valueOf( tempMap.get( "timestamp" ) );
                    if (time - current >= interval) {
                        it.remove();
                        logger.info( clientLogs.getUserId() + "日志信息已上传 " + new Date() );
                    }
                }
            }
        }
    }

    private boolean whenVideoPlayError(ClientLogsBean clientLogs) {
        if (clientLogs.getProgram() == null) {
            return false;
        }
        for (ClientLogsProgramBean program : clientLogs.getProgram()) {
            if (program.getVideoInfo() == null) {
                continue;
            }
            for (VideoInfoBean videoInfo : program.getVideoInfo()) {
                if (StringUtils.isNotBlank( videoInfo.getPlayErrorCode() )
                        && videoInfo.getPlayErrorCode().split( ":" ).length > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean whenLoginInfoError(ClientLogsBean clientLogs) {
        if(clientLogs.getLogin()==null){
            return false;
        }
        for (ClientLogsLoginBean login : clientLogs.getLogin()) {
            if (StringUtils.isNotBlank( login.getUrl() ) && StringUtils.isNotBlank( login.getStartTime() )
                    && StringUtils.isNotBlank( login.getEndTime() ) && login.getReturnCode() != 0) {
                return true;
            }
        }
        return false;
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
        String startkey = "3" + "," + 200 + ",";
        scan.setStartRow( Bytes.toBytes( startkey ) );
        String stopkey = "3" + "," + 200 + "|";
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
        HBaseClientConsumer consumer = new HBaseClientConsumer();
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
