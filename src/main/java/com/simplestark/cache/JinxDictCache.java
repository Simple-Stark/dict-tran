package com.simplestark.cache;


import com.simplestark.entity.JinxDict;
import com.simplestark.entity.JinxDictVo;
import java.util.List;

/**
 * 字典缓存策略
 *
 * @author wangruoheng
 */
public interface JinxDictCache {

    /**
     * 批量添加、更新字典项
     *
     * @param list 字典项列表
     */
    void push(List<JinxDict> list);

    /**
     * 添加、更新单个字典项
     *
     * @param dictCode   字典编码
     * @param dict 字典对象
     */
    void push(String dictCode, JinxDict dict);

    /**
     * 根据字典编码和字典值获取字典含义
     *
     * @param dictCode 字典编码
     * @param code     字典值
     * @return 字典含义
     */
    String getMeaning(String dictCode, String code);

    /**
     * 根据字典编码获取字典列表
     *
     * @param dictCode 字典编码
     * @return 字典列表
     */
    List<JinxDictVo> getDictList(String dictCode);

    /**
     * 删除指定字典项
     *
     * @param dictCode 字典编码
     * @param code     字典值
     */
    void remove(String dictCode, String code);

    /**
     * 删除字典
     *
     * @param dictCode 字典编码
     */
    void remove(String dictCode);
}
