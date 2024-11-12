package com.simplestark.cache.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.simplestark.cache.JinxDictCache;
import com.simplestark.entity.JinxDict;
import com.simplestark.entity.JinxDictVo;
import com.simplestark.entity.JinxSingleDict;
import com.simplestark.util.JsonUtil;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 字典缓存容器策略 —— Redis
 *
 * @author wangruoheng
 */
@Slf4j
public class JinxDictRedisCache implements JinxDictCache {

    private final RedisTemplate<String, Object> dictRedisTemplate;

    private static final String DICT_MAP_KEY = "DICT_MAP:";
    private static final String DICT_List_KEY = "DICT_LIST:";

    public JinxDictRedisCache(@Qualifier("dictRedisTemplate") RedisTemplate<String, Object> dictRedisTemplate) {
        this.dictRedisTemplate = dictRedisTemplate;
        log.info("JinxDictRedisCache success");
    }

    @Override
    public void push(List<JinxDict> list) {
        Map<String, List<JinxDict>> map = list.stream().collect(Collectors.groupingBy(JinxDict::getDictCode));
        // 使用管道批量写入
        dictRedisTemplate.executePipelined((RedisCallback<?>) connection -> {
            map.forEach((dictCode, dictList) -> {
                JinxSingleDict jinxSingleDict = new JinxSingleDict(dictList);
                Map<String, JinxDict> dictMap = jinxSingleDict.getDictMap();
                dictMap.forEach((k, v) -> connection.hashCommands().hSet((DICT_MAP_KEY + dictCode).getBytes(), k.getBytes(), JsonUtil.toJson(v).getBytes()));
                connection.stringCommands()
                    .setNX((DICT_List_KEY + dictCode).getBytes(), JsonUtil.toJson(jinxSingleDict.getDictList()).getBytes());
            });
            return null;
        });
    }

    @Override
    public void push(String dictCode, JinxDict dict) {
        dictRedisTemplate.opsForHash().putIfAbsent(DICT_MAP_KEY + dictCode, dict.getCode(), JsonUtil.toJson(dict));
        String s = (String) dictRedisTemplate.opsForValue().get(DICT_List_KEY + dictCode);
        List<JinxDictVo> list = JsonUtil.toObject(s, new TypeReference<>() {
        });
        list.add(new JinxDictVo(dict.getCode(), dict.getMeaning(), dict.getSeqNum()));
        // 排序
        list.sort(Comparator.comparingInt(JinxDictVo::getSeqNum));
        dictRedisTemplate.opsForValue().set(DICT_List_KEY + dictCode, JsonUtil.toJson(list));
    }

    @Override
    public String getMeaning(String dictCode, String code) {
        Object o = dictRedisTemplate.opsForHash().get(DICT_MAP_KEY + dictCode, code);
        JinxDict jinxDict = JsonUtil.toObject(o, JinxDict.class);
        if (Objects.nonNull(jinxDict)) {
            return jinxDict.getMeaning();
        }
        return "";
    }

    @Override
    public List<JinxDictVo> getDictList(String dictCode) {
        String s = (String) dictRedisTemplate.opsForValue().get(DICT_List_KEY + dictCode);
        List<JinxDictVo> jinxDictVos = JsonUtil.toObject(s, new TypeReference<List<JinxDictVo>>() {
        });
        if (CollectionUtils.isNotEmpty(jinxDictVos)) {
            return jinxDictVos;
        }
        return List.of();
    }

    @Override
    public void remove(String dictCode, String code) {
        // 删除 Map 指定 Key
        dictRedisTemplate.opsForHash().delete(DICT_MAP_KEY + dictCode, code);
        // 删除 List 指定 元素
        String s = (String) dictRedisTemplate.opsForValue().get(DICT_List_KEY + dictCode);
        List<JinxDictVo> redisList = JsonUtil.toObject(s, new TypeReference<List<JinxDictVo>>() {});
        redisList.removeIf(x -> x.getCode().equals(code));
        dictRedisTemplate.opsForValue().set(DICT_List_KEY + dictCode, JsonUtil.toJson(redisList));
    }

    @Override
    public void remove(String dictCode) {
        dictRedisTemplate.delete(Arrays.asList(DICT_MAP_KEY + dictCode, DICT_List_KEY + dictCode));
    }
}
