package net.turnbig.pandora.httpclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author QianBiao.NG
 * @date   2017-12-07 15:51:08
 */
public class ProxiedPlainConnectionSocketFactory extends PlainConnectionSocketFactory {

	private static final Logger logger = LoggerFactory.getLogger(ProxiedPlainConnectionSocketFactory.class);

	@Override
	public Socket createSocket(final HttpContext context) throws IOException {
		if (context != null) {
			InetSocketAddress addr = (InetSocketAddress) context
					.getAttribute(ProxiedHttpClientBuilder.PROXY_SOCKS_ADDRESS_ATTR);
			if (addr != null) {
				Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr);
				return new Socket(proxy);
			}
		}

		logger.warn("no proxy socks address is configurated, directly connect socket will be used");
		return super.createSocket(context);
	}

	@Override
	public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
			InetSocketAddress localAddress, HttpContext context) throws IOException {
		if (context != null) {
			InetSocketAddress addr = (InetSocketAddress) context
					.getAttribute(ProxiedHttpClientBuilder.PROXY_SOCKS_ADDRESS_ATTR);
			if (addr != null) {
				remoteAddress = InetSocketAddress.createUnresolved(host.getHostName(), remoteAddress.getPort());
			}
		}
		return super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
	}
}
