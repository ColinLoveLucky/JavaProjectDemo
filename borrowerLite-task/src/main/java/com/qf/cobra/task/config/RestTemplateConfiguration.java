package com.qf.cobra.task.config;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties(HttpClientConfiguration.class)
@Slf4j
public class RestTemplateConfiguration {

    @Autowired
    private HttpClientConfiguration httpClientConfiguration;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // 设置连接池
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        //设置转换
        List<HttpMessageConverter<?>> httpMessageConverterList = reInitMessageConverter(restTemplate);
        restTemplate.getMessageConverters().clear();
        restTemplate.setMessageConverters(httpMessageConverterList);
        // 设置错误处理
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        return restTemplate;
    }
    
    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = null;
        try {
            // 注册协议
            Registry<ConnectionSocketFactory> socketFactotyRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", getSslConnectionSocketFactory()).build();

            //配置连接池
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactotyRegistry);
            poolingHttpClientConnectionManager.setMaxTotal(httpClientConfiguration.getMaxTotal());//最大连接数
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(httpClientConfiguration.getMaxPerRoute());//同路由最大连接数
            HttpClientBuilder httpClientBuilder = httpClientBuilder();
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(httpClientConfiguration.getRetrySize(), httpClientConfiguration.isRetry()));

            //配置客户端连接属性
            HttpClient httpClient = httpClientBuilder.build();
            httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            httpComponentsClientHttpRequestFactory.setConnectTimeout(httpClientConfiguration.getConnectTimeout()); //连接超时时间
            httpComponentsClientHttpRequestFactory.setReadTimeout(httpClientConfiguration.getReadTimeout()); //数据读取超时时间
            httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(httpClientConfiguration.getConnectionRequestTimeout()); //获取连接时间
            log.info("Request a connection pool to create success...");
        } catch (Exception e) {
        	log.error("Request connection pool to create a failure...", e);
        }
        return httpComponentsClientHttpRequestFactory;
    }

    @Bean
    public HttpClientBuilder httpClientBuilder() {
        return HttpClientBuilder.create();
    }


    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
        mappingJackson2HttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypeList);
        return mappingJackson2HttpMessageConverter;
    }


    private StringHttpMessageConverter stringHttpMessageConverter() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypeList.add(MediaType.TEXT_PLAIN);
        stringHttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setSupportedMediaTypes(mediaTypeList);

        return stringHttpMessageConverter;
    }

    private FormHttpMessageConverter formHttpMessageConverter() {
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_FORM_URLENCODED);
        formHttpMessageConverter.setMultipartCharset(Charset.forName("UTF-8"));
        formHttpMessageConverter.setSupportedMediaTypes(mediaTypeList);
        return formHttpMessageConverter;
    }

    /**
     * 初始化自定义消息转换器
     * 1.系统默认消息转换器
     * 2.StringHttpMessageConverter
     * 3.MappingJackson2HttpMessageConverter
     * 4.FormHttpMessageConverter
     * 
     * @param restTemplate
     * @return
     */
    private List<HttpMessageConverter<?>> reInitMessageConverter(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        List<HttpMessageConverter<?>> newConverterList = new ArrayList<>();
        for (HttpMessageConverter<?> item : converterList) {
            if (item.getClass() == StringHttpMessageConverter.class) {
                newConverterList.add(stringHttpMessageConverter());
            } else if (item.getClass() == MappingJackson2HttpMessageConverter.class) {
                newConverterList.add(mappingJackson2HttpMessageConverter());
            } else {
                newConverterList.add(item);
            }
        }

        newConverterList.add(formHttpMessageConverter());
        return newConverterList;
    }


    private SSLConnectionSocketFactory getSslConnectionSocketFactory() {
        SSLContext sslContext = null;
        HostnameVerifier hostnameVerifier = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            httpClientBuilder().setSSLContext(sslContext);
        } catch (NoSuchAlgorithmException e) {
        	log.error("Generate a key exception", e);
        } catch (KeyManagementException e) {
        	log.error("Key management exception", e);
        } catch (KeyStoreException e) {
        	log.error("Key management exception", e);
        }
        return new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
    }
}
