package net.turnbig.pandora.httpclient;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

/**
 *
 * @author QianBiao.NG
 * @date   2017-12-07 15:54:57
 */
public class ProxiedHttpClientBuilder {

	public static final String PROXY_SOCKS_ADDRESS_ATTR = "proxy.socks.address";

	public static PoolingHttpClientConnectionManager createConnectionManager(SSLContext sslContext) {
		sslContext = sslContext != null ? sslContext: SSLContexts.createDefault();
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", new ProxiedPlainConnectionSocketFactory())
				.register("https", new ProxiedSSLConnectionSocketFactory(sslContext)).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
		return cm;
	}
	
	public static PoolingHttpClientConnectionManager createConnectionManager() {
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", new ProxiedPlainConnectionSocketFactory())
				.register("https", new ProxiedSSLConnectionSocketFactory(SSLContexts.createDefault())).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
		return cm;
	}


	public static HttpContext createHttpContext(String host, Integer port) {
		InetSocketAddress proxySocketAddr = new InetSocketAddress(host, port);
		HttpClientContext context = HttpClientContext.create();
		context.setAttribute(PROXY_SOCKS_ADDRESS_ATTR, proxySocketAddr);
		return context;
	}

}
