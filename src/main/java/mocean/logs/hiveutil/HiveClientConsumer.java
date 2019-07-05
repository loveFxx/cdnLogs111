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


public class HiveClientConsumer {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    public List<ClientLogsBean> get(PageBean pageBean) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        Connection conn = ConnectionFactory.createConnection( conf );
        TableName tname = TableName.valueOf( "ns1:client" );
        Table table = conn.getTable( tname );
        Scan scan = new Scan();
        scan.setCaching( 10 );
//        scan.setBatch( 10 );

        logger.info( "pageBean===" + pageBean.toString() );

        //全部默认为空
        String rows = getRowKey.getStartOrStopClient( pageBean );
        String startkey = rows.split( "=" )[0];
        String stopkey = rows.split( "=" )[1];
        String flag = rows.split( "=" )[2];

        logger.info( "startkey===" + startkey );
        logger.info( "stopkey===" + stopkey );

        List<ClientLogsBean> list = new ArrayList<>();
        Map<String, CdnLogsBean> mapc = new HashMap<>();
        scan.setStartRow( Bytes.toBytes(startkey) );
        scan.setStopRow( Bytes.toBytes(stopkey  ) );
        int totalRows = 0;
        try {

            ResultScanner scanner = table.getScanner( scan );
            Result r;
//                Iterator<Result> it = scanner.iterator();
            while ((r = scanner.next()) != null) {
                if (list.size() < 50)
                    totalRows++;
                else
                    break;
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
                            if ("clients".equals( c )) {
                                ClientLogsBean clientLogs = new ClientLogsBean();
                                String client =value.split( "#" )[0];
                                String login =value.split( "#" )[1];
                                String program ="";
                                if(value.split( "#" ).length==3){
                                     program =value.split( "#" )[2];
                                }
                                clientLogs.setUserId( client.split( "," )[0] );
                                clientLogs.setSendTime( client.split( "," )[1] );
                                clientLogs.setApkVersion( client.split( "," )[2]);
                                clientLogs.setDevicePlatform( client.split( "," )[3]);
                                clientLogs.setSystemVersion( client.split( "," )[4] );
                                clientLogs.setPid( client.split( "," )[5] );
                                clientLogs.setMid( client.split( "," )[6]);
                                clientLogs.setCid( client.split( "," )[7]);
                                clientLogs.setTotalCpu( client.split( "," )[8] );
                                clientLogs.setFtvCpu( client.split( "," )[9] );
                                clientLogs.setTotalMemory( client.split( "," )[10]);
                                clientLogs.setFreeMemory( client.split( "," )[11]);
                                if (client.split( "," ).length==12){
                                    clientLogs.setFtvMemory("");
                                }else if (client.split( "," ).length>12){
                                    clientLogs.setFtvMemory( client.split( "," )[12]);
                                }
//                                clientLogs.setFtvMemory( client.split( "," )[12]);
                                if (client.split( "," ).length==13){
                                    clientLogs.setPingResult("");
                                }else if (client.split( "," ).length>13) {
                                    clientLogs.setPingResult( client.split( "," )[13]);
                                }

                                List<ClientLogsLoginBean> loginlist = new ArrayList<>(  );
                                if("".equals( login )||null!=login){
                                    int login_len=login.split( "," ).length;
                                    for (int a=0;a<login_len;a=a+5){
                                        ClientLogsLoginBean clientLogsLoginBean = new ClientLogsLoginBean();
                                        clientLogsLoginBean.setStartTime( login.split( "," )[a] );
                                        clientLogsLoginBean.setEndTime( login.split( "," )[a+1]  );
                                        clientLogsLoginBean.setRequestName( login.split( "," )[a+2] );
                                        clientLogsLoginBean.setReturnCode( Integer.parseInt( login.split( "," )[a+3]));
                                        clientLogsLoginBean.setUrl(  login.split( "," )[a+4]);
                                        loginlist.add(clientLogsLoginBean);
                                    }
                                }
                                clientLogs.setLogin( loginlist );

                                List<ClientLogsProgramBean> programlist = new ArrayList<>(  );
                                if(value.split( "#" ).length==3){
                                    int programlen = program.split( "\\$" ).length;

                                    for (int i=0;i<programlen;i=i+1) {
                                        ClientLogsProgramBean  program1= new ClientLogsProgramBean();
                                        program1.setName( program.split( "\\$" )[i].split( "\\|" )[0] );

                                        List<VideoInfoBean> videoList =new ArrayList<>(  );
                                        String videolist ="";
                                        if (program.split( "\\$" )[i].split( "\\|" ).length==2){
                                            videolist = program.split( "\\$" )[i].split( "\\|" )[1];

                                        }
//                                        System.out.println(videolist);
                                        if(program.split( "\\$" )[i].split( "\\|" ).length==2){
                                            int videolen = videolist.split( "," ).length;
                                            for (int j=0;j<videolen;j=j+11){
                                                VideoInfoBean videoInfoBean = new VideoInfoBean();
                                                videoInfoBean.setPlayurl( videolist.split( "," )[j]  );
                                                videoInfoBean.setCdnAddress(   videolist.split( "," )[j+1]);
                                                videoInfoBean.setSpeed(   videolist.split( "," )[j+2]);
                                                videoInfoBean.setResolution(   videolist.split( "," )[j+3]);
                                                videoInfoBean.setVideoFormat(   videolist.split( "," )[j+4]);
                                                videoInfoBean.setAudioFormat(   videolist.split( "," )[j+5]);
                                                videoInfoBean.setStartSwitchTime(   videolist.split( "," )[j+6]);
                                                videoInfoBean.setStartSetDataSourceTime(   videolist.split( "," )[j+7]);
                                                videoInfoBean.setStartPlayTime(   videolist.split( "," )[j+8]);
                                                videoInfoBean.setPlayErrorCode(   videolist.split( "," )[j+9]);
                                                videoInfoBean.setProgramId(  Integer.parseInt(videolist.split( "," )[j+10]));
//                                                System.out.println("videoInfoBean="+videoInfoBean.toString());
                                                videoList.add( videoInfoBean );
                                            }
                                        }
                                        program1.setVideoInfo( videoList );
//                                        System.out.println( program1.getName());
                                        programlist.add( program1 );
                                    }
                                }
                                clientLogs.setProgram( programlist);
                                list.add(clientLogs  );
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
