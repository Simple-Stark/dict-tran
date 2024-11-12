package com.simplestark.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

/**
 * Json工具类
 * @author wangruoheng
 * @date 2024/5/22
 */
@Slf4j
public class JsonUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对象转Json字符串
     *
     * @param object 需要转换的对象
     * @return Json字符串
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JsonUtil 对象Json序列化异常:{}", e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * 将Json字符串转换为指定对象
     *
     * @param json  json
     * @param clazz 指定的对象类型
     * @return 指定对象
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JsonUtil Json反序列化异常:{}", e.getMessage(), e);
            throw new RuntimeException("JsonUtil 反序列化失败");
        }
    }

    /**
     * 将Json字符串转换为指定对象
     *
     * @param json           json
     * @param tTypeReference 指定的对象类型
     * @return 指定对象
     */
    public static <T> T toObject(String json, TypeReference<T> tTypeReference) {
        try {
            return objectMapper.readValue(json, tTypeReference);
        } catch (JsonProcessingException e) {
            log.error("JsonUtil Json反序列化异常:{}", e.getMessage(), e);
            throw new RuntimeException("JsonUtil 反序列化失败");
        }
    }

    /**
     * 将 Object 转换为指定对象
     *
     * @param o     对象
     * @param clazz 指定的对象类型
     * @return 指定对象
     */
    public static <T> T toObject(Object o, Class<T> clazz) {
        try {
            return objectMapper.convertValue(o, clazz);
        } catch (IllegalArgumentException e) {
            log.error("JsonUtil Json反序列化异常:{}", e.getMessage(), e);
            throw new RuntimeException("JsonUtil 反序列化失败");
        }
    }

    /**
     * 将 Object 转换为指定对象
     *
     * @param o              对象
     * @param tTypeReference 指定的对象类型
     * @return 指定对象
     */
    public static <T> T toObject(Object o, TypeReference<T> tTypeReference) {
        try {
            return objectMapper.convertValue(o, tTypeReference);
        } catch (IllegalArgumentException e) {
            log.error("JsonUtil Json反序列化异常:{}", e.getMessage(), e);
            throw new RuntimeException("JsonUtil 反序列化失败");
        }
    }
}
