package com.simplestark.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * 字典实体类
 *
 * @author wangruoheng
 */
@Data
public class JinxDict implements Serializable {

    private static final long serialVersionUID = 1L;

    public JinxDict() {

    }

    public JinxDict(String dictCode, String code, String meaning) {
        this.dictCode = dictCode;
        this.code = code;
        this.meaning = meaning;
        this.seqNum = 0;
    }

    public JinxDict(String dictCode, String code, String meaning, Integer seqNum) {
        this.dictCode = dictCode;
        this.code = code;
        this.meaning = meaning;
        this.seqNum = seqNum;
    }

    public JinxDict(String dictCode, String code, String meaning, String expand) {
        this.dictCode = dictCode;
        this.code = code;
        this.meaning = meaning;
        this.seqNum = 0;
        this.expand = expand;
    }

    public JinxDict(String dictCode, String code, String meaning, Integer seqNum, String expand) {
        this.dictCode = dictCode;
        this.code = code;
        this.meaning = meaning;
        this.seqNum = seqNum;
        this.expand = expand;
    }

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典映射（key）
     */
    private String code;

    /**
     * 字典映射（value）
     */
    private String meaning;

    /**
     * 键值对序列号（排序用）
     */
    private Integer seqNum;

    /**
     * 扩展信息
     */
    private String expand;
}
