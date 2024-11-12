package com.simplestark.cache.impl;


import com.simplestark.cache.JinxDictCache;
import com.simplestark.entity.JinxDict;
import com.simplestark.entity.JinxDictVo;
import com.simplestark.entity.JinxSingleDict;
import com.simplestark.util.JsonUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典缓存容器策略——内存
 *
 * @author wangruoheng
 */
@Slf4j
public class JinxDictMemoryCache implements JinxDictCache {

    /**
     * 字典缓存 Map 容器
     */
    private final Map<String, JinxSingleDict> dictMap = new ConcurrentHashMap<>();

    public JinxDictMemoryCache() {

    }

    public JinxDictMemoryCache(List<JinxDict> list) {
        push(list);
        log.info("字典缓存容器初始化完成: {}", JsonUtil.toJson(this.dictMap));
    }

    @Override
    public void push(List<JinxDict> list) {
        // 根据dictCode 进行分组
        Map<String, List<JinxDict>> map = list.stream().collect(Collectors.groupingBy(JinxDict::getDictCode));
        map.forEach((k, v) -> {
            JinxSingleDict jinxSingleDict = new JinxSingleDict(v);
            dictMap.put(k, jinxSingleDict);
        });
    }

    @Override
    public void push(String dictCode, JinxDict dict) {
        if (this.dictMap.containsKey(dictCode)) {
            JinxSingleDict jinxSingleDict = this.dictMap.get(dictCode);
            jinxSingleDict.getDictList().add(new JinxDictVo(dict.getCode(), dict.getMeaning()));
            jinxSingleDict.getDictMap().put(dict.getCode(), dict);
        } else {
            List<JinxDict> list = Collections.singletonList(dict);
            push(list);
        }
    }

    @Override
    public String getMeaning(String dictCode, String code) {
        return this.dictMap.get(dictCode).getDictMap().get(code).getMeaning();
    }

    @Override
    public List<JinxDictVo> getDictList(String dictCode) {
        return this.dictMap.get(dictCode).getDictList();
    }

    @Override
    public void remove(String dictCode, String code) {
        if (this.dictMap.containsKey(dictCode)) {
            JinxSingleDict jinxSingleDict = this.dictMap.get(dictCode);
            jinxSingleDict.getDictMap().remove(code);
            jinxSingleDict.getDictList().removeIf(x -> x.getCode().equals(code));
        }
    }

    @Override
    public void remove(String dictCode) {
        this.dictMap.remove(dictCode);
    }
}
