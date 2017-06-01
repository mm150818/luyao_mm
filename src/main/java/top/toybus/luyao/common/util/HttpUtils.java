package top.toybus.luyao.common.util;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP请求工具类
 * 
 * @author SXG
 */
public class HttpUtils {

	static RestTemplate restTemplate;

	/**
	 * @param uri 一个有效的URI的字符串形式
	 * @return string
	 */
	public static String doGet(URI uri) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			try {
				HttpEntity entity = httpResponse.getEntity();
				if (entity == null) {
					return null;
				} else {
					return EntityUtils.toString(entity);
				}
			} finally {
				httpResponse.close();
			}
		} finally {
			httpClient.close();
		}
	}

	public static void main(String[] args) throws Exception {
		/*String uri = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
		uri = uri.replace("APPID", "wxa47ac1daaa18b4ab").replace("APPSECRET",
				"b0e75f1fb6de0021e66b5868fce15d10");*/
		/*URI uri = new URIBuilder(
				"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential")
				.addParameter("appid", "wxa47ac1daaa18b4ab")
				.addParameter("secret", "b0e75f1fb6de0021e66b5868fce15d10").build();

		String string = HttpUtils.doGet(uri);
		System.out.println(string);*/

		RestTemplate restTemplate2 = new RestTemplate();
		String string2 = restTemplate2
				.getForObject(
						"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={APPSECRET}",
						String.class, "wxa47ac1daaa18b4ab", "b0e75f1fb6de0021e66b5868fce15d10");
		System.out.println(string2);
	}

	/**
	 * 获得 忽略证书并且被连接池管理的HttpClient
	 * 
	 * @return
	 * @throws Exception
	 */
	public static CloseableHttpClient getIgnoreCertsPooledHttpClient() throws Exception {

		// setup a Trust Strategy that allows all certificates.
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
				return true;
			}
		}).build();

		// don't check Hostnames, either.
		// -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to
		// weaken
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

		// here's the special part:
		// -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
		// -- and create a Registry, to register it.
		//
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
				sslContext, hostnameVerifier);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", sslConnectionSocketFactory).build();

		// now, we create connection-manager using our Registry.
		// -- allows multi-threaded use
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(20);

		// finally, build the HttpClient;
		// -- done!
		CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext)
				.setConnectionManager(connectionManager).build();

		return httpClient;
	}

}
