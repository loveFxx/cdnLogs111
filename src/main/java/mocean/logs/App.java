package mocean.logs;

import mocean.logs.hiveutil.HBaseClientConsumer;
import mocean.logs.hiveutil.HBaseConsumer;
import mocean.logs.hiveutil.HBaseGslbConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;


//@SpringBootApplication
@SpringBootApplication  (exclude = {DataSourceAutoConfiguration.class})
@EnableAutoConfiguration(exclude={ JpaRepositoriesAutoConfiguration.class }) //禁止springboot自动加载持久化bean
@ServletComponentScan("mocean.logs.service")
//@MapperScan("mocean.logs.dao")
//@ComponentScan({"mocean.logs.service","mocean.logs.controller"})
//@EnableScheduling
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    HBaseClientConsumer hBaseClientConsumer(){
        HBaseClientConsumer hBaseClientConsumer = new HBaseClientConsumer();
        new Thread( () -> hBaseClientConsumer.processLog() ).start();
        new Thread( () -> hBaseClientConsumer.processLog() ).start();
        new Thread( () -> hBaseClientConsumer.processLog() ).start();
        return hBaseClientConsumer;
    }

    @Bean
    HBaseConsumer hBaseConsumer(){
        HBaseConsumer hBaseConsumer = new HBaseConsumer();
        new Thread( () -> hBaseConsumer.processLog() ).start();
        new Thread( () -> hBaseConsumer.processLog() ).start();
        new Thread( () -> hBaseConsumer.processLog() ).start();
        return hBaseConsumer;
    }

    @Bean
    HBaseGslbConsumer hBaseGslbConsumer(){
        HBaseGslbConsumer hBaseGslbConsumer = new HBaseGslbConsumer();
        new Thread( () -> hBaseGslbConsumer.processLog() ).start();
        new Thread( () -> hBaseGslbConsumer.processLog() ).start();
        new Thread( () -> hBaseGslbConsumer.processLog() ).start();
        return hBaseGslbConsumer;
    }



}
