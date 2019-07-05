package mocean.logs.util;

import mocean.logs.domain.Audrating;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class Task extends RecursiveTask<Map<String, List<String>>> {
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
    public Task(ArrayList<Audrating> list1, int start, int end, boolean IsStart, String startTime, String stopTime) {
        super();
        this.list = list1;
        this.start = start;
        this.end = end;
        this.IsStart = IsStart ;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }



    @Override
    protected Map<String, List<String>> compute(){
        Map<String, List<String>> mapdis = new HashedMap();
        ArrayList<String> mapKey = new ArrayList<>(  );
        //当end与start之间的差小于threshold时，开始进行实际的累加
        if(end - start <THRESHOLD){
            mapKey = new ArrayList<>();
            for (Audrating audrating : list.subList( start,end )) {
                List<String> listdis = new ArrayList<>();
                if (!mapdis.containsKey( audrating.getChannel_id() )) {
                    listdis.add( audrating.getSn() );
                    if (IsStart) {
                        if (startTime.compareTo( audrating.getCurrent_Times() ) > 0 || stopTime.compareTo( audrating.getConnect_Times() ) < 0) {

                        } else {
                            mapdis.put( audrating.getChannel_id(), listdis );

                        }
                    } else {
                        mapdis.put( audrating.getChannel_id(), listdis );
                    }
                } else {
                    listdis = mapdis.get( audrating.getChannel_id() );
                    if (IsStart) {
                        if (startTime.compareTo( audrating.getCurrent_Times() ) > 0 || stopTime.compareTo( audrating.getConnect_Times() ) < 0) {

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
            }
            return mapdis;
        }else {//当end与start之间的差大于threshold，即要累加的数超过20个时候，将大任务分解成小任务

            int middle = (start+ end)/2;
            Task left = new Task(list, start, middle, IsStart ,startTime, stopTime);
            Task right = new Task(list, middle, end, IsStart, startTime, stopTime);
            //并行执行两个 小任务
            left.fork();
            right.fork();
            //把两个小任务累加的结果合并起来
            Map<String, List<String>> mapKeyleft = left.join();
            Map<String, List<String>> mapKeyright = right.join();
            List<String> mapKeyListright = new ArrayList<>( mapKeyright.keySet() );
            if(mapKeyListright.size()!=0){
                for (int i = 0; i <mapKeyListright.size() ; i++) {
                    if(mapKeyleft.containsKey( mapKeyListright.get( i ) )){
                        List<String> leftlista = mapKeyleft.get( mapKeyListright.get( i ) );
                        List<String> rightlista = mapKeyright.get( mapKeyListright.get( i ) );
                        for (int j = 0; j <rightlista.size() ; j++) {
                            boolean iscontain = false;
                            for (int k = 0; k < leftlista.size(); k++) {
                                if(leftlista.get( k ).equals( rightlista.get( j ) )){
                                    iscontain = true;
                                }
                            }
                            if(!iscontain){
                                leftlista.add( rightlista.get( j ) );
                                mapKeyleft.put( mapKeyListright.get( i ),leftlista );
                            }
                        }
                    }else {
                        List<String> rightlistas = mapKeyright.get( mapKeyListright.get( i ) );
                        mapKeyleft.put( mapKeyListright.get( i ),rightlistas );
                    }
                }
            }
            return mapKeyleft;
        }

    }

}
