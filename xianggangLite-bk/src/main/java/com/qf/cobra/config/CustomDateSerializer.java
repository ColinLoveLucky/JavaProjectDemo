package com.qf.cobra.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;

public class CustomDateSerializer extends JsonSerializer<Date> {

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        DateTime dateTime = new DateTime(value);
        String formattedDate = dateTime.toString("yyyy-MM-dd HH:mm:ss");
        gen.writeString(formattedDate);
    }
}
