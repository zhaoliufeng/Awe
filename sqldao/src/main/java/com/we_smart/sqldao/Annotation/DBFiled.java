package com.we_smart.sqldao.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库字段注解
 * Created by Zhao Liufeng on 2018/4/19.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBFiled {
    //是否是主键
    boolean isPrimary() default false;

    //是否自动递增
    boolean isAutoIncrement() default false;

    //是否是长文本
    boolean isText() default false;

    //字段在数据库中的显示名称
    String nameInDb() default "";
}
