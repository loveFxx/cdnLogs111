package mocean.logs.util;

import mocean.logs.domain.Audrating;
import java.util.ArrayList;

import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<ArrayList<String>> {
    private static final int THRESHOLD = 100000; //每个小任务 最多只累加20个数
    private ArrayList<Audrating> list;
    private int start;
    private int end;





    /**
     * Creates a new instance of SumTask.
     * 累加从start到end的arry数组
     * @param list1
     * @param start
     * @param end
     */
    public SumTask(ArrayList<Audrating> list1, int start, int end) {
        super();
        this.list = list1;
        this.start = start;
        this.end = end;
    }



    @Override
    protected ArrayList<String> compute(){
        ArrayList<String> mapKey = new ArrayList<>(  );
        //当end与start之间的差小于threshold时，开始进行实际的累加
        if(end - start <THRESHOLD){
            mapKey = new ArrayList<>();
            for (Audrating audrating : list.subList( start,end )) {
                if (!mapKey.contains( audrating.getChannel_id() )) {
                    mapKey.add( audrating.getChannel_id() );
                }
            }
            return mapKey;
        }else {//当end与start之间的差大于threshold，即要累加的数超过20个时候，将大任务分解成小任务

            int middle = (start+ end)/2;
            SumTask left = new SumTask(list, start, middle);
            SumTask right = new SumTask(list, middle, end);
            //并行执行两个 小任务
            left.fork();
            right.fork();
            //把两个小任务累加的结果合并起来
            ArrayList<String> mapKeyleft = left.join();
            ArrayList<String> mapKeyright = right.join();

            if(mapKeyright.size()!=0){
                for (int i = 0; i <mapKeyright.size() ; i++) {
                    if(!mapKeyleft.contains( mapKeyright.get( i ) )){
                        mapKeyleft.add( mapKeyright.get( i ) );
                    }
                }
            }
            return mapKeyleft;
        }

    }

}
