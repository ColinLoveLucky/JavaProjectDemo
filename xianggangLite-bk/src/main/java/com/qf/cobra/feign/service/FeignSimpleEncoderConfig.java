package com.qf.cobra.feign.service;

import com.qf.cobra.util.JsonUtil;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Configuration
public class FeignSimpleEncoderConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;


    @Bean
    @Primary
    @Scope(SCOPE_PROTOTYPE)
    Encoder feignFormEncoder() {
        return new FormEncoder();
    }

    private static class FormEncoder implements Encoder {

        @Override
        public void encode(Object o, Type type, RequestTemplate rt) throws EncodeException {
            Collection<String> contentTypes = rt.headers().get("Content-Type");
            if (contentTypes == null || contentTypes.contains("application/json")) {
            	rt.header("Content-Type", "application/json");
                rt.body(JsonUtil.convert(o));
            } else {
                if(!(o instanceof Map))
                    throw new EncodeException("Can only encode Map data");

                Map m = (Map)o;

                // XXX: quick n dirty!
                StringBuilder sb = new StringBuilder();

                for(Object k: m.keySet()) {
                    if(!(k instanceof String))
                        throw new EncodeException("Can only encode String keys");

                    if(sb.length() > 0)
                        sb.append("&");

                    Object v = m.get(k);
                    if(!(v instanceof String))
                        throw new EncodeException("Can only encode String values");

                    try {
                        sb.append(URLEncoder.encode((String)k, "UTF-8"))
                                .append("=")
                                .append(URLEncoder.encode((String)v, "UTF-8"));
                    }catch(UnsupportedEncodingException ex) {
                        throw new EncodeException("Invalid encoding", ex);
                    }
                }
//                rt.header("Content-Type", "application/x-www-form-urlencoded");
                rt.body(sb.toString());
            }
            }


    }

}
