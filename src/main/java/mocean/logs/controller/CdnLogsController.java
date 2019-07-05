package mocean.logs.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import mocean.logs.common.Common;
import mocean.logs.domain.Audrating;
import mocean.logs.domain.CdnChannelBean;
import mocean.logs.domain.CdnLogsBean;
import mocean.logs.hiveutil.HbaseRatingConsumer;
import mocean.logs.hiveutil.HiveConsumer;
import mocean.logs.hiveutil.StringUtil;

import mocean.logs.service.CdnChannelService;
import mocean.logs.util.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import net.sf.json.JSONObject;

import javax.annotation.Resource;

@Controller
public class CdnLogsController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @RequestMapping("saveCdnLogs")
    @ResponseBody
    public synchronized void saveCdnLogsToKafka(@RequestBody String jsonStrs) {
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
            String topic = "cdnlog108";
            KeyedMessage<String, String> data = new KeyedMessage<String, String>( topic, jsonStrs );
//            List<KeyedMessage<String, String>> messageList = Common.getMessageList();
            producer.send( data );
            arrayList.add( producer );
        } else {
            logger.info( "saveCdnLogs fail...." );
        }
    }

    @RequestMapping("getCdnLogsList")
    @ResponseBody
    public JSONObject getCdnLogsList(int rows, int page, PageBean pageBean) throws Exception {
        logger.info( System.currentTimeMillis() + " getAllCdnLogsList start" );
        long btime = System.currentTimeMillis();
        Page<CdnLogsBean> cdnLogs = new Page<>();
        List<CdnLogsBean> list = new ArrayList();
        list = new HiveConsumer().get( pageBean );
        Collections.sort( list, (CdnLogsBean c1, CdnLogsBean c2) -> c2.getCurrent_Times().compareTo( c1.getCurrent_Times() ) );
        logger.info( "list" + list.size() );
        cdnLogs.setTotal( list.size() );
        logger.info( (System.currentTimeMillis() - btime) / 1000 + " getCdnLogsCount  end" );
        if (cdnLogs.getTotal() > 0) {
            cdnLogs.setRows( list );
        }
        return JSONObject.fromObject( cdnLogs );
    }

    @Test
    public void get() {
        List<CdnLogsBean> list = new ArrayList();
        CdnLogsBean cdnLogsBean = new CdnLogsBean();
        cdnLogsBean = new CdnLogsBean();
        cdnLogsBean.setCurrent_Times( StringUtil.date( 24 ) );
        list.add( cdnLogsBean );
        cdnLogsBean = new CdnLogsBean();
        cdnLogsBean.setCurrent_Times( StringUtil.date( 0 ) );
        list.add( cdnLogsBean );
        for (CdnLogsBean cdnLogsBean1 : list) {
            System.out.println( cdnLogsBean1.getCurrent_Times() );
        }

        Collections.sort( list, (CdnLogsBean c1, CdnLogsBean c2) -> c2.getCurrent_Times().compareTo( c1.getCurrent_Times() ) );
        for (CdnLogsBean cdnLogsBean1 : list) {
//            System.out.println( cdnLogsBean1.getCurrent_Times() );
        }

        String start_time = "2019-08-10 09:56:26";
        String stop_time = "2019-06-10 10:56:26";

        System.out.println( start_time.compareTo( stop_time ) );

    }

    public static void main(String[] args) throws Exception {
        ArrayList<String> mapKey = new ArrayList<>(100);
        for(int i=0;i < 10000000;i++){
            mapKey.add( String.valueOf( i%50 ) );
//            System.out.println("初始化数组总和："+String.valueOf( i%50 ));

        }
        long start= System.currentTimeMillis();
        System.out.println("start="+start);
//        SumTask task = new SumTask(mapKey, 0, mapKey.size());
////        创建一个通用池，这个是jdk1.8提供的功能
//        ForkJoinPool pool = ForkJoinPool.commonPool();
//        Future<ArrayList<String>> future = pool.submit(task); //提交分解的SumTask 任务
//        System.out.println("多线程执行结果："+future.get());
//        pool.shutdown(); //关闭线程池
//        Long aaa = System.currentTimeMillis()-start;
//        System.out.println(start+"=end="+aaa);



    }


}
