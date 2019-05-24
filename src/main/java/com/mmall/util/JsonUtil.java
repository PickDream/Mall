package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * JSON
 * */

@Slf4j
public class JsonUtil {

    /**
     * Jackson的objectMapper
     * */
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //注意EMPTY 与 NULL的区别
        //对象的所有字段全部列入,即使有NULL或是是默认值也会被
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //取消默认转换为timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //忽略空对象的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //所有的日期都统一为以下的样式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //忽略在json字符串中存在，但是在Java对象中不存在属性的情况
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static<T> String obj2String(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj:objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error",e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回格式化好的Json串
     * 除了一般的对象之外，List等Collection类的子类都可以被正常的序列化
     * @param obj 待序列化的对象
     * */
    public static <T> String obj2StringPretty(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj:objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error",e);
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<T> clazz){
        //gard
        if (StringUtils.isEmpty(str)||clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("parse string to Object error",e);
            return null;
        }
    }

    /**
     * 解决对象带泛型参数的字符串反序列化,
     * */
    public static<T> T string2Obj(String str, TypeReference<T> typeReference){
        //gard
        if (StringUtils.isEmpty(str)||typeReference==null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)
                    ?str:objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            log.warn("parse string to Object error",e);
            return null;
        }
    }

    /**
     *
     * */
    public static <T> T string2Obj(String str,Class<?> collectionClass
            ,Class<?>... elementClasses){
         JavaType javaType = objectMapper.getTypeFactory()
                 .constructParametricType(collectionClass,elementClasses);
         try{
             return objectMapper.readValue(str,javaType);
         }catch (Exception e){
             log.warn("Parse String to Object error",e);
             return null;
         }
    }


}
