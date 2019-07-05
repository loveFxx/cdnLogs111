package mocean.logs.hiveutil;

import mocean.logs.util.PageBean;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;
import java.lang.String;


public class getRowKey {

    public static String getStartOrStop(Scan scan, PageBean pageBean) {

        boolean sn_filter = "0".equals( pageBean.getSn_filter() );
        boolean status_filter = "-1".equals( pageBean.getConnect_status() );
        boolean note = "-1".equals( pageBean.getNote() );
        boolean sn = "".equals( pageBean.getSn() );
        boolean server = "".equals( pageBean.getServer_address() );
        boolean address = "".equals( pageBean.getAddress() );
        boolean channel = "".equals( pageBean.getChannel() );
        boolean startTime = null == (pageBean.getStartTime());
        boolean endTime = null == (pageBean.getEndTime());

        System.out.println( "sn_filter=" + sn_filter + " status_filter=" + status_filter + " note=" + note + " sn=" + sn +
                " server=" + server + " address=" + address + " channel=" + channel + " startTime=" + startTime + " endTime=" + endTime );
        //用 原始索引 1或2开头

        String start_sn_filter = sn_filter ? "0" : pageBean.getSn_filter();
        String stop_sn_filter = sn_filter ? "2" : pageBean.getSn_filter();

        String start_status_filter = status_filter ? "0" : pageBean.getConnect_status();
        String stop_status_filter = status_filter ? "2" : pageBean.getConnect_status();

        String start_note = note ? "200" : pageBean.getNote();
        String stop_note = note ? "905" : pageBean.getNote();

        String start_sn = sn ? "000000000000" : pageBean.getSn();
        String stop_sn = sn ? "999999999999" : pageBean.getSn();

        String start_server = server ? "000.000.000.000" : StringUtil.ipFormat( pageBean.getServer_address() );
        String stop_sserver = server ? "999.999.999.999" : StringUtil.ipFormat( pageBean.getServer_address() );

        String start_address = address ? "000.000.000.000:00000" : StringUtil.ipFormatClinet( pageBean.getAddress() );
        String stop_address = address ? "999.999.999.999:99999" : StringUtil.ipFormatClinet( pageBean.getAddress() );

        String start_channel = channel ? "T" : pageBean.getChannel();
        String stop_channel = channel ? "Z" : pageBean.getChannel();

        String start_startTime = startTime ? "1999-01-01 00:00" : pageBean.getStartTime();
        String stop_startTime = startTime ? "1999-01-01 00:00" : pageBean.getStartTime();

        String start_endTime = endTime ? "2999-01-01 00:00" : pageBean.getEndTime();
        String stop_endTime = endTime ? "2999-01-01 00:01" : pageBean.getEndTime();

        String[] keys = new String[3];


        if ("1".equals( pageBean.getSn_filter() )) {
            List<Filter> list = new ArrayList<>();
//            String startkey = 1 + ",";
//            String stopkey = 1 + "," + 3;
            String startkey = "7," + StringUtil.date( 10 ) + ",";
            String stopkey = "7," + StringUtil.datenow() + ",";

            if (!status_filter) {
                startkey = 1 + "," + pageBean.getConnect_status() + "";
                stopkey = 1 + "," + pageBean.getConnect_status() + "," + "|";
//                RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( "," + pageBean.getConnect_status() + "," ) );
//                list.add( filter );
            }
            if (!note) {
                if (!status_filter) {
                    startkey = 1 + "," + pageBean.getConnect_status() + "," + pageBean.getNote() + ",";
                    stopkey = 1 + "," + pageBean.getConnect_status() + "," + pageBean.getNote() + "|";
//                    RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( "," + pageBean.getNote() + "," ) );
//                    list.add( filter );
                    if (!startTime && !endTime) {
                        RowFilter startime = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( "," +pageBean.getStartTime().substring( 0,16 )  ) );
                        list.add( startime );
                    }
                } else {
                    startkey = "3" + "," + pageBean.getNote() + ",";
                    stopkey = "3" + "," + pageBean.getNote() + "|";
                    if (!startTime && !endTime) {
                        startkey = "3" + "," + pageBean.getNote() + "," + pageBean.getStartTime()+ ",";
                        stopkey = "3" + "," + pageBean.getNote() + "," + pageBean.getEndTime()+"|";
                    }
                }
//
            }
            if (!sn) {
                if (status_filter && note) {
                    startkey = "4," + pageBean.getSn() + ",";
                    stopkey = "4," + pageBean.getSn() + "|";
                    if (!startTime && !endTime) {
                        startkey = "4," + pageBean.getSn()+ "," + pageBean.getStartTime()+ ",";
                        stopkey = "4," + pageBean.getSn() + "," + pageBean.getEndTime()+"|";
                    }
                } else {
                    RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( "," + pageBean.getSn() + "," ) );
                    list.add( filter );
                }
            }
            if (!server) {
                if (status_filter && note && sn) {
                    startkey = "5," + pageBean.getServer_address()  + ",";
                    stopkey = "5," +  pageBean.getServer_address()  + "|";
                    if (!startTime && !endTime) {
                        startkey = "5," + pageBean.getServer_address()+ "," + pageBean.getStartTime()+ ",";
                        stopkey = "5," + pageBean.getServer_address() + "," + pageBean.getEndTime()+"|";
                    }
                } else {
                    RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( ","+StringUtil.ipFormat( pageBean.getServer_address() ) +"," ) );
                    list.add( filter );
                }
            }
            if (!address) {
                if (status_filter && note && sn && server) {
                    startkey = "6," +  pageBean.getAddress() + ",";
                    stopkey = "6," +  pageBean.getAddress()  + "|";
                    if (!startTime && !endTime) {
                        startkey = "6," +  pageBean.getAddress()+ "," + pageBean.getStartTime()+ ",";
                        stopkey = "6," +  pageBean.getAddress() + "," + pageBean.getEndTime()+"|";
                    }
                } else {
                    RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( ","+StringUtil.ipFormatClinet( pageBean.getAddress() )+","  ) );
                    list.add( filter );
                }
            }
            if (!channel) {
                if (status_filter && note && sn && server && address) {
                    startkey = "8," + pageBean.getChannel() + ",";
                    stopkey = "8," + pageBean.getChannel() + "|";
                    if (!startTime && !endTime) {
                        startkey = "8," + pageBean.getChannel()+ "," + pageBean.getStartTime()+ ",";
                        stopkey = "8," + pageBean.getChannel() + "," + pageBean.getEndTime()+"|";
                    }
                } else {
                    RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( ","+pageBean.getChannel()+"," ) );
                    list.add( filter );
                }
            }
            if (!startTime && !endTime) {
                if (status_filter && note && sn && server && address && channel) {
                    startkey = "7," + pageBean.getStartTime() + ",";
                    stopkey = "7," + pageBean.getEndTime() + ",";
                }

            }


