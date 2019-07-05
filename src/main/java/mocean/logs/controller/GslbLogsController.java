package mocean.logs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONObject;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import mocean.logs.common.Common;
import mocean.logs.domain.GslbLogsBean;
import mocean.logs.service.HBaseGslbSparkQuery;
import mocean.logs.util.DateUtil;
import mocean.logs.util.Page;
import mocean.logs.util.PageBean;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Controller
public class GslbLogsController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @RequestMapping("saveGslbLogs")
    @ResponseBody
    public synchronized void saveGslbLogsToKafka(@RequestBody String jsonStrs) {
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
            logger.info( "saveGslbLogs--jsonStrs"+jsonStrs );
            String topic = "gslblogs";
            if(jsonStrs.contains( "\\" )){
                jsonStrs = jsonStrs.replace( "\\","" );
                KeyedMessage<String, String> data = new KeyedMessage<String, String>( topic, jsonStrs );
                producer.send( data );
                arrayList.add( producer );
            } else {
                KeyedMessage<String, String> data = new KeyedMessage<String, String>( topic, jsonStrs );
                producer.send( data );
                arrayList.add( producer );
            }
        } else {
//            logger.info( "saveCdnLogs fail...." );
        }
    }

    @RequestMapping("getGslbLogsList")
    @ResponseBody
    public net.sf.json.JSONObject getGslbLogsList(int rows, int page, PageBean pageBean) throws Exception {
        logger.info( System.currentTimeMillis() + " getAllGslbLogsList start" );
        long btime = System.currentTimeMillis();
        Page<GslbLogsBean> cdnLogs = new Page<>();
        ArrayList<GslbLogsBean> list = new ArrayList();
        list = new HBaseGslbSparkQuery().start(pageBean);
        Collections.sort( list, (GslbLogsBean c1, GslbLogsBean c2) -> c2.getCurrent_time().compareTo( c1.getCurrent_time() ) );
        logger.info( "list" + list.size() );
        cdnLogs.setTotal( list.size() );
        logger.info( (System.currentTimeMillis() - btime) / 1000 + " getCdnLogsCount  end" );
        if (cdnLogs.getTotal() > 0) {
            cdnLogs.setRows( list );
        }
        return JSONObject.fromObject( cdnLogs );
    }


    @Test
    public void test(){
        String jsonStrs ="{\\\"server\\\":\\\"221.4.223.101\\\",\\\"clients\\\":[{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:54720\\\",\\\"note\\\":\\\"2\\\",\\\"user_agent\\\":\\\"\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530956},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:59740\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:59744\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:59746\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:59750\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:59752\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:59756\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:59942\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:59976\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958},{\\\"procotol\\\":\\\"HTTP\\\",\\\"address\\\":\\\"119.29.16.130:60004\\\",\\\"note\\\":\\\"903\\\",\\\"user_agent\\\":\\\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\\\",\\\"redirect_url\\\":\\\"\\\",\\\"current_time\\\":1561530958}]}";
        jsonStrs = jsonStrs.replace( "\\","" );
        System.out.println(jsonStrs);
//        String jsonStrs ="{\"server\":\"221.4.223.101\",\"clients\":[{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:54720\",\"note\":\"2\",\"user_agent\":\"\",\"redirect_url\":\"\",\"current_time\":1561530956},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:59740\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:59744\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:59746\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:59750\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:59752\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:59756\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:59942\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:59976\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958},{\"procotol\":\"HTTP\",\"address\":\"119.29.16.130:60004\",\"note\":\"903\",\"user_agent\":\"Mozilla/5.0 (Windows; U; Windows NT 6.0;en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6)\",\"redirect_url\":\"\",\"current_time\":1561530958}]}";
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject( jsonStrs );
        JSONArray jsonArray = jsonObject.getJSONArray( "clients" );
        String server = jsonObject.getString( "server" );
        List<GslbLogsBean> list = JSON.parseArray( jsonArray.toJSONString(), GslbLogsBean.class );
        for (GslbLogsBean gs: list) {
            gs.setServer( server );
            gs.setCurrent_time( DateUtil.getTransTimeByTimeStamp( Long.valueOf( gs.getCurrent_time() ) * 1000 ) );
            System.out.println("-----"+gs.toString());
        }


    }

}
