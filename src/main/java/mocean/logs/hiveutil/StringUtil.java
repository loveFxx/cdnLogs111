package mocean.logs.hiveutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import mocean.logs.domain.ClientLogsBean;
import mocean.logs.domain.ClientLogsLoginBean;
import mocean.logs.domain.ClientLogsProgramBean;
import mocean.logs.domain.VideoInfoBean;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * |是正则表示特殊字符
     */
    private static final String token = "\\|\\|\\|";

    /**
     * 切割单行日志
     */
    public static String[] splitLog(String log) {
        String[] arr = log.split( token );
        return arr;
    }

    public static String getHostname(String[] arr) {
        return arr[0];
    }

    /**
     * 返回 2017/02/28/12/12
     */
    public static String formatYyyyMmDdHhMi(String[] arr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.US );
            Date d = sdf.parse( arr[3].split( " " )[0] );
            SimpleDateFormat localSDF = new SimpleDateFormat( "yyyy/MM/dd/HH/mm", Locale.US );
            return localSDF.format( d );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void formatYyyyMmDdHhMi1() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            calendar.set( Calendar.MINUTE, 0 );
            String date = fmt.format( calendar.getTime() );
//            System.out.println( date );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String date(int index) {
        try {

            Date date = new Date();//获取当前时间    
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
//        calendar.add(Calendar.YEAR, -1);//当前时间减去一年，即一年前的时间    
            calendar.add(Calendar.MINUTE, -10);//当前时间前去一个月，即一个月前的时间    
//            System.out.println(calendar.getTime());//获取一年前的时间，或者一个月前的时间

//            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
//            calendar.set( Calendar.MINUTE, index );
            String date1 = fmt.format( calendar.getTime() );
            return date1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



//    @Test
    public  static String dateday(int day) {
        try {
            Date date = new Date();//获取当前时间    
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, day);//当前时间前去一个月，即一个月前的时间    
            SimpleDateFormat fmt = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
//            calendar.set( Calendar.MINUTE, index );
            String date1 = fmt.format( calendar.getTime() );
            System.out.println(date1);
            return date1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String datenow() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
//            calendar.set( Calendar.MINUTE, index );
            String date = fmt.format( calendar.getTime() );
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将数组转换成字符串，使用token作为分隔符
     */
    public static String arr2Str(Object[] arr, String token) {
        String str = "";
        for (Object o : arr) {
            str = str + o + token;
        }
        return str.substring( 0, str.length() - 1 );
    }

    /**
     * 将字符串转成日期对象
     */
    public static Date str2Date(String[] arr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( "dd/MMM/yyyy:HH:mm:ss", Locale.US );
            Date d = sdf.parse( arr[3].split( " " )[0] );
            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String ipFormat(String ip) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern( "000" );
        String[] bbb = ip.split( "\\." );
//        System.out.println( bbb[0] + bbb[1] );
        String ipformat = df.format( Integer.parseInt( bbb[0] ) ).toString() + "." +
                df.format( Integer.parseInt( bbb[1] ) ).toString() + "." +
                df.format( Integer.parseInt( bbb[2] ) ).toString() + "." +
                df.format( Integer.parseInt( bbb[3] ) );
        return ipformat;
    }


    public static String ipFormatClinet(String ip) {
//        String ip="90.118.108.160:436";
        if("".equals( ip )){
            return "";
        }
        DecimalFormat dfhost = new DecimalFormat();
        dfhost.applyPattern( "000" );

        DecimalFormat dfport = new DecimalFormat();
        dfport.applyPattern( "00000" );

        String[] hostport = ip.split( ":" );

        String[] bbb = hostport[0].split( "\\." );
        String ipformat = dfhost.format( Integer.parseInt( bbb[0] ) ).toString() + "." +
                dfhost.format( Integer.parseInt( bbb[1] ) ).toString() + "." +
                dfhost.format( Integer.parseInt( bbb[2] ) ).toString() + "." +
                dfhost.format( Integer.parseInt( bbb[3] ) ).toString() + ":" +
                dfport.format( Integer.parseInt( hostport[1] ) ).toString();
        return ipformat;
    }

    @Test
    public void test(){

        String mess ="170118017837,2019-05-31 15:20:51.051,1.0.2102_04302019_common,,4.4.2,74,128,64,25%,0%,984MB,315MB,45MB,#null,null,获取AAA服务器接口,0,null,2019-05-26 02:15:04.004,2019-05-26 02:15:04.004,获取挑战字接口,0,http://185.165.243.207:8660/aaa/ott/getLoginCode?\\u0026userid=170118017837,null,null,激活接口,0,null,2019-05-26 02:15:04.004,2019-05-26 02:15:04.004,登录接口,0,http://185.165.243.207:8660/aaa/ott/login?\\u0026userid=170118017837\\u0026Authenticator=2686b59b683b1a3a60f5a75f2aaf833d97d8fe5042cb5812269e705785fbcf702ecd213a668ced493a03e168c4c8cdb18cd563fbae7dac5894fedf7539ddc007f3a37a26411f59f13c4939c1b6d55466112a54fc96ae70da\\u0026pid=74\\u0026cid=64\\u0026mid=128\\u0026type=AndroidStb,2019-05-26 02:15:04.004,2019-05-26 02:15:04.004,获取首页接口,0,http://185.165.243.207:8290/epgserver/program/index?live=G-IPTV\\u0026usertoken=0000000017011801783720190528211637389\\u0026type=AndroidStb\\u0026cpspid=0#beIN Sport HD 3-AR|http://89.38.97.48:8080/TV4003@2000?AuthInfo=13f4f89006af23189ed9f747a026e46d7dd8313ad3f9dbe52e2329e66a52ab609920ea6e619e1f8ea684e39891e7f2f6a678b164beb446e121c37b41573b4bf03f21255319046aa946b559ab137347d3ba0382f63a14353f,null,null,null,null,null,2019-05-30 08:23:41.041,null,null,null,0,http://89.38.97.48:8080/TV4003@2000?AuthInfo=1af6f31fa81bc9e9988a73c97d7797a77dd8313ad3f9dbe52e2329e66a52ab609920ea6e619e1f8ea684e39891e7f2f6a678b164beb446e121c37b41573b4bf060ca84b692d19b6db1dcba362e652b60ba0382f63a14353f,null,291 kb/s,1920 x 1080, OMX.hisi.video.decoder, aac,2019-05-30 08:23:41.041,2019-05-30 13:26:48.048,2019-05-30 13:26:51.051,null,0,http://185.132.177.120:8080/TV6083?AuthInfo=15936436fa7c1ac06dcda8dac09bd5847dd8313ad3f9dbe52e2329e66a52ab609920ea6e619e1f8ea684e39891e7f2f6a678b164beb446e121c37b41573b4bf0eda219bda5eadbc9890b3a1ed08ae1c8ba0382f63a14353f,null,null,null,null,null,2019-05-31 15:20:51.051,2019-05-31 15:20:51.051,null,-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持,0,http://193.108.117.91:8080/TV6083?AuthInfo=15936436fa7c1ac06dcda8dac09bd5847dd8313ad3f9dbe52e2329e66a52ab609920ea6e619e1f8ea684e39891e7f2f6a678b164beb446e121c37b41573b4bf0eda219bda5eadbc9890b3a1ed08ae1c8ba0382f63a14353f,null,null,null,null,null,2019-05-31 15:20:47.047,2019-05-31 15:20:51.051,null,-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持,0,http://89.38.97.48:8080/TV6083?AuthInfo=15936436fa7c1ac06dcda8dac09bd5847dd8313ad3f9dbe52e2329e66a52ab609920ea6e619e1f8ea684e39891e7f2f6a678b164beb446e121c37b41573b4bf0eda219bda5eadbc9890b3a1ed08ae1c8ba0382f63a14353f,null,null,null,null,null,2019-05-31 15:20:51.051,2019-05-31 15:20:51.051,null,-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持,0";
        String client =mess.split( "#" )[0];
        String login =mess.split( "#" )[1];
        String program ="";
        if(mess.split( "#" ).length==3){
            program =mess.split( "#" )[2];
        }
//        String program =mess.split( "#" )[2];
        System.out.println(client.split( "," ).length);
        System.out.println("client="+client);
        System.out.println("login="+login);
        System.out.println("program="+program);

        ClientLogsBean clientLogs = new ClientLogsBean();
        if(mess.split( "#" ).length==3){
            program =mess.split( "#" )[2];
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
        if(mess.split( "#" ).length==3){
            int programlen = program.split( "\\$" ).length;

            for (int i=0;i<programlen;i=i+1) {
                ClientLogsProgramBean  program1= new ClientLogsProgramBean();
                program1.setName( program.split( "\\$" )[i].split( "\\|" )[0] );

                List<VideoInfoBean> videoList =new ArrayList<>(  );
                String videolist ="";
                if (program.split( "\\$" )[i].split( "\\|" ).length==2){
                    videolist = program.split( "\\$" )[i].split( "\\|" )[1];

                }
                System.out.println(videolist);
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
                        System.out.println("videoInfoBean="+videoInfoBean.toString());
                        videoList.add( videoInfoBean );
                    }
                }
                program1.setVideoInfo( videoList );
//                                        System.out.println( program1.getName());
                programlist.add( program1 );
            }
        }
        clientLogs.setProgram( programlist);
        System.out.println(clientLogs.getProgram().get( 0 ).getVideoInfo().size());
    }

    public static void main(String[] args) {
        String mes ="{\\\"apkVersion\\\":\\\"1.0.2102_04302019_common\\\",\\\"cid\\\":\\\"180\\\",\\\"devicePlatform\\\":\\\"\\\",\\\"freeMemory\\\":\\\"1208MB\\\",\\\"ftvCpu\\\":\\\"0%\\\",\\\"ftvMemory\\\":\\\"55MB\\\",\\\"login\\\":[{\\\"requestName\\\":\\\"获取AAA服务器接口\\\",\\\"returnCode\\\":0},{\\\"endTime\\\":\\\"2019-06-27 17:26:47.047\\\",\\\"requestName\\\":\\\"获取挑战字接口\\\",\\\"returnCode\\\":0,\\\"startTime\\\":\\\"2019-06-27 17:26:46.046\\\",\\\"url\\\":\\\"http://38.91.107.210:8660/aaa/ott/getLoginCode?\\\\u0026userid\\\\u003d181013003543\\\"},{\\\"requestName\\\":\\\"激活接口\\\",\\\"returnCode\\\":0},{\\\"endTime\\\":\\\"2019-06-27 17:26:47.047\\\",\\\"requestName\\\":\\\"登录接口\\\",\\\"returnCode\\\":0,\\\"startTime\\\":\\\"2019-06-27 17:26:47.047\\\",\\\"url\\\":\\\"http://38.91.107.210:8660/aaa/ott/login?\\\\u0026userid\\\\u003d181013003543\\\\u0026Authenticator\\\\u003d3e6d2988221036cae0eb2fdc4250d6906f7df1e26ae60e5a268b2352748b1b41b59398a0f203a573cfbe3226096b2c4e17b349f4a800a884a6a04dc06be8d370ddb498b6c349fcfb80464472110d199ff3f8691cb3ab2cf5\\\\u0026pid\\\\u003d77\\\\u0026cid\\\\u003d180\\\\u0026mid\\\\u003d149\\\\u0026type\\\\u003dAndroidStb\\\"},{\\\"endTime\\\":\\\"2019-06-27 17:26:48.048\\\",\\\"requestName\\\":\\\"获取首页接口\\\",\\\"returnCode\\\":0,\\\"startTime\\\":\\\"2019-06-27 17:26:47.047\\\",\\\"url\\\":\\\"http://38.91.107.210:8290/epgserver/program/index?live\\\\u003dG-IPTV\\\\u0026usertoken\\\\u003d0000000018101300354320190628042647673\\\\u0026type\\\\u003dAndroidStb\\\\u0026cpspid\\\\u003d32\\\"}],\\\"mid\\\":\\\"149\\\",\\\"pid\\\":\\\"77\\\",\\\"pingResult\\\":\\\"\\\",\\\"program\\\":[{\\\"name\\\":\\\"Playboy TV Iberia\\\",\\\"videoInfo\\\":[{\\\"playErrorCode\\\":\\\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\\\",\\\"playUrl\\\":\\\"http://38.91.107.64:8080/TV4749?AuthInfo\\\\u003d653193c316d02843295c7237b3ae86f4ec15eb6e6204134064a5f8da4701b5cadbd0f734be7cba2016e53915412944004862ae67af1585993ce844f1f0359ea9b77f7772efb2a8b4256dda26606ce896cce39eb380f901ea\\\",\\\"startSetDataSourceTime\\\":\\\"2019-06-28 03:25:16.016\\\",\\\"startSwitchTime\\\":\\\"2019-06-28 03:25:12.012\\\"},{\\\"playErrorCode\\\":\\\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\\\",\\\"playUrl\\\":\\\"http://38.91.106.50:8080/TV4749?AuthInfo\\\\u003d653193c316d02843295c7237b3ae86f4ec15eb6e6204134064a5f8da4701b5cadbd0f734be7cba2016e53915412944004862ae67af1585993ce844f1f0359ea9b77f7772efb2a8b4256dda26606ce896cce39eb380f901ea\\\",\\\"startSetDataSourceTime\\\":\\\"2019-06-28 03:25:11.011\\\",\\\"startSwitchTime\\\":\\\"2019-06-28 03:25:11.011\\\"}]}],\\\"sendTime\\\":\\\"2019-06-28 03:25:16.016\\\",\\\"systemVersion\\\":\\\"7.0\\\",\\\"totalCpu\\\":\\\"17%\\\",\\\"totalMemory\\\":\\\"1955MB\\\",\\\"userId\\\":\\\"181013003543\\\"}";
//        String mes ="{\"apkVersion\":\"1.0.1992_03282019_common\",\"cid\":\"181\",\"devicePlatform\":\"\",\"freeMemory\":\"1050MB\",\"ftvCpu\":\"13%\",\"ftvMemory\":\"78MB\",\"login\":[{\"requestName\":\"获取AAA服务器接口\",\"returnCode\":0},{\"endTime\":\"2015-01-01 02:00:35.035\",\"requestName\":\"获取挑战字接口\",\"returnCode\":0,\"startTime\":\"2015-01-01 02:00:34.034\",\"url\":\"http://109.236.80.4:8660/aaa/ott/getLoginCode?\\u0026userid\\u003d181214007765\"},{\"requestName\":\"激活接口\",\"returnCode\":0},{\"endTime\":\"2015-01-01 02:00:36.036\",\"requestName\":\"登录接口\",\"returnCode\":0,\"startTime\":\"2015-01-01 02:00:35.035\",\"url\":\"http://109.236.80.4:8660/aaa/ott/login?\\u0026userid\\u003d181214007765\\u0026Authenticator\\u003d678e2ac70c52e562f34dc4026a74a28b12859bae0cc4f556b7048582066c84da876fb8e84773256c202febf9fde33d55ce40939afce7b679a2e7715208b3fd1a5571a1d4a810a169be263220a6d1fdd9edb038f24f8f888b\\u0026pid\\u003d77\\u0026cid\\u003d181\\u0026mid\\u003d151\\u0026type\\u003dAndroidStb\"},{\"endTime\":\"2015-01-01 02:00:36.036\",\"requestName\":\"获取首页接口\",\"returnCode\":0,\"startTime\":\"2015-01-01 02:00:36.036\",\"url\":\"http://109.236.80.4:8290/epgserver/program/index?live\\u003dG-IPTV\\u0026usertoken\\u003d0000000018121400776520190627005320225\\u0026type\\u003dAndroidStb\\u0026cpspid\\u003d0\"}],\"mid\":\"151\",\"pid\":\"77\",\"pingResult\":\"\",\"program\":[{\"name\":\"OSN Ya Hala HD\",\"videoInfo\":[{\"cdnAddress\":\"212.8.253.31:8082\",\"playUrl\":\"http://193.108.117.91:8080/TV6760?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 20:05:41.041\",\"startSwitchTime\":\"2019-06-26 20:05:41.041\"}]},{\"name\":\"OSN Ya Hala Al Oula HD\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"212.8.253.31:8082\",\"playUrl\":\"http://193.108.117.91:8080/TV6788?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"157 kb/s\",\"startPlayTime\":\"2019-06-26 20:05:38.038\",\"startSetDataSourceTime\":\"2019-06-26 20:05:37.037\",\"startSwitchTime\":\"2019-06-26 20:05:36.036\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"OSN YaHala Cinema HD\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"212.8.253.31:8082\",\"playUrl\":\"http://193.108.117.91:8080/TV6797?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"134 kb/s\",\"startPlayTime\":\"2019-06-26 20:05:43.043\",\"startSetDataSourceTime\":\"2019-06-26 20:05:43.043\",\"startSwitchTime\":\"2019-06-26 20:05:41.041\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"Dorcel TV\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"185.132.177.129:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV9917?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"138 kb/s\",\"startPlayTime\":\"2019-06-26 20:14:29.029\",\"startSetDataSourceTime\":\"2019-06-26 20:14:28.028\",\"startSwitchTime\":\"2019-06-26 20:14:28.028\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"Hustler HD\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.154.164:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV1532?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"114 kb/s\",\"startPlayTime\":\"2019-06-26 20:16:21.021\",\"startSetDataSourceTime\":\"2019-06-26 20:16:20.020\",\"startSwitchTime\":\"2019-06-26 20:16:19.019\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"Hustler TV Europe\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.154.164:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV9920?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"720 x 576\",\"speed\":\"152 kb/s\",\"startPlayTime\":\"2019-06-26 20:07:17.017\",\"startSetDataSourceTime\":\"2019-06-26 20:07:16.016\",\"startSwitchTime\":\"2019-06-26 20:07:16.016\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"PPV 2\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.154.164:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV3682?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"720 x 576\",\"speed\":\"58 kb/s\",\"startPlayTime\":\"2019-06-26 20:08:16.016\",\"startSetDataSourceTime\":\"2019-06-26 20:08:14.014\",\"startSwitchTime\":\"2019-06-26 20:08:13.013\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"Private TV HD\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.154.164:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV9921?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"77 kb/s\",\"startPlayTime\":\"2019-06-26 20:16:34.034\",\"startSetDataSourceTime\":\"2019-06-26 20:16:33.033\",\"startSwitchTime\":\"2019-06-26 20:16:32.032\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"Redlight HD\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"212.8.243.177:8082\",\"playUrl\":\"http://193.108.117.91:8080/TV1921?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"157 kb/s\",\"startPlayTime\":\"2019-06-26 20:09:58.058\",\"startSetDataSourceTime\":\"2019-06-26 20:09:57.057\",\"startSwitchTime\":\"2019-06-26 20:09:56.056\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"},{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.154.164:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV9922?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"134 kb/s\",\"startPlayTime\":\"2019-06-26 20:10:06.006\",\"startSetDataSourceTime\":\"2019-06-26 20:10:05.005\",\"startSwitchTime\":\"2019-06-26 20:10:05.005\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"Studio 66 TV 2\",\"videoInfo\":[{\"playUrl\":\"http://89.38.97.48:8080/TV9905?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 20:10:12.012\"}]},{\"name\":\"Studio 66 TV 3\",\"videoInfo\":[{\"playUrl\":\"http://89.38.97.48:8080/TV9919?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 20:10:20.020\"}]},{\"name\":\"Venus-POR\",\"videoInfo\":[{\"playUrl\":\"http://89.38.97.48:8080/TV9916?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 20:10:29.029\"}]},{\"name\":\"Vivid TV Europe\",\"videoInfo\":[{\"playUrl\":\"http://89.38.97.48:8080/TV9918?AuthInfo\\u003d3d66fac978cf73b621d052a0734daeaa7bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 20:10:36.036\"}]},{\"name\":\"Abu Dhabi Sport 3 HD\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"185.132.134.119:8082\",\"playUrl\":\"http://193.108.117.91:8080/TV7080?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"118 kb/s\",\"startPlayTime\":\"2019-06-26 20:16:58.058\",\"startSetDataSourceTime\":\"2019-06-26 20:16:57.057\",\"startSwitchTime\":\"2019-06-26 20:16:54.054\",\"videoFormat\":\" OMX.hisi.video.decoder.hevc\"}]},{\"name\":\"beIN Sport HD 1-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"185.132.134.119:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV4001@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"42 kb/s\",\"startPlayTime\":\"2019-06-26 20:17:15.015\",\"startSetDataSourceTime\":\"2019-06-26 20:17:12.012\",\"startSwitchTime\":\"2019-06-26 20:17:12.012\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"},{\"cdnAddress\":\"178.132.6.61:8086\",\"playUrl\":\"http://89.38.97.48:9091/TV4001/index.m3u8?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"66 kb/s\",\"startPlayTime\":\"2019-06-26 20:21:27.027\",\"startSetDataSourceTime\":\"2019-06-26 20:21:23.023\",\"startSwitchTime\":\"2019-06-26 20:21:21.021\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 2-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"178.132.6.61:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV4002@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"63 kb/s\",\"startPlayTime\":\"2019-06-26 20:17:22.022\",\"startSetDataSourceTime\":\"2019-06-26 20:17:20.020\",\"startSwitchTime\":\"2019-06-26 20:17:19.019\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"},{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.154.164:8086\",\"playUrl\":\"http://89.38.97.48:9091/TV4002/index.m3u8?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1280 x 720\",\"speed\":\"94 kb/s\",\"startPlayTime\":\"2019-06-26 20:21:43.043\",\"startSetDataSourceTime\":\"2019-06-26 20:21:38.038\",\"startSwitchTime\":\"2019-06-26 20:21:38.038\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 3-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"212.8.243.177:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV4003@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"162 kb/s\",\"startPlayTime\":\"2019-06-26 20:17:29.029\",\"startSetDataSourceTime\":\"2019-06-26 20:17:28.028\",\"startSwitchTime\":\"2019-06-26 20:17:26.026\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 4-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"178.132.6.61:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV4004@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"108 kb/s\",\"startPlayTime\":\"2019-06-26 20:17:39.039\",\"startSetDataSourceTime\":\"2019-06-26 20:17:37.037\",\"startSwitchTime\":\"2019-06-26 20:17:35.035\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 5-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.141.93:8083\",\"playUrl\":\"http://89.38.97.48:8080/TV4005@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"130 kb/s\",\"startPlayTime\":\"2019-06-26 20:17:48.048\",\"startSetDataSourceTime\":\"2019-06-26 20:17:47.047\",\"startSwitchTime\":\"2019-06-26 20:17:46.046\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 6-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"185.180.222.93:8083\",\"playUrl\":\"http://89.38.97.48:8080/TV4006@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"128 kb/s\",\"startPlayTime\":\"2019-06-26 20:17:54.054\",\"startSetDataSourceTime\":\"2019-06-26 20:17:53.053\",\"startSwitchTime\":\"2019-06-26 20:17:51.051\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 7-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.141.93:8083\",\"playUrl\":\"http://89.38.97.48:8080/TV4007@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"143 kb/s\",\"startPlayTime\":\"2019-06-26 20:18:00.000\",\"startSetDataSourceTime\":\"2019-06-26 20:17:59.059\",\"startSwitchTime\":\"2019-06-26 20:17:57.057\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 8-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"190.2.141.93:8083\",\"playUrl\":\"http://89.38.97.48:8080/TV4008@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"148 kb/s\",\"startPlayTime\":\"2019-06-26 20:20:21.021\",\"startSetDataSourceTime\":\"2019-06-26 20:20:19.019\",\"startSwitchTime\":\"2019-06-26 20:20:19.019\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 9-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"178.132.6.61:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV4009@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"63 kb/s\",\"startPlayTime\":\"2019-06-26 20:20:25.025\",\"startSetDataSourceTime\":\"2019-06-26 20:20:23.023\",\"startSwitchTime\":\"2019-06-26 20:20:22.022\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 10-AR\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"178.132.6.61:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV4010@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"162 kb/s\",\"startPlayTime\":\"2019-06-26 20:20:29.029\",\"startSetDataSourceTime\":\"2019-06-26 20:20:28.028\",\"startSwitchTime\":\"2019-06-26 20:20:27.027\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 11-EN\",\"videoInfo\":[{\"audioFormat\":\" mp2\",\"cdnAddress\":\"190.2.154.164:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV4011@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1024 x 576\",\"speed\":\"111 kb/s\",\"startPlayTime\":\"2019-06-26 20:20:33.033\",\"startSetDataSourceTime\":\"2019-06-26 20:20:32.032\",\"startSwitchTime\":\"2019-06-26 20:20:31.031\",\"videoFormat\":\" OMX.hisi.video.decoder.avc\"}]},{\"name\":\"beIN Sport HD 12-EN\",\"videoInfo\":[{\"audioFormat\":\" aac\",\"cdnAddress\":\"178.132.6.61:8082\",\"playUrl\":\"http://89.38.97.48:8080/TV4012@2000?AuthInfo\\u003d3d66fac978cf73b6472679a1e82d78f57bca51b62435a0bb55c4d2f61dad0c40dbd0f734be7cba2016e5391541294400624b24e616e681e1234124294dc7445e4ad1ce683ebc17bff30ac244add2cb02cce39eb380f901ea\",\"resolution\":\"1920 x 1080\",\"speed\":\"119 kb/s\",\"startPlayTime\":\"2019-06-26 20:20:38.038\",\"startSetDataSourceTime\":\"2019-06-26 20:20:36.036\"";
//        String mes = "{\"apkVersion\":\"1.0.2102_04302019_common\",\"cid\":\"183\",\"devicePlatform\":\"\",\"freeMemory\":\"977MB\",\"ftvCpu\":\"33%\",\"ftvMemory\":\"116MB\",\"login\":[{\"requestName\":\"获取AAA服务器接口\",\"returnCode\":0},{\"endTime\":\"2015-01-01 01:00:28.028\",\"requestName\":\"获取挑战字接口\",\"returnCode\":0,\"startTime\":\"2015-01-01 01:00:27.027\",\"url\":\"http://89.39.107.98:8660/aaa/ott/getLoginCode?\\u0026userid\\u003d181109006237\"},{\"requestName\":\"激活接口\",\"returnCode\":0},{\"endTime\":\"2015-01-01 01:00:29.029\",\"requestName\":\"登录接口\",\"returnCode\":0,\"startTime\":\"2015-01-01 01:00:28.028\",\"url\":\"http://89.39.107.98:8660/aaa/ott/login?\\u0026userid\\u003d181109006237\\u0026Authenticator\\u003d0cd795ddd12fcf933b148b1a23a5a96b020bbcf65f8922d35c6536a3d4181f603b7137f24b89839f52c4a9bab373aa404ad01b43d68bf17f7f67e0f74c619c7c9858f67e74212c5bdc57e4b690b765460c9884e32a2c2f41\\u0026pid\\u003d77\\u0026cid\\u003d183\\u0026mid\\u003d151\\u0026type\\u003dAndroidStb\"},{\"endTime\":\"2015-01-01 01:00:30.030\",\"requestName\":\"获取首页接口\",\"returnCode\":0,\"startTime\":\"2015-01-01 01:00:29.029\",\"url\":\"http://89.39.107.98:8290/epgserver/program/index?live\\u003dG-IPTV\\u0026usertoken\\u003d0000000018110900623720190624023633417\\u0026type\\u003dAndroidStb\\u0026cpspid\\u003d0\"}],\"mid\":\"151\",\"pid\":\"77\",\"pingResult\":\"\",\"program\":[{\"name\":\"6ter\",\"videoInfo\":[{\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4a94fd96df9493f9f1abb9e09e33c5beb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c4ce371183e1fccffcce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 19:22:08.008\"},{\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4a94fd96df9493f9f1abb9e09e33c5beb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c4ce371183e1fccffcce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 15:08:09.009\",\"startSwitchTime\":\"2019-06-26 15:08:09.009\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4a94fd96df9493f9f1abb9e09e33c5beb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c4ce371183e1fccffcce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 03:20:06.006\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec5f3ea69d4372b99eb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:11.011\",\"startSwitchTime\":\"2019-06-26 03:20:06.006\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec5f3ea69d4372b99eb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:11.011\",\"startSwitchTime\":\"2019-06-26 03:20:11.011\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec5f3ea69d4372b99eb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:12.012\",\"startSwitchTime\":\"2019-06-26 03:20:12.012\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec8f83a69862388ae5b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:16.016\",\"startSwitchTime\":\"2019-06-26 03:20:12.012\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec8f83a69862388ae5b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:17.017\",\"startSwitchTime\":\"2019-06-26 03:20:17.017\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec8f83a69862388ae5b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:18.018\",\"startSwitchTime\":\"2019-06-26 03:20:17.017\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ecf62826ccbf0684d0b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:22.022\",\"startSwitchTime\":\"2019-06-26 03:20:18.018\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ecf62826ccbf0684d0b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:23.023\",\"startSwitchTime\":\"2019-06-26 03:20:23.023\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ecf62826ccbf0684d0b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 03:20:23.023\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ecdf35f071530731f4b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:28.028\",\"startSwitchTime\":\"2019-06-26 03:20:24.024\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ecdf35f071530731f4b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:28.028\",\"startSwitchTime\":\"2019-06-26 03:20:28.028\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ecdf35f071530731f4b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 03:20:29.029\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec535236e2c99eeebbb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:34.034\",\"startSwitchTime\":\"2019-06-26 03:20:29.029\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec535236e2c99eeebbb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:34.034\",\"startSwitchTime\":\"2019-06-26 03:20:34.034\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec535236e2c99eeebbb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 03:20:35.035\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec5424362b03a00b03b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:39.039\",\"startSwitchTime\":\"2019-06-26 03:20:35.035\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec5424362b03a00b03b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:40.040\",\"startSwitchTime\":\"2019-06-26 03:20:40.040\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec5424362b03a00b03b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:41.041\",\"startSwitchTime\":\"2019-06-26 03:20:41.041\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec8144dc2b9c3e5c41b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:46.046\",\"startSwitchTime\":\"2019-06-26 03:20:41.041\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec8144dc2b9c3e5c41b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:46.046\",\"startSwitchTime\":\"2019-06-26 03:20:46.046\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec8144dc2b9c3e5c41b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:47.047\",\"startSwitchTime\":\"2019-06-26 03:20:47.047\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec034dd2a5ba534dd8b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:51.051\",\"startSwitchTime\":\"2019-06-26 03:20:47.047\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec034dd2a5ba534dd8b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:52.052\",\"startSwitchTime\":\"2019-06-26 03:20:52.052\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec034dd2a5ba534dd8b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 03:20:52.052\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec1a5ef97db68a057bb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:57.057\",\"startSwitchTime\":\"2019-06-26 03:20:53.053\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec1a5ef97db68a057bb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:58.058\",\"startSwitchTime\":\"2019-06-26 03:20:58.058\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec1a5ef97db68a057bb43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:20:58.058\",\"startSwitchTime\":\"2019-06-26 03:20:58.058\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec151f7d2bbd963c71b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:21:03.003\",\"startSwitchTime\":\"2019-06-26 03:20:59.059\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://89.38.97.48:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec151f7d2bbd963c71b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:21:03.003\",\"startSwitchTime\":\"2019-06-26 03:21:03.003\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://185.132.177.120:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec151f7d2bbd963c71b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSwitchTime\":\"2019-06-26 03:21:04.004\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",\"playUrl\":\"http://193.108.117.91:8080/TV2136?AuthInfo\\u003d4bcbfe25724fc8ec2301785b6168c2d4b43199ab49258c46d69658d8395f277cdbd0f734be7cba2016e53915412944005052eff5e0980fc5866b5d8eadfc28247efea62cd278b50c995dfc12c08d9eaecce39eb380f901ea\",\"startSetDataSourceTime\":\"2019-06-26 03:21:09.009\",\"startSwitchTime\":\"2019-06-26 03:21:05.005\"},{\"playErrorCode\":\"-875574520 : 数据连接中断，一般是视频源有问题或者数据格式不支持\",";
//        System.out.println( !mes.contains( "totalMemory" )&&!mes.contains( "totalCpu" ) );
//        System.out.println( mes.replace( "\\", "" ) );
//        System.out.println(mes.length());
        if(mes.contains( "\\" )){
            mes = mes.replace( "\\", "" );
//            mes = mes.replaceAll( "\\","" );
        }
        ClientLogsBean clientLogs = JSON.parseObject( mes,ClientLogsBean.class);
        System.out.println(clientLogs.toString());
//        System.out.println( mes.lastIndexOf( "},{" ) );
//        mes= mes.substring( 0, mes.lastIndexOf( "},{" ) );
//        System.out.println( mes.endsWith( "}]" ) );
//        System.out.println( mes );
//        System.out.println(mes.length());
//        System.out.println(mes+"}]}]}");
//        ClientLogsBean clientLogs = JSON.parseObject( mes+"]}]}", new TypeReference<ClientLogsBean>() {
//        } );
//        System.out.println( clientLogs.toString() );
//        System.out.println(mes.endsWith( "}]}]}" ));

        /*int meslength = msg.length / (8 * 1024);
        if(meslength==0||meslength==1){
            clientLogs = JSON.parseObject( log, new TypeReference<ClientLogsBean>() {
            } );
        } else {
            log = log.substring( 0, log.lastIndexOf( "},{" ) );
            logger.info( "====msglengthsubstring====" +msg.length/(8*1024)+"KB=="+log );
            if (log.endsWith( "}]" )) {
                log = log + "}]}\n";
            } else {
                log = log + "}]}]}\n";
            }
            logger.info( "====msglength====" +msg.length/(8*1024)+"KB=="+log );
            clientLogs = JSON.parseObject( log, ClientLogsBean.class );
        }*/

    }


}



