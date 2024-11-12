package com.simplestark.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 字典输出Vo
 *
 * @author wangruoheng
 */
@Data
public class JinxDictVo {
    /**
     * 字典映射（key）
     */
    private String code;
    /**
     * 字典映射（value）
     */
    private String meaning;
    /**
     * 排序
     */
    @JsonIgnore
    private Integer seqNum;

    public JinxDictVo() {
    }

    public JinxDictVo(String code, String meaning) {
        this.code = code;
        this.meaning = meaning;
    }

    public JinxDictVo(String code, String meaning, Integer seqNum) {
        this.code = code;
        this.meaning = meaning;
        this.seqNum = seqNum;
    }
}
