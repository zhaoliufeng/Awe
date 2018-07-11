package com.we_smart.sqldao;

import android.database.Cursor;

import com.we_smart.sqldao.Annotation.DBFiled;

import java.lang.reflect.Field;

/**
 * Created by zhaol on 2018/4/19.
 */

class SqlType {

    static String getStringType(Field field) {
        String clazzType = field.getType().getSimpleName();
        switch (clazzType) {
            case "int":
                return "INTEGER";
            case "String":
                //判断是否需要长文本
                if (field.getAnnotation(DBFiled.class).isText()){
                    return "TEXT";
                }
                return "VARCHAR";
            default:
                return clazzType;
        }
    }

    public static void setFieldValue(Cursor cursor, int index, Object obj, Field field) throws IllegalAccessException {
        Class clazz = field.getType();
        if (clazz == String.class) {
            field.set(obj, cursor.getString(index));
        } else if (clazz == Integer.class ||
                clazz == int.class) {
            field.set(obj, cursor.getInt(index));
        } else if (clazz == Boolean.class ||
                clazz == boolean.class) {
            field.set(obj, cursor.getInt(index) != 0);
        } else if (clazz == Long.class ||
                clazz == long.class) {
            field.set(obj, cursor.getLong(index));
        }
    }
}
