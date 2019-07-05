package mocean.logs.service;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpsConfiguration {
	@Value("${protocol.scheme:#{null}}")
	private String scheme;
	@Value("${protocol.port:#{null}}")
	private Integer port;
	@Value("${protocol.ssl.key-store-password:#{null}}")
	private String keyStorePassword;
	@Value("${protocol.ssl.key-password:#{null}}")
	private String keyPassword;
	@Value("${protocol.ssl.key-store:#{null}}")
	private String keyStore;
	@Value("${protocol.ssl.client-auth:#{null}}")
	private String clientAuth;
	@Value("${protocol.ssl.trust-store:#{null}}")
	private String truststore;
	@Value("${protocol.ssl.trust-store-password:#{null}}")
	private String truststorePassword;

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		if(scheme != null && scheme.equals("https")) {
			tomcat.addAdditionalTomcatConnectors(createHttpsConnector());
		}
		return tomcat;
	}

	private Connector createHttpsConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
		connector.setScheme("https");
		connector.setSecure(true);
		connector.setPort(port);
		protocol.setSSLEnabled(true);
		protocol.setKeystoreFile(keyStore);
		protocol.setKeystorePass(keyStorePassword);
		protocol.setKeyPass(keyPassword);
		if(clientAuth != null) {
			protocol.setClientAuth(clientAuth);
			protocol.setTruststoreFile(truststore);
			protocol.setTruststorePass(truststorePassword);
		}
		return connector;
	}
}
