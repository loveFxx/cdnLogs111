package mocean.logs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import mocean.logs.common.Common;
import mocean.logs.domain.*;
import mocean.logs.domain.CdnLogsBean;
import mocean.logs.hiveutil.HiveClientConsumer;
import mocean.logs.hiveutil.HiveConsumer;
import mocean.logs.hiveutil.StringUtil;
import mocean.logs.service.HBaseClientSparkQuery;
import mocean.logs.util.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ClientLogsController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    private List<ClientLogsBean> list = new ArrayList();
    private List<ClientLogsProgramBean> clientLogsProgramBeanList = new ArrayList<>();
    private List<ClientLogsLoginBean> loginBeanListList = new ArrayList<>();

//

    @RequestMapping("saveClientLogs")
    @ResponseBody
    public synchronized void saveClientLogs(@RequestBody String jsonStrs) {
        Producer<String, String> producer;
        ArrayList<Producer<String, String>> arrayList = Common.getProducerListList();

        if (arrayList.size() > 0) {
            producer = arrayList.get( 0 );
            arrayList.remove( 0 );
        } else {
            Properties props = new Properties();
            props.put( "metadata.broker.list", "185.180.221.108:9092" );
            props.put( "serializer.class", "kafka.serializer.StringEncoder" );
            props.put( "request.required.acks", "1" );
            producer = new Producer<String, String>( new ProducerConfig( props ) );
        }

        if (StringUtils.isNotBlank( jsonStrs )) {
            if(jsonStrs.contains( "760724206@qq.com" )){
                logger.info( "clienttestsend==="+jsonStrs );
            }
            if(jsonStrs.contains( "f52d2c4488a97" )){
                logger.info( "clienttestsendf52d2c4488a97==="+jsonStrs );
            }
            String topic = "clienttest";
            if (jsonStrs.contains( "\\" )) {
                jsonStrs = jsonStrs.replace( "\\", "" );
                KeyedMessage<String, String> data = new KeyedMessage<String, String>( topic, jsonStrs );
                producer.send( data );
                arrayList.add( producer );
            } else {
                KeyedMessage<String, String> data = new KeyedMessage<String, String>( topic, jsonStrs );
                producer.send( data );
//            producer.close();
                arrayList.add( producer );
            }
        } else {
            logger.info( "上传了空数据！！！" );
        }
    }


    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Hello World!";
    }


    @RequestMapping("findClientLogs")
    @ResponseBody
    public JSONObject findClientLogs(int rows, int page, PageBean pageBean) throws Exception {
        Page<ClientLogsBean> cdnLogs = new Page<>();
        list = new ArrayList();

//        list = new HiveClientConsumer().get( pageBean );
        list = new HBaseClientSparkQuery().start( pageBean );
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss SSS" );
        logger.info( dateFormat.format( new Date() ) + " findClientLogs Result " + list.size() );
//        list=list.subList( 0,50 );
        cdnLogs.setRows( list );
        cdnLogs.setTotal( list.size() );
        for (int i = 0; i < list.size(); i++) {
            list.get( i ).setId( i );
        }
        logger.info( "findClientLogs success " + list.size() );
        return JSONObject.fromObject( cdnLogs );
    }

    @RequestMapping("getProgramList")
    @ResponseBody
    public JSONObject getProgramList(int id) {

        Page<ClientLogsProgramBean> programBean = new Page<>();
        clientLogsProgramBeanList = list.get( id ).getProgram();
        for (int i = 0; i < clientLogsProgramBeanList.size(); i++) {
            clientLogsProgramBeanList.get( i ).setId( i );
            clientLogsProgramBeanList.get( i ).setLogsid( i );
        }
        programBean.setTotal( clientLogsProgramBeanList.size() );
        programBean.setRows( clientLogsProgramBeanList );
        logger.info( clientLogsProgramBeanList.size() + " hang getProgramList success " + id );
        return JSONObject.fromObject( programBean );
    }

    @RequestMapping("getVideoInfoList")
    @ResponseBody
    public JSONObject getVideoInfoList(int id) {
        Page<VideoInfoBean> pageBean = new Page<>();
        List<VideoInfoBean> videoInfoBeanList = clientLogsProgramBeanList.get( id ).getVideoInfo();
        pageBean.setTotal( videoInfoBeanList.size() );
        pageBean.setRows( videoInfoBeanList );
        logger.info( videoInfoBeanList.size() + " hang getVideoInfoList success " + id );
        return JSONObject.fromObject( pageBean );
    }

    @RequestMapping("getLoginList")
    @ResponseBody
    public JSONObject getLoginList(int id) {
        Page<ClientLogsLoginBean> loginBean = new Page<>();
        loginBeanListList = list.get( id ).getLogin();
        loginBean.setTotal( loginBeanListList.size() );
        loginBean.setRows( loginBeanListList );
        logger.info( " hang getLoginList success " + id );
        return JSONObject.fromObject( loginBean );
    }

    @RequestMapping("getNoCheat")
    @ResponseBody
    public String getNoCheat(String sn, int times, int frequency) {
        ReturnModel returnModel = new ReturnModel();
        returnModel.setReturnCode( "0" );
        returnModel.setReturnMessage( "链接失败，未成功发送请求" );
        String result = null;
        try {
            String message = "{\"freq\" : " + frequency + ",\"totalCount\" : " + times + "}";
            String base64String1 = new AES().encryptToBase64String( message, sn );
            logger.info( base64String1 );
            String url = "http://69.197.169.122:8960/nocheat?userid=" + sn + "&message=" + base64String1;
            result = sendGet( url );
            Map<String, String> map = new HashMap<>();
            long interval = Long.valueOf( frequency ) * 1000 + 15000;
            long timestamp = System.currentTimeMillis();
            map.put( "sn", sn );
            map.put( "timestamp", String.valueOf( timestamp ) );
            map.put( "interval", String.valueOf( interval ) );
            Common.getSNLIST().add( map );
            Thread.sleep( interval );
            if (Common.getSNLIST().contains( map )) {
                returnModel.setReturnMessage(
                        "自" + DateUtil.getTransTimeByTimeStamp( timestamp ) + "提交的sn:" + sn + "链接失败，当前客户端不在线或超时！！！" );
                Iterator<Map<String, String>> it = Common.getSNLIST().iterator();
                while (it.hasNext()) {
                    Map<String, String> tempMap = it.next();
                    if (tempMap.equals( map )) {
                        it.remove();
                    }
                }
            } else {
                returnModel.setReturnCode( "1" );
                returnModel.setReturnMessage(
                        "自" + DateUtil.getTransTimeByTimeStamp( timestamp ) + "提交的sn:" + sn + "链接成功，日志信息已上传" );
            }
        } catch (Exception e) {
            logger.info( result );
            return JSON.toJSONString( returnModel );
        }
        return JSON.toJSONString( returnModel );
    }

    private String sendGet(String url) {
        StringBuffer result = new StringBuffer();
        BufferedReader in = null;
        try {
            URL realUrl = new URL( url );
            logger.info( url );
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty( "accept", "*/*" );
            connection.setRequestProperty( "connection", "Keep-Alive" );
            connection.setRequestProperty( "user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)" );
            connection.connect();
            connection.setConnectTimeout( 30000 );
            in = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;
            while ((line = in.readLine()) != null) {
                result.append( line );
            }
        } catch (Exception e) {
            logger.info( "set GET request error！" + e + url );
            result = new StringBuffer();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            }
        }
        return result.toString();
    }


}
