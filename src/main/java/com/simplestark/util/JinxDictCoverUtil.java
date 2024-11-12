package com.simplestark.util;

import com.simplestark.DictPint;
import com.simplestark.cache.JinxDictCache;
import com.simplestark.entity.JinxDict;
import com.simplestark.entity.JinxDictVo;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangruoheng
 */
@Slf4j
public class JinxDictCoverUtil {

    Pattern dictCodeMeanPattern = Pattern.compile("\\[(.*?)]");
    Pattern dictCodePattern = Pattern.compile("^(.*?)\\[");

    /**
     * 字典缓存容器
     */
    private final JinxDictCache dictCache;

    public JinxDictCoverUtil(JinxDictCache dictCache) {
        this.dictCache = dictCache;
    }

    public List<JinxDictVo> getDictList(String dictCode) {
        return dictCache.getDictList(dictCode);
    }

    public void push(String dictCode, JinxDict dict) {
        try {
            dictCache.push(dictCode, dict);
        } catch (Exception e) {
            log.warn("添加|更新 字典对象异常 dict:{} e:{}", JsonUtil.toJson(dict), e.getMessage(), e);
        }

    }

    /**
     * 批量添加、更新字典项
     *
     * @param list     对应编码下的全量字典列表
     */
    public void push(List<JinxDict> list) {
        try {
            dictCache.push(list);
        } catch (Exception e) {
            log.warn("批量添加|更新 字典对象异常 list:{} e:{}", JsonUtil.toJson(list), e.getMessage(), e);
        }

    }

    /**
     * 删除指定字典项
     *
     * @param dictCode 字典编码
     * @param code     字典值
     */
    public void remove(String dictCode, String code) {
        try {
            dictCache.remove(dictCode, code);
        } catch (Exception e) {
            log.warn("删除指定字典项异常 dictCode:{} code:{} e:{}", dictCode, code, e.getMessage(), e);
        }
    }

    /**
     * 删除字典
     *
     * @param dictCode 字典编码
     */
    public void remove(String dictCode) {
        try {
            dictCache.remove(dictCode);
        } catch (Exception e) {
            log.warn("删除字典异常 dictCode:{} e:{}", dictCode, e.getMessage(), e);
        }
    }

    /**
     * 【工具】 按照注解 转换指定对象的字典编码
     *
     * @param object 需要转换的对象
     * @param <T>    对象类型
     */
    public <T> void coverCodeToMean(T object) {
        // 判空
        if (null == object) {
            return;
        }
        // 获取类属性，转Map（key：属性名，value：属性对象）
        Map<String, Field> fieldMap = getObjectFieldMap(object);

        // 第一次遍历 - 保存所有字段的原值
        Map<String, String> originalCodeMap = new HashMap<>();
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            // 提取编码并保存到临时Map中
            try {
                Field field = entry.getValue();
                field.setAccessible(true);
                originalCodeMap.put(entry.getKey(), field.get(object).toString());
            } catch (Exception e) {
                log.warn("{} 对象字典转换发生异常：{}", object, e.getMessage(), e);
            }

        }

        // 遍历循环处理数据
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            Field field = entry.getValue();
            if (field.isAnnotationPresent(DictPint.class)) {
                // 获取对应的字典编码
                String dictCode = field.getAnnotation(DictPint.class).dictCode();
                if (StringUtils.isNotBlank(dictCode)) {
                    // 字典不为空，需要进行转换
                    try {
                        field.setAccessible(true);
                        String[] split = dictCode.split(",");
                        StringBuilder meaning = new StringBuilder();
                        String code = null;
                        for (String s : split) {
                            // 匹配字典原值
                            Matcher matcher = dictCodeMeanPattern.matcher(s);
                            if (matcher.find()) {
                                String fieldKey = matcher.group(1);
                                Field dictCodeField = fieldMap.get(fieldKey);
                                dictCodeField.setAccessible(true);
                                code = originalCodeMap.get(fieldKey);
                                Matcher matcher1 = dictCodePattern.matcher(s);
                                if (matcher1.find()) {
                                    s = matcher1.group(1);
                                }
                            } else {
                                code = originalCodeMap.get(entry.getKey());
                            }
                            // 获取当前属性对应的值
                            if (StringUtils.isNotBlank(code)) {
                                // 值不为空，进行转换
                                String separator = field.getAnnotation(DictPint.class).separator();
                                meaning.append(dictCache.getMeaning(s, code)).append(separator);
                            }
                        }
                        // 删除多余的分割符
                        meaning = new StringBuilder(meaning.substring(0, meaning.length() - 1));
                        // 获取前置属性
                        String beforeValueTo = field.getAnnotation(DictPint.class).beforeValueTo();
                        if (StringUtils.isNotBlank(beforeValueTo)) {
                            // 获取前置属性的Field,并将code 放到该属性
                            Field beforeField = fieldMap.get(beforeValueTo);
                            beforeField.setAccessible(true);
                            beforeField.set(object, code);
                        }
                        // 获取后置属性
                        String afterValueTo = field.getAnnotation(DictPint.class).afterValueTo();
                        if (StringUtils.isNotBlank(afterValueTo)) {
                            // 获取后置属性的Field,并将meaning 放到该属性
                            Field afterField = fieldMap.get(afterValueTo);
                            afterField.setAccessible(true);
                            afterField.set(object, meaning.toString());
                        } else {
                            // 后置未传值，则不管前置是否传值，都将meaning 放到当前属性
                            field.set(object, meaning.toString());
                        }
                    } catch (Exception e) {
                        log.warn("{} 对象字典转换发生异常：{}", object, e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * 【工具，重载】 按照注解 转换指定对象列表的字典编码
     *
     * @param list 需要转换的对象列表
     * @param <T>  对象类型
     */
    public <T> void coverCodeToMean(List<T> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            for (T t : list) {
                coverCodeToMean(t);
            }
        }
    }

    /**
     * 将对象的属性置入Map中，避免遍历
     *
     * @param object obj
     * @return Map(Obj Name - Obj)
     */
    private Map<String, Field> getObjectFieldMap(Object object) {
        // 获取所有属性
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields.length > 0) {
            return Arrays.stream(fields).collect(Collectors.toMap(Field::getName, f -> f));
        } else {
            return new HashMap<>(0);
        }
    }
}
