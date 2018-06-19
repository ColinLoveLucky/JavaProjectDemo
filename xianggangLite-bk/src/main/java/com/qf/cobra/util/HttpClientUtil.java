package com.qf.cobra.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HttpClientUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

	private static HttpClient httpClient;

	static {
		PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
		pollingConnectionManager.setMaxTotal(1000);
		pollingConnectionManager.setDefaultMaxPerRoute(1000);

		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(pollingConnectionManager);
//		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, false));
		httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

		RequestConfig.Builder builder = RequestConfig.custom();
		builder.setConnectionRequestTimeout(200);
		builder.setConnectTimeout(5000);
		builder.setSocketTimeout(30000);
		RequestConfig requestConfig = builder.build();
		httpClientBuilder.setDefaultRequestConfig(requestConfig);

		// List<Header> headers = new ArrayList<>();
		// httpClientBuilder.setDefaultHeaders(headers);

		httpClient = httpClientBuilder.build();

		LOGGER.info("RestClient初始化完成");
	}

	/**
	 * 获取重定向之后的网址信息
	 * 
	 * @throws Exception
	 * @see HttpClient缺省会自动处理客户端重定向
	 * @see 即访问网页A后,假设被重定向到了B网页,那么HttpClient将自动返回B网页的内容
	 * @see 若想取得B网页的地址
	 *      ,就需要借助HttpContext对象,HttpContext实际上是客户端用来在多次请求响应的交互中,保持状态信息的
	 * @see 我们自己也可以利用HttpContext来存放一些我们需要的信息,以便下次请求的时候能够取出这些信息来使用
	 */
	public static void getRedirectInfo(String url) throws Exception {
		HttpContext httpContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(url);
		try {
			// 将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			// 获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
			HttpHost targetHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			// 获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
			HttpUriRequest realRequest = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				// System.out.println("响应内容:" + EntityUtils.toString(entity,
				// ContentType.getOrDefault(entity).getCharset()));
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String doGet(String url, String charset, Map<String, String> headers) throws Exception {
		String result = null;
		HttpGet httpget = null;
		try {
			httpget = new HttpGet(url);
			if (headers != null) {
				for (Entry<String, String> e : headers.entrySet()) {
					httpget.addHeader(e.getKey(), e.getValue());
				}
			}
			// 设置参数
			HttpResponse response = httpClient.execute(httpget);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (httpget != null) {
				httpget.releaseConnection();
			}
		}
		return result;
	}
	/**
	 * 发起post请求
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String url, List<NameValuePair> params, String charset, Map<String, String> headers) throws Exception {
		UrlEncodedFormEntity entity = null;
		if (null != params && params.size() > 0) {
			entity = new UrlEncodedFormEntity(params, charset);
		}
		return doPost(url, entity, charset, headers);
	}

	/**
	 * 发送http请求，请求参数序列化为json传输
	 * 
	 * @param url
	 * @param requestObject
	 * @return
	 * @throws Exception
	 */
	public static String doPostWithJson(String url, Object requestObject, Map<String, String> headers) throws Exception {
		if (StringUtils.isEmpty(url)) {
			return "URL is empty!";
		}
		String charset = "utf-8";
		String param = JsonUtil.convert(requestObject);
		StringEntity entity = new StringEntity(param, charset);// 解决中文乱码问题
		entity.setContentEncoding(charset);
		entity.setContentType("application/json");
		return doPost(url, entity, charset, headers);
	}

	/**
	 * 发起post请求
	 * 
	 * @param url
	 * @param entity
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String url, HttpEntity entity, String charset, Map<String, String> headers) throws Exception {
		String result = null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			if (headers != null) {
				for (Entry<String, String> e : headers.entrySet()) {
					httpPost.addHeader(e.getKey(), e.getValue());
				}
			}
			// 设置参数
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return result;
	}

	/**
	 * 下载网络文件
	 * 
	 * @param httpUrl
	 * @param filePath
	 * @throws Exception
	 */
	public static boolean downloadFile(String httpUrl, String filePath) throws Exception {
		if (StringUtils.isEmpty(httpUrl)) {
			return false;
		}
		boolean downResult = false;
		FileOutputStream fs = null;
		InputStream inStream = null;
		try {
			int byteread = 0;
			URL url = new URL(httpUrl);
			URLConnection conn = url.openConnection();
			inStream = conn.getInputStream();
			fs = new FileOutputStream(filePath);

			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			downResult = true;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fs != null) {
				fs.close();
			}
			if (inStream != null) {
				inStream.close();
			}
		}
		return downResult;
	}

}
