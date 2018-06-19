/**
 * @Title: [DateSerializer.java]
 * @Package: [com.qf.bpms.config.jackson]
 * @author: [JishengXu]
 * @CreateDate: [2017年4月17日 下午3:08:52]
 * @UpdateUser: [JishengXu]
 * @UpdateDate: [2017年4月17日 下午3:08:52]
 * @UpdateRemark: [说明本次修改内容]
 * @Description: [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
package com.quark.cobra.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: DateSerializer
 * @author: [JishengXu]
 * @CreateDate: [2017年4月17日 下午3:08:52]   
 * @UpdateUser: [JishengXu]
 * @UpdateDate: [2017年4月17日 下午3:08:52]   
 * @UpdateRemark: [说明本次修改内容]
 * @Description: [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
public class CustomDateSerializer extends JsonSerializer<Date> {

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException{
        DateTime dateTime = new DateTime(value);
        String formattedDate = dateTime.toString("yyyy-MM-dd HH:mm:ss");
        gen.writeString(formattedDate);
    }
}
