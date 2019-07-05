package mocean.logs.hiveutil;

import mocean.logs.domain.CdnLogsBean;
import mocean.logs.util.PageBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.String;
import java.io.IOException;
import java.util.*;


public class HiveConsumer {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    public List<CdnLogsBean> get(PageBean pageBean) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        Connection conn = ConnectionFactory.createConnection( conf );
        TableName tname = TableName.valueOf( "ns1:cdnlog" );
        Table table = conn.getTable( tname );
        Scan scan = new Scan();
        scan.setCaching( 10 );
//        scan.setBatch( 10 );

        logger.info( "pageBean===" + pageBean.toString() );

        //全部默认为空
        String rows = getRowKey.getStartOrStop( scan, pageBean );
        String startkey = rows.split( "=" )[0];
        String stopkey = rows.split( "=" )[1];
        String flag = rows.split( "=" )[2];

        logger.info( "startkey===" + startkey );
        logger.info( "stopkey===" + stopkey );

//        pageBean.getEndTime();

//         startkey = "1,2,901,171115016825,190.002.145.151,196.064.007.036:11175,2019-04-19 09:55:43,TV6052,1556418592078";

        List<CdnLogsBean> list = new ArrayList<>();
        Map<String, CdnLogsBean> mapc = new HashMap<>();
        scan.setStartRow( Bytes.toBytes(startkey) );
        scan.setStopRow( Bytes.toBytes(stopkey  ) );
        int totalRows = 0;
        try {

            ResultScanner scanner = table.getScanner( scan );
            Result r;
//                Iterator<Result> it = scanner.iterator();
            while ((r = scanner.next()) != null) {
//                String rowkeys = Bytes.toString( r.getRow() ).split( "," )[2];
//                if (!mapc.containsKey( rowkeys )) {
                if (list.size() < 500)
                    totalRows++;
                else
                    break;
//                cdnLogsBean = new CdnLogsBean();
//                    mapc.put( rowkeys, cdnLogsBean );

//                }
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

                            if ("cdnLogs".equals( c )) {
                                CdnLogsBean cdnLogsBean = new CdnLogsBean();
                                cdnLogsBean.setServer_address( value.split( "," )[0] );
                                cdnLogsBean.setProtocol( value.split( "," )[1] );
                                cdnLogsBean.setAddress( value.split( "," )[2] );
                                cdnLogsBean.setChannel_id( value.split( "," )[3] );
                                cdnLogsBean.setAuthinfo( value.split( "," )[4] );
                                cdnLogsBean.setSn( value.split( "," )[5] );
                                cdnLogsBean.setNote( value.split( "," )[6] );
                                cdnLogsBean.setUser_agent( value.split( "," )[7] );
                                cdnLogsBean.setConnect_status( value.split( "," )[10] );
                                cdnLogsBean.setConnect_Times( value.split( "," )[12] );
                                cdnLogsBean.setStream_duration( value.split( "," )[13] );
                                cdnLogsBean.setStream_speed( value.split( "," )[14] );
                                cdnLogsBean.setStream_total_len( value.split( "," )[15] );
                                cdnLogsBean.setStream_lose_len( value.split( "," )[16] );
                                cdnLogsBean.setCurrent_Times( value.split( "," )[17] );
                                list.add( cdnLogsBean );
                            }

//                            if ("server_address".equals( c )) {
//                                mapc.get( rowkeys ).setServer_address( value );
//                            }
//                            if ("address".equals( c )) {
//                                mapc.get( rowkeys ).setAddress( value );
//                            }
//                            if ("protocol".equals( c )) {
//                                mapc.get( rowkeys ).setProtocol( value );
//                            }
//                            if ("authinfo".equals( c )) {
//                                mapc.get( rowkeys ).setAuthinfo( value );
//                            }
//                            if ("sn".equals( c )) {
//                                mapc.get( rowkeys ).setSn( value );
//                            }
//                            if ("note".equals( c )) {
//                                mapc.get( rowkeys ).setNote( value );
//                            }
//                            if ("user_agent".equals( c )) {
//                                mapc.get( rowkeys ).setUser_agent( value );
//                            }
//                            if (!"".equals( c ) && "connect_status".equals( c )) {
//                                mapc.get( rowkeys ).setConnect_status( value );
//                            }
//                            if ("connect_Times".equals( c )) {
//                                mapc.get( rowkeys ).setConnect_Times( value );
//                            }
//                            if (!"".equals( c ) && "stream_duration".equals( c )) {
//                                mapc.get( rowkeys ).setStream_duration( value );
//                            }
//                            if (!"".equals( c ) && "stream_speed".equals( c )) {
//                                mapc.get( rowkeys ).setStream_speed( value );
//                            }
//                            if (!"".equals( c ) && "stream_total_len".equals( c )) {
//                                mapc.get( rowkeys ).setStream_total_len( value );
//                            }
//                            if (!"".equals( c ) && "stream_lose_len".equals( c )) {
//                                mapc.get( rowkeys ).setStream_lose_len( value );
//                            }
//                            if (!"".equals( c ) && "current_Times".equals( c )) {
//                                mapc.get( rowkeys ).setCurrent_Times( value );
//                            }
//                            if ("channel_id".equals( c )) {
//                                mapc.get( rowkeys ).setChannel_id( value );
//                            }

                        }
                    }
                }

            }
            scanner.close();
            //最后一页，查询结束
//            if (!mapc.isEmpty()) {
//                for (CdnLogsBean cdnLogsBean1 : mapc.values()) {
//                    list.add( cdnLogsBean1 );
//                }
//
//            }
            table.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

}
