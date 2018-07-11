package com.we_smart.sqldao;

/**
 * 解析字段类型 将java数据类型转化成sqllite数据类型
 */
public class TypeParser {
    protected static boolean isInteger(Class<?> clazz) {
        return clazz == int.class ||
                clazz == Integer.class ||
                clazz == short.class ||
                clazz == Short.class ||
                clazz == Byte.class ||
                clazz == byte.class;
    }

    protected static boolean isLong(Class<?> clazz) {
        return clazz == long.class ||
                clazz == Long.class;
    }

    protected static boolean isFloat(Class<?> clazz) {
        return clazz == Float.class ||
                clazz == float.class ||
                clazz == Double.class ||
                clazz == double.class;
    }

    protected static boolean isString(Class<?> clazz) {
        return clazz == String.class;
    }

    protected static boolean isBoolean(Class<?> clazz) {
        return clazz == boolean.class ||
                clazz == Boolean.class;
    }
}