//            if(status_filter && note && sn && server && address && channel&&startTime &&endTime){
//                RowFilter filter = new RowFilter( CompareFilter.CompareOp.GREATER_OR_EQUAL, new SubstringComparator( "," +StringUtil.datenow().substring( 0,16 )  ) );
//                list.add( filter );
//            }
//            if (startTime && endTime) {
//
//                RowFilter startime = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( "," +StringUtil.date( 1 ).substring( 0,11 )  ) );
////                RowFilter filter = new RowFilter( CompareFilter.CompareOp.GREATER_OR_EQUAL, new SubstringComparator( "," +StringUtil.date( 60 ) +","  ) );
//               // 2019-05-14 12:24:12
////                list.add( filter );
//                list.add( startime );
//
//            }
            if (list.size() != 0) {
                FilterList filterList = new FilterList( FilterList.Operator.MUST_PASS_ALL, list );
                scan.setFilter( filterList );
            }
            keys[2] = "1";
            return startkey + "=" + stopkey + "=" + "1";
        }
        if ("2".equals( pageBean.getSn_filter() )) {
            List<Filter> list = new ArrayList<>();
            String startkey = 2 + ",";
            String stopkey = 2 + "," + 3;

            if (!status_filter) {
                RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( "," + pageBean.getConnect_status() + "," ) );
                list.add( filter );
            }
            if (!note) {
                RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( "," + pageBean.getNote() + "," ) );
                list.add( filter );
            }
            if (!sn) {
                RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( "," + pageBean.getSn() + "," ) );
                list.add( filter );
            }
            if (!server) {
                RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( StringUtil.ipFormat( pageBean.getServer_address() ) ) );
                list.add( filter );
            }
            if (!address) {
                RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( StringUtil.ipFormatClinet( pageBean.getAddress() ) ) );
                list.add( filter );
            }
            if (!channel) {
                RowFilter filter = new RowFilter( CompareFilter.CompareOp.EQUAL, new SubstringComparator( pageBean.getChannel() ) );
                list.add( filter );
            }
            if (!startTime && !endTime) {
                Filter startime = new SingleColumnValueFilter( Bytes.toBytes( "f1" ),
                        Bytes.toBytes( "current_Times" ),
                        CompareFilter.CompareOp.GREATER_OR_EQUAL,
                        Bytes.toBytes( pageBean.getStartTime() ) );
                Filter filter = new SingleColumnValueFilter( Bytes.toBytes( "f1" ),
                        Bytes.toBytes( "current_Times" ),
                        CompareFilter.CompareOp.LESS_OR_EQUAL,
                        Bytes.toBytes( pageBean.getEndTime() ) );
                list.add( filter );
                list.add( startime );

            }
            if (startTime && endTime) {
                RowFilter startime = new RowFilter( CompareFilter.CompareOp.LESS_OR_EQUAL, new SubstringComparator( "," + StringUtil.date( -5 ) + "," ) );
                RowFilter filter = new RowFilter( CompareFilter.CompareOp.GREATER_OR_EQUAL, new SubstringComparator( "," + StringUtil.date( 60 ) + "," ) );
                list.add( filter );
                list.add( startime );

            }
            if (list.size() != 0) {
                FilterList filterList = new FilterList( FilterList.Operator.MUST_PASS_ALL, list );
                scan.setFilter( filterList );
            }
            keys[2] = "sn_filterno";

            return startkey + "=" + stopkey + "=" + "2";

        }
        return "";
    }

    public static String getStartOrStopClient(PageBean pageBean) {

        boolean startTime = "null".equals( pageBean.getStartTime());
        boolean endTime = "null".equals(pageBean.getEndTime());
        if(!"".equals( pageBean.getSn() )){
            String startkey = pageBean.getSn() + ",";
            String stopkey = pageBean.getSn() + "|" ;
//            if(!("null".equals( pageBean.getPid() ))&&!("null".equals( pageBean.getMid()))){
//                startkey = pageBean.getSn() + ","+pageBean.getPid()+","+pageBean.getMid()+","+pageBean.getCid()+",";
//                stopkey = pageBean.getSn() + ","+pageBean.getPid()+","+pageBean.getMid()+","+pageBean.getCid()+"|" ;
//                return startkey + "=" + stopkey + "=" + "2";
//            }
            return startkey + "=" + stopkey ;

        }
        /*if(!"".equals( pageBean.getSn() )&&!"".equals( pageBean.getMid())){
            String startkey = pageBean.getSn() + ","+pageBean.getPid()+","+pageBean.getMid()+","+pageBean.getCid()+",";
            String stopkey = pageBean.getSn() + ","+pageBean.getPid()+","+pageBean.getMid()+","+pageBean.getCid()+"|" ;
            return startkey + "=" + stopkey + "=" + "2";
        }
        if ("".equals( pageBean.getSn() )&&"".equals( pageBean.getMid() )){
            String startkey = StringUtil.dateday( -3 );
            String stopkey =  StringUtil.datenow() + ",";
            return startkey + "=" + stopkey + "=" + "2";
        }*/
        /*System.out.println(null==pageBean.getStartTime()+"====="+startTime);
        if (null!=pageBean.getStartTime()) {
            String startkey =  pageBean.getStartTime()+ ",";
            String stopkey = pageBean.getEndTime()+"|";
            return startkey + "=" + stopkey + "=" + "2";
        }*/
        String startkey = StringUtil.dateday( -1 );
        String stopkey =  StringUtil.datenow() + "|";
        return startkey + "=" + stopkey ;


    }

}
