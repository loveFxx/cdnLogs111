package mocean.logs.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

//@Configuration
//@MapperScan(basePackages = "mocean.logs.dao", sqlSessionTemplateRef  = "cmsSqlSessionTemplate")
public class DataLogsSourceConfig {
    @Bean(name = "cmsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.cms")
    @Primary
    public DataSource cmsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "cmsSqlSessionFactory")
    public SqlSessionFactory cmsSqlSessionFactory(@Qualifier("cmsDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "cmsTransactionManager")
    public DataSourceTransactionManager cdnMonitorTransactionManager(@Qualifier("cmsDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "cmsSqlSessionTemplate")
    public SqlSessionTemplate cmsSqlSessionTemplate(@Qualifier("cmsSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
