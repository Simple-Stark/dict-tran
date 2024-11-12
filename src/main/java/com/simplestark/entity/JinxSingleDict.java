package com.simplestark.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 单个dictCode 对应的字典
 *
 * @author wangruoheng
 */
@Data
public class JinxSingleDict {
    /**
     * 单个dictCode
     * code - SysDict 映射Map
     */
    private Map<String, JinxDict> dictMap;

    /**
     * 单个dictCode 下
     * JinxDict 列表
     */
    private List<JinxDictVo> dictList;

    public JinxSingleDict(List<JinxDict> list) {
        // 生成映射Map 放入dictMap
        this.dictMap = list.stream().collect(Collectors.toMap(JinxDict::getCode, s -> s));
        // 将排序后的字典放入dictList
        this.dictList = list.stream()
            .sorted(Comparator.comparing(JinxDict::getSeqNum))
            .map(x -> {
                // 创建 JinxDict 的副本
                JinxDictVo newDict = new JinxDictVo();
                BeanUtils.copyProperties(x, newDict);
                return newDict;
            })
            .collect(Collectors.toList());
    }

    public JinxSingleDict(JinxDict dict) {
        this.dictMap = Collections.singletonMap(dict.getCode(), dict);
        this.dictList = Collections.singletonList(new JinxDictVo(dict.getCode(), dict.getMeaning()));
    }
}
