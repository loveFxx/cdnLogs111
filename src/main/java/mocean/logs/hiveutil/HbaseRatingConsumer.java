package mocean.logs.hiveutil;

import mocean.logs.domain.*;
import mocean.logs.util.PageBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class HbaseRatingConsumer {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    public ArrayList<Audrating> get(PageBean pageBean) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        Connection conn = ConnectionFactory.createConnection( conf );
        TableName tname = TableName.valueOf( "ns1:rating" );
        Table table = conn.getTable( tname );
        Scan scan = new Scan();
        scan.setCaching( 10 );
        logger.info( "pageBean===" + pageBean.toString() );

//        String rows = getRowKey.getStartOrStopClient( scan, pageBean );
        String startkey = "TV";
        String stopkey = "TV|";

        logger.info( "startkey===" + startkey );
        logger.info( "stopkey===" + stopkey );

        ArrayList<Audrating> list = new ArrayList<>();
        Map<String, CdnLogsBean> mapc = new HashMap<>();
        scan.setStartRow( Bytes.toBytes( startkey ) );
        scan.setStopRow( Bytes.toBytes( stopkey ) );
        try {

            ResultScanner scanner = table.getScanner( scan );
            Result r;
//                Iterator<Result> it = scanner.iterator();
            while ((r = scanner.next()) != null) {
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
//                            System.out.print( f + "/" + c + "/" + ts + "=" + value );
//                            logger.info("value==="+value );
//                            logger.info("==================");
                            if ("rating".equals( c )) {
                                Audrating audrating = new Audrating();
                                audrating.setChannel_id( value.split( "," )[0] );
                                audrating.setSn( value.split( "," )[1] );
                                audrating.setConnect_Times( value.split( "," )[2] );
                                audrating.setCurrent_Times( value.split( "," )[3] );
                                audrating.setStream_duration( value.split( "," )[4] );
                                list.add( audrating );

                            }
                        }
                    }

                }
            }
            scanner.close();
            table.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }
}
