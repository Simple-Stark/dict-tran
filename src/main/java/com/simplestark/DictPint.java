package com.simplestark;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典转换工具注解
 *
 * @author Simple 2021/10/2
 */
@Documented
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DictPint {
    // 需要转换的字典编码 (必填)
    // 多个转换用英文逗号 , 分隔 中括号内为字典编码对应值 空值默认为当前注解所在字段值
    // ex: "ACT_TYPE,OPEN_STATUS[actStatus]"
    String dictCode();

    // 各种情况下的处理逻辑：
    // code 、meaning 对应数据库中的同名字段
    // 1.前置后置均未传值：默认将当前属性的code转换为meaning
    // 2.前置传值，后置未传值：将code 放到前置指向的属性，将meaning 放到当前属性
    // 3.前置未传值，后置传值：将code 放到当前属性，将meaning 放到后置指向的属性
    // 4.前置后置均传值：将code 放到前置指向的属性，将meaning 放到后置指向的属性
    // 5.以上情况均先处理前置字段，后处理后置字段

    // （前置）转换之前的值code 放置属性 (不传：默认不保存原code内容)
    String beforeValueTo() default "";

    // （后置）转换之后的值meaning 放置属性 (不传：默认放到当前属性)
    String afterValueTo() default "";

    // 多个字典编码转换的情况的分割符号 默认 |
    String separator() default "|";
}
