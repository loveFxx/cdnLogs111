package mocean.logs.service;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import mocean.logs.common.Common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.Properties;


/**
 *
 * @author
 * @date
 */
@WebListener
public class ConfigListener implements ServletContextListener {

	public ConfigListener() {
		
		super();
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		Producer<String, String> producer;
		Producer<String, String> producer2;
		try {
			ArrayList<Producer<String, String>> arrayList = new ArrayList<>(  );
			ArrayList<Producer<String, String>> arrayListclient = new ArrayList<>(  );
			Properties props = new Properties();
			props.put( "metadata.broker.list", "185.180.221.108:9092" );
			props.put( "serializer.class", "kafka.serializer.StringEncoder" );
			props.put( "request.required.acks", "1" );
			producer = new Producer<String, String>( new ProducerConfig( props ) );

//			Properties props2 = new Properties();
//			props2.put( "metadata.broker.list", "185.180.221.108:9092" );
//			props2.put( "serializer.class", "kafka.serializer.StringEncoder" );
//			props2.put( "request.required.acks", "1" );
//			producer2 = new Producer<String, String>( new ProducerConfig( props2 ) );
			for(int i=0;i<50;i++){
				arrayList.add( producer );
//				arrayListclient.add(producer2  );
			}
			Common.setProducerListList( arrayList );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ArrayList<Producer<String, String>> arrayList = Common.getProducerListList();
		for (Producer<String, String> producer:arrayList) {
			if(producer==null)
				producer.close();
		}
		arrayList.clear();
	}
}
