package mocean.logs.common;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import mocean.logs.domain.CdnChannelBean;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Common {

    private Common() {

    }

    public static final String CDN_LOGS = "cdn_log";

    private static final List<Map<String, String>> SNLIST = new ArrayList<>();

    public static List<Map<String,String>> getSNLIST(){
        return SNLIST;
    }

    public static final String SEVER_LIST = "server_list";

    public static final String VOD = "vod";

    public static final String LIVE = "live";

    public static final String QUICK_HOUR_COUNT = "quick_hour_count";
    public static final String QUICK_DAY_COUNT = "quick_day_count";
    public static final String QUICK_YESTERDAY_COUNT = "quick_yesterday_count";
    public static final String QUICK_WEEK_COUNT = "quick_week_count";
    public static final String QUICK_MONTH_COUNT = "quick_month_count";
    public static final String QUICK_YEAR_COUNT = "quick_year_count";
    public static final String QUICK_ALL_COUNT = "quick_all_count";

    public static final String ADMIN = "admin";
    public static final String USER = "statistic_user";

    public static final int ADDRESS_LENGTH = 2;
    public static final String HTTP_SCHEME = "http";
    public static final String BROKE = "185.180.221.108:9092"; //192.168.7.200:9092 185.180.221.108:9092
    public static final String ZOOKEEPER = "192.168.7.200:2128";//192.168.7.200:2128 185.180.221.108:2181
    public static final String GROUPID = "test-consumer-group";//test-consumer-group cdnlogs

    public static  List<Producer> listproduce= new ArrayList<>();

    public static List<Producer> getListproduce() {
        return listproduce;
    }

    public static void setListproduce(List<Producer> listproduce) {
        Common.listproduce = listproduce;
    }

    public static final int TOTALSIZE = 10000;

    public static HashMap<String, List> getMap(String s) {
        return map;
    }

    public static void setMap(HashMap<String, List> map) {
        Common.map = map;
    }

    public static HashMap<String,List> map = new HashMap<>(  );

    public static List<KeyedMessage<String, String>> getMessageList() {
        return messageList;
    }

    public static void setMessageList(List<KeyedMessage<String, String>> messageList) {
        Common.messageList = messageList;
    }

    public static List<KeyedMessage<String, String>> messageList = new ArrayList<KeyedMessage<String, String>>();

    public static synchronized ArrayList<Producer<String, String>> getProducerListList() {
        return producerListList;
    }

    public static synchronized void setProducerListList(ArrayList<Producer<String, String>> producerListList) {
        Common.producerListList = producerListList;
    }

    public  static  ArrayList<Producer<String, String>> producerListList = new ArrayList<Producer<String, String>>();

    public static  synchronized ArrayList<Producer<String, String>> getProducerListListclient() {
        return producerListListclient;
    }

    public static synchronized void setProducerListListclient(ArrayList<Producer<String, String>> producerListListclient) {
        Common.producerListListclient = producerListListclient;
    }

    public  static  ArrayList<Producer<String, String>> producerListListclient = new ArrayList<Producer<String, String>>();

    public static long getCount() {
        return count;
    }

    public static void setCount(long count) {
        Common.count = count;
    }

    public static long count;


    public static long getCountcon() {
        return countcon;
    }

    public static void setCountcon(long countcon) {
        Common.countcon = countcon;
    }

    public static long countcon;

    public static Map<String, CdnChannelBean> getMap1() {
        return map1;
    }

    public static void setMap1(Map<String, CdnChannelBean> map1) {
        Common.map1 = map1;
    }

    public static Map<String, CdnChannelBean> map1 = new HashedMap();



}
