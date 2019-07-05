package mocean.logs.util;

import mocean.logs.domain.Audrating;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class MapTask extends RecursiveTask<Map<String, List<Audrating>>> {
    private static final int THRESHOLD = 100000; //每个小任务 最多只累加20个数
    private ArrayList<Audrating> list;
    private int start;
    private int end;
    private boolean IsStart;
    private String startTime;
    private String stopTime;



    /**
     * Creates a new instance of SumTask.
     * 累加从start到end的arry数组
     * @param list1
     * @param start
     * @param end
     */
    public MapTask(ArrayList<Audrating> list1, int start, int end, boolean IsStart, String startTime, String stopTime) {
        super();
        this.list = list1;
        this.start = start;
        this.end = end;
        this.IsStart = IsStart ;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }



    @Override
    protected Map<String, List<Audrating>> compute(){
        Map<String, List<Audrating>> map = new HashedMap();
        ArrayList<String> mapKey = new ArrayList<>(  );
        //当end与start之间的差小于threshold时，开始进行实际的累加
        if(end - start <THRESHOLD){
            mapKey = new ArrayList<>();
            for (Audrating audrating : list.subList( start,end )) {
                List<Audrating> list1 = new ArrayList<>();
                if (!map.containsKey( audrating.getChannel_id() )) {
                    list1.add( audrating );
                    if (IsStart) {
                        if (startTime.compareTo( audrating.getCurrent_Times() ) > 0 || stopTime.compareTo( audrating.getConnect_Times() ) < 0) {

                        } else {
                            map.put( audrating.getChannel_id(), list1 );
                        }
                    } else {
                        map.put( audrating.getChannel_id(), list1 );
                    }
                } else {
                    list1 = map.get( audrating.getChannel_id() );
                    if (IsStart) {
                        if (startTime.compareTo( audrating.getCurrent_Times() ) > 0 || stopTime.compareTo( audrating.getConnect_Times() ) < 0) {

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
            return map;
        }else {//当end与start之间的差大于threshold，即要累加的数超过20个时候，将大任务分解成小任务

            int middle = (start+ end)/2;
            MapTask left = new MapTask(list, start, middle, IsStart ,startTime,stopTime);
            MapTask right = new MapTask(list, middle, end, IsStart, startTime,stopTime);
            //并行执行两个 小任务
            left.fork();
            right.fork();
            //把两个小任务累加的结果合并起来
            Map<String, List<Audrating>> mapKeyleft = left.join();
            Map<String, List<Audrating>> mapKeyright = right.join();
            List<String> mapKeyListright = new ArrayList<>( mapKeyright.keySet() );
            if(mapKeyListright.size()!=0){
                for (int i = 0; i <mapKeyListright.size() ; i++) {
                    if(mapKeyleft.containsKey( mapKeyListright.get( i ) )){
                        List<Audrating> leftlista = mapKeyleft.get( mapKeyListright.get( i ) );
                        List<Audrating> rightlista = mapKeyright.get( mapKeyListright.get( i ) );
                        leftlista.addAll( rightlista );
                        mapKeyleft.put( mapKeyListright.get( i ),leftlista );
                    }else {
                        List<Audrating> rightlista = mapKeyright.get( mapKeyListright.get( i ) );
                        mapKeyleft.put( mapKeyListright.get( i ),rightlista );
                    }
                }
            }
            return mapKeyleft;
        }

    }

}
