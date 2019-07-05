package mocean.logs.hiveutil;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Test;
import java.util.*;

public class MyConsumer {
//    private final ConsumerConnector consumerConn;
//    private final String topic;

//    public MyConsumer() {
//        Properties props = new Properties();
//        props.put("zookeeper.connect", "185.180.221.108:2181");
//        props.put("group.id", "cdnlogs");
//        props.put("auto.offset.reset", "smallest");
////        props.put("zookeeper.session.timeout.ms", "500");
////        props.put("zookeeper.sync.time.ms", "250");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("enable-auto-commit", "true");
//        //kafka-console-producer.sh --broker-list 192.168.7.200:9092 --topic test
//        // 创建消费者连接器kafka-console-consumer.sh --bootstrap-server 192.168.7.200:9092 --topic test --from-beginning
//        consumerConn = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
//        this.topic = "cdnlog3";
//    }

    @Test
    public void testConsumer() {
        // 指定消费的主题
        Properties props = new Properties();
//        props.put("zookeeper.connect", "185.180.221.108:2181");
        props.put("bootstrap.servers", "185.180.221.108:9092");
        props.put("group.id", "cdnlogs");
        props.put("auto.offset.reset", "earliest");//largest
//        props.put("zookeeper.session.timeout.ms", "5000");
//        props.put("zookeeper.sync.time.ms", "250");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("enable-auto-commit", "true");

        //kafka-console-producer.sh --broker-list 192.168.7.200:9092 --topic test
        // 创建消费者连接器kafka-console-consumer.sh --bootstrap-server 192.168.7.200:9092 --topic test --from-beginning
//        ConsumerConnector consumerConn = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        String topic = "cdnlog3";
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe( Collections.singleton( topic ) );
        List<PartitionInfo> parList = consumer.partitionsFor( topic );
        System.out.println(parList+"========");
        Map<String, Integer> topicCount = new HashMap<String, Integer>();
        topicCount.put(topic, new Integer(3));


        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(5000);
            System.out.println("topic---------------------: "+topic + " pool return records size: "+ records.count());
            for (ConsumerRecord<String, String> record : records){
                System.out.println(record.toString());
                //手动提交已消费数据的offset
//                if("false".equalsIgnoreCase(isAutoCommitBool)){
//                    consumer.commitSync();
//                }

            }

        }
    }

    public static void main(String[] args) {
        new Thread( () -> new MyConsumer().testConsumer() ).start();
        new Thread( () -> new MyConsumer().testConsumer() ).start();
        new Thread( () -> new MyConsumer().testConsumer() ).start();







    }
}
