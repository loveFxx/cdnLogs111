package mocean.logs.controller;

import mocean.logs.common.Common;
import mocean.logs.domain.Audrating;
import mocean.logs.domain.CdnChannelBean;
import mocean.logs.hiveutil.HbaseRatingConsumer;
import mocean.logs.service.CdnChannelService;
//import mocean.logs.service.HBaseSparkQuery;
import mocean.logs.service.HBaseSparkQuery;
import mocean.logs.util.*;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import scala.Tuple2;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Controller
public class RatingController {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );


    @Resource
    private CdnChannelService cdnChannelService;

    private Map<String, CdnChannelBean> map1 = new HashedMap();

    /**
     * 使用spark处理，搜索hbase和处理逻辑用时10s左右，主要就是搜索hbase时间
     * @param rows
     * @param page
     * @param pageBean
     * @return
     * @throws Exception
     */
    @RequestMapping("getRating")
    @ResponseBody
    public JSONObject getRatingSpark(int rows, int page, PageBean pageBean) throws Exception {

        boolean IsStart = false;
        if (null != pageBean.getStartTime() && pageBean.getStartTime().length() > 3) {
            IsStart = true;
        }
        String startTime = pageBean.getStartTime();
        String stopTime = pageBean.getEndTime();

        Page<Audrating> cdnLogs = new Page<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss SSS" );
        ArrayList<Audrating> list = new ArrayList();
        logger.info( dateFormat.format( new Date() ) + " getRating start " );
        Map<String, Map<String, Long>> mapss = new HBaseSparkQuery().start( IsStart, startTime, stopTime );
        logger.info( dateFormat.format( new Date() ) + " getRating hbase " );
        Map<String, Long> map = mapss.get( "map" );
        if(map.size()==0){
            return null;
        }
        ArrayList<String> mapKeyList = new ArrayList<>( map.keySet() );
        Map<String, Long> mapdis = mapss.get( "mapdis" );
        logger.info( map.size() + "==mapsize=" + mapdis.size() );
        List<Audrating> listmap = new ArrayList();
        ArrayList<String> newmapKey = new ArrayList<>();
        Map<String, CdnChannelBean> map3 = new HashedMap();
        if (mapKeyList.size() != 0) {
            List<CdnChannelBean> cdnChannelBeans = cdnChannelService.getForChannelName( mapKeyList );
            if (cdnChannelBeans.size() != 0) {
                for (CdnChannelBean channelBean : cdnChannelBeans) {
                    map3.put( channelBean.getCallsign(), channelBean );
                }
                Common.setMap1( map3 );
            }
        }
        for (int i = 0; i < mapKeyList.size(); i++) {
            Audrating audrating = new Audrating();
            audrating.setCount( map.get( mapKeyList.get( i ) ).intValue() );
            audrating.setNum( mapdis.get( mapKeyList.get( i ) ).intValue() );
            audrating.setChannel_id( mapKeyList.get( i ) );

            if (map3.containsKey( mapKeyList.get( i ) )) {
                audrating.setName( map3.get( mapKeyList.get( i ) ).getName() );
                listmap.add( audrating );
            } else {
                if (!newmapKey.contains( mapKeyList.get( i ) )) {
                    newmapKey.add( mapKeyList.get( i ) );
                }
            }
        }
        Map<String, CdnChannelBean> newmap3 = new HashedMap();
        if (newmapKey.size() != 0) {
            List<CdnChannelBean> cdnChannelBeans = cdnChannelService.getForChannelNameByOther( newmapKey );
            if (cdnChannelBeans.size() != 0) {
                for (CdnChannelBean channelBean : cdnChannelBeans) {
                    newmap3.put( channelBean.getCallsign(), channelBean );
                }
                Common.setMap1( newmap3 );
            }
        }

        for (int i = 0; i < mapKeyList.size(); i++) {
            if (!map3.containsKey( mapKeyList.get( i ) )) {
                Audrating audrating = new Audrating();
                audrating.setCount( map.get( mapKeyList.get( i ) ).intValue() );
                audrating.setNum( mapdis.get( mapKeyList.get( i ) ).intValue() );
                audrating.setChannel_id( mapKeyList.get( i ) );
                if (mapKeyList.get( i ).contains( "@" )) {
                    if (newmap3.containsKey( mapKeyList.get( i ).split( "@" )[0] )) {
                        audrating.setName( newmap3.get( mapKeyList.get( i ).split( "@" )[0] ).getName() );
                    }
                } else {
                    if (newmap3.containsKey( mapKeyList.get( i ) )) {
                        audrating.setName( newmap3.get( mapKeyList.get( i ) ).getName() );
                    }
                }
                listmap.add( audrating );
            }
        }

        Collections.sort( listmap, (Audrating c1, Audrating c2) -> c2.getCount() - (c1.getCount()) );
        logger.info( dateFormat.format( new Date() ) + " getRating sql " );
        cdnLogs.setTotal( listmap.size() );
        if (cdnLogs.getTotal() > 0) {
            cdnLogs.setRows( listmap );
        }
        logger.info( dateFormat.format( new Date() ) + " getRating over " + list.size() );
        return JSONObject.fromObject( cdnLogs );
    }


    /**
     *  使用ForkJoinPool处理逻辑，费时较多（5s）
     * @param rows
     * @param page
     * @param pageBean
     * @return
     * @throws Exception
     */
    @RequestMapping("getRating_1")
    @ResponseBody
    public JSONObject getRatingFork(int rows, int page, PageBean pageBean) throws Exception {

        Page<Audrating> cdnLogs = new Page<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        ArrayList<Audrating> list = new ArrayList();
        logger.info( dateFormat.format( new Date() ) + " getRating start " );
        list = new HbaseRatingConsumer().get( pageBean );
        logger.info( dateFormat.format( new Date() ) + " getRating hbase " + list.size() );

        map1 = Common.getMap1();
        Map<String, List<Audrating>> map = new HashedMap();
        Map<String, List<String>> mapdis = new HashedMap();
        ArrayList<String> mapKey = new ArrayList<>();
        boolean IsStart = false;
        if (null != pageBean.getStartTime() && pageBean.getStartTime().length() > 3) {
            IsStart = true;
        }

        SumTask task = new SumTask( list, 0, list.size() );
//        创建一个通用池，这个是jdk1.8提供的功能
        ForkJoinPool pool = ForkJoinPool.commonPool();
        Future<ArrayList<String>> future = pool.submit( task ); //提交分解的SumTask 任务
        mapKey = future.get();
//        System.out.println("多线程执行结果："+future.get());
        pool.shutdown(); //关闭线程池

        String startTime = pageBean.getStartTime();
        String stopTime = pageBean.getEndTime();

        MapTask taskmap = new MapTask( list, 0, list.size(), IsStart, startTime, stopTime );
//        创建一个通用池，这个是jdk1.8提供的功能
        ForkJoinPool poolmap = ForkJoinPool.commonPool();
        Future<Map<String, List<Audrating>>> futuremap = poolmap.submit( taskmap ); //提交分解的SumTask 任务
        map = futuremap.get();
//        System.out.println("多线程执行结果："+future.get());
        poolmap.shutdown(); //关闭线程池

        MapDisTask taskmapdis = new MapDisTask( list, 0, list.size(), IsStart, startTime, stopTime );
//        创建一个通用池，这个是jdk1.8提供的功能
        ForkJoinPool poolmapdis = ForkJoinPool.commonPool();
        Future<Map<String, List<String>>> futuremapdis = poolmapdis.submit( taskmapdis ); //提交分解的SumTask 任务
        mapdis = futuremapdis.get();
//        System.out.println("多线程执行结果："+future.get());
        poolmapdis.shutdown(); //关闭线程池


        Map<String, CdnChannelBean> map3 = new HashedMap();
        if (mapKey.size() != 0) {
            List<CdnChannelBean> cdnChannelBeans = cdnChannelService.getForChannelName( mapKey );
            if (cdnChannelBeans.size() != 0) {
                for (CdnChannelBean channelBean : cdnChannelBeans) {
                    map3.put( channelBean.getCallsign(), channelBean );
                }
                Common.setMap1( map3 );
            }
        }
        map1 = Common.getMap1();
        logger.info( dateFormat.format( new Date() ) + " getRating sql   mp3=" + map3.size() );
        List<Audrating> listmap = new ArrayList();
        List<String> mapKeyList = new ArrayList<>( map.keySet() );
        ArrayList<String> newmapKey = new ArrayList<>();
        ArrayList<String> newmapKey2 = new ArrayList<>();
        for (int i = 0; i < mapKeyList.size(); i++) {
            Audrating audrating = new Audrating();
            audrating.setCount( map.get( mapKeyList.get( i ) ).size() );
            audrating.setNum( mapdis.get( mapKeyList.get( i ) ).size() );
            audrating.setChannel_id( mapKeyList.get( i ) );
//            logger.info( mapdis.get( mapKeyList.get( i ) ).size() + " getRating 111111111= " + map.get( mapKeyList.get( i ) ).size() );
            if (map3.containsKey( mapKeyList.get( i ) )) {
                audrating.setName( map3.get( mapKeyList.get( i ) ).getName() );
//                audrating.setCountry( map3.get( mapKeyList.get( i ) ).getCountry() );
                listmap.add( audrating );
            } else {
//                if (mapKeyList.get( i ).contains( "@" )){
                if (!newmapKey.contains( mapKeyList.get( i ) )) {
                    newmapKey.add( mapKeyList.get( i ) );
                }
            }
        }
        logger.info( dateFormat.format( new Date() ) + " getRating map " + map.size() );
        Map<String, CdnChannelBean> newmap3 = new HashedMap();
        if (newmapKey.size() != 0) {
            List<CdnChannelBean> cdnChannelBeans = cdnChannelService.getForChannelNameByOther( newmapKey );
            if (cdnChannelBeans.size() != 0) {
                for (CdnChannelBean channelBean : cdnChannelBeans) {
                    newmap3.put( channelBean.getCallsign(), channelBean );
                }
                Common.setMap1( newmap3 );
            }
        }
        logger.info( dateFormat.format( new Date() ) + " getRating sql newmap3 " + newmap3.size() );
        for (int i = 0; i < mapKeyList.size(); i++) {
            if (!map3.containsKey( mapKeyList.get( i ) )) {
                Audrating audrating = new Audrating();
                audrating.setCount( map.get( mapKeyList.get( i ) ).size() );
                audrating.setNum( mapdis.get( mapKeyList.get( i ) ).size() );
                audrating.setChannel_id( mapKeyList.get( i ) );
                if (mapKeyList.get( i ).contains( "@" )) {
                    if (newmap3.containsKey( mapKeyList.get( i ).split( "@" )[0] )) {
                        audrating.setName( newmap3.get( mapKeyList.get( i ).split( "@" )[0] ).getName() );
//                        audrating.setCountry( newmap3.get( mapKeyList.get( i ).split( "@" )[0] ).getCountry() );
                    }
                } else {
                    if (newmap3.containsKey( mapKeyList.get( i ) )) {
                        audrating.setName( newmap3.get( mapKeyList.get( i ) ).getName() );
//                        audrating.setCountry( newmap3.get( mapKeyList.get( i ) ).getCountry() );
                    }
                }
                listmap.add( audrating );

            }
        }
//        logger.info( dateFormat.format( new Date() ) + " getRating listmap "+listmap.size() );
        Collections.sort( listmap, (Audrating c1, Audrating c2) -> c2.getCount() - (c1.getCount()) );
        cdnLogs.setTotal( listmap.size() );
        if (cdnLogs.getTotal() > 0) {
            cdnLogs.setRows( listmap );
        }
        logger.info( dateFormat.format( new Date() ) + " getRating size" + listmap.size() );

        return JSONObject.fromObject( cdnLogs );
    }


    /**
     * 正常处理逻辑 用时8s
     * @param rows
     * @param page
     * @param pageBean
     * @return
     * @throws Exception
     */
    @RequestMapping("getRating_0")
    @ResponseBody
    public JSONObject getRatingCommon(int rows, int page, PageBean pageBean) throws Exception {

        Page<Audrating> cdnLogs = new Page<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        ArrayList<Audrating> list = new ArrayList();
        logger.info( dateFormat.format( new Date() ) + " getRating start " );
        list = new HbaseRatingConsumer().get( pageBean );
        logger.info( dateFormat.format( new Date() ) + " getRating hbase " );

        map1 = Common.getMap1();
        Map<String, List<Audrating>> map = new HashedMap();
        Map<String, List<String>> mapdis = new HashedMap();
        ArrayList<String> mapKey = new ArrayList<>();
        boolean IsStart = false;
        if (null != pageBean.getStartTime() && pageBean.getStartTime().length() > 3) {
            IsStart = true;
        }

        for (Audrating audrating : list) {
            List<Audrating> list1 = new ArrayList<>();
            List<String> listdis = new ArrayList<>();
            if (!mapKey.contains( audrating.getChannel_id() )) {
                mapKey.add( audrating.getChannel_id() );
            }
//            }
            if (!mapdis.containsKey( audrating.getChannel_id() )) {
                listdis.add( audrating.getSn() );
                if (IsStart) {
                    if (pageBean.getStartTime().compareTo( audrating.getCurrent_Times() ) > 0 || pageBean.getEndTime().compareTo( audrating.getConnect_Times() ) < 0) {

                    } else {
                        mapdis.put( audrating.getChannel_id(), listdis );

                    }
                } else {
                    mapdis.put( audrating.getChannel_id(), listdis );
                }
            } else {
                listdis = mapdis.get( audrating.getChannel_id() );
                if (IsStart) {
                    if (pageBean.getStartTime().compareTo( audrating.getCurrent_Times() ) > 0 || pageBean.getEndTime().compareTo( audrating.getConnect_Times() ) < 0) {

                    } else {
                        boolean isdis = false;
                        for (int i = 0; i < listdis.size(); i++) {
                            if (audrating.getSn().equals( listdis.get( i ) )) {
                                isdis = true;
                                break;
                            }

                        }
                        if (!isdis) {
                            listdis.add( audrating.getSn() );
                            mapdis.put( audrating.getChannel_id(), listdis );
                        }
                    }
                } else {
                    boolean isdis1 = false;
                    for (int i = 0; i < listdis.size(); i++) {
                        if (audrating.getSn().equals( listdis.get( i ) )) {
                            isdis1 = true;
                            break;
                        }

                    }
                    if (!isdis1) {
                        listdis.add( audrating.getSn() );
                        mapdis.put( audrating.getChannel_id(), listdis );
                    }
                }
            }
            if (!map.containsKey( audrating.getChannel_id() )) {
                list1.add( audrating );
                if (IsStart) {
                    if (pageBean.getStartTime().compareTo( audrating.getCurrent_Times() ) > 0 || pageBean.getEndTime().compareTo( audrating.getConnect_Times() ) < 0) {

                    } else {
                        map.put( audrating.getChannel_id(), list1 );
                    }
                } else {
                    map.put( audrating.getChannel_id(), list1 );
                }
            } else {
                list1 = map.get( audrating.getChannel_id() );
                if (IsStart) {
                    if (pageBean.getStartTime().compareTo( audrating.getCurrent_Times() ) > 0 || pageBean.getEndTime().compareTo( audrating.getConnect_Times() ) < 0) {

                    } else {
                        list1.add( audrating );
                        map.put( audrating.getChannel_id(), list1 );
                    }
                } else {
                    list1.add( audrating );
                    map.put( audrating.getChannel_id(), list1 );
                }

            }
        }


        Map<String, CdnChannelBean> map3 = new HashedMap();
        if (mapKey.size() != 0) {
            List<CdnChannelBean> cdnChannelBeans = cdnChannelService.getForChannelName( mapKey );
            if (cdnChannelBeans.size() != 0) {
                for (CdnChannelBean channelBean : cdnChannelBeans) {
                    map3.put( channelBean.getCallsign(), channelBean );
                }
                Common.setMap1( map3 );
            }
        }
        map1 = Common.getMap1();
        logger.info( dateFormat.format( new Date() ) + " getRating sql   mp3=" + map3.size() );
        List<Audrating> listmap = new ArrayList();
        List<String> mapKeyList = new ArrayList<>( map.keySet() );
        ArrayList<String> newmapKey = new ArrayList<>();
        ArrayList<String> newmapKey2 = new ArrayList<>();
        for (int i = 0; i < mapKeyList.size(); i++) {
            Audrating audrating = new Audrating();
            audrating.setCount( map.get( mapKeyList.get( i ) ).size() );
            audrating.setNum( mapdis.get( mapKeyList.get( i ) ).size() );
            audrating.setChannel_id( mapKeyList.get( i ) );
//            logger.info( mapdis.get( mapKeyList.get( i ) ).size() + " getRating 111111111= " + map.get( mapKeyList.get( i ) ).size() );
            if (map3.containsKey( mapKeyList.get( i ) )) {
                audrating.setName( map3.get( mapKeyList.get( i ) ).getName() );
//                audrating.setCountry( map3.get( mapKeyList.get( i ) ).getCountry() );
                listmap.add( audrating );
            } else {
//                if (mapKeyList.get( i ).contains( "@" )){
                if (!newmapKey.contains( mapKeyList.get( i ) )) {
                    newmapKey.add( mapKeyList.get( i ) );
                }
            }
        }
        logger.info( dateFormat.format( new Date() ) + " getRating map " + map.size() );
        Map<String, CdnChannelBean> newmap3 = new HashedMap();
        if (newmapKey.size() != 0) {
            List<CdnChannelBean> cdnChannelBeans = cdnChannelService.getForChannelNameByOther( newmapKey );
            if (cdnChannelBeans.size() != 0) {
                for (CdnChannelBean channelBean : cdnChannelBeans) {
                    newmap3.put( channelBean.getCallsign(), channelBean );
                }
                Common.setMap1( newmap3 );
            }
        }
        logger.info( dateFormat.format( new Date() ) + " getRating sql newmap3 " + newmap3.size() );
        for (int i = 0; i < mapKeyList.size(); i++) {
            if (!map3.containsKey( mapKeyList.get( i ) )) {
                Audrating audrating = new Audrating();
                audrating.setCount( map.get( mapKeyList.get( i ) ).size() );
                audrating.setNum( mapdis.get( mapKeyList.get( i ) ).size() );
                audrating.setChannel_id( mapKeyList.get( i ) );
                if (mapKeyList.get( i ).contains( "@" )) {
                    if (newmap3.containsKey( mapKeyList.get( i ).split( "@" )[0] )) {
                        audrating.setName( newmap3.get( mapKeyList.get( i ).split( "@" )[0] ).getName() );
//                        audrating.setCountry( newmap3.get( mapKeyList.get( i ).split( "@" )[0] ).getCountry() );
                    }
                } else {
                    if (newmap3.containsKey( mapKeyList.get( i ) )) {
                        audrating.setName( newmap3.get( mapKeyList.get( i ) ).getName() );
//                        audrating.setCountry( newmap3.get( mapKeyList.get( i ) ).getCountry() );
                    }
                }
                listmap.add( audrating );

            }
        }
        Collections.sort( listmap, (Audrating c1, Audrating c2) -> c2.getCount() - (c1.getCount()) );
        cdnLogs.setTotal( listmap.size() );
        if (cdnLogs.getTotal() > 0) {
            cdnLogs.setRows( listmap );
        }
        logger.info( dateFormat.format( new Date() ) + " getRating size" + listmap.size() );

        return JSONObject.fromObject( cdnLogs );
    }


}
