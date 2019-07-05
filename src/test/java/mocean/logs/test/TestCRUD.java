package mocean.logs.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

public class TestCRUD {

    @Test
    public void put() throws  Exception{
        Configuration conf = HBaseConfiguration.create();
        conf.set( "hbase.rootdir","hdfs://192.168.7.200:8020/hbase" );
        Connection conn = ConnectionFactory.createConnection( conf );

        TableName tname = TableName.valueOf( "ns1:hello" );
        Table table = conn.getTable( tname );
        String rowkey = "170714038542"+","+"TV7032"+","+System.currentTimeMillis();
        System.out.println("rowkey=="+rowkey);
        Put put = new Put( Bytes.toBytes(rowkey) );
        byte[] f1 = Bytes.toBytes("f1");
        byte[] id = Bytes.toBytes("name") ;
        byte[] value = Bytes.toBytes("hahaha");
        put.addColumn(f1,id,value);

        table.put( put );
        System.out.println("-------------------------------");

    }
}
