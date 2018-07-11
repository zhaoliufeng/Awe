package com.we_smart.sqldao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.we_smart.sqldao.Annotation.DBFiled;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础数据库操作类
 * Created by Zhao Liufeng on 2018/4/18.
 */

public class BaseDAO<T> {

    private Class<T> mClazz;

    protected BaseDAO(Class<T> clazz) {
        this.mClazz = clazz;
    }

    /**
     * 新建表 表名是当前实体类的类名
     */
    public void createTable(SQLiteDatabase db) {
        db.execSQL(SqlBuilder.getInstance().createTable(mClazz));
    }

    //增
    protected boolean insert(T obj) {
        return SqlBuilder.getInstance().insertObject(obj);
    }

    //删
    protected boolean delete(T obj, String... whereKey) {
        return SqlBuilder.getInstance().deleteObject(obj, whereKey);
    }

    //改
    protected boolean update(T obj, String... whereKey) {
        return SqlBuilder.getInstance().updateObject(obj, whereKey);
    }

    //查
    protected List<T> query(String[] whereValue, String... whereKey) {
        List<T> list = new ArrayList<>();
        try {
            Cursor cursor = SqlBuilder.getInstance().query(mClazz, whereKey, whereValue);
            if (cursor == null){
                return list;
            }
            Field fields[] = mClazz.getFields();
            Field dbFields[] = new Field[cursor.getColumnCount()];
            //过滤不是数据库字段的属性
            for (int i = 0, dbCount = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(DBFiled.class)) {
                    dbFields[dbCount] = fields[i];
                    dbCount++;
                }
            }
            int index[] = new int[cursor.getColumnCount()];
            for (int i = 0; i < dbFields.length; i++) {
                index[i] = cursor.getColumnIndex(dbFields[i].getName());
            }

            //获取下标
            while (cursor.moveToNext()) {
                Object obj = mClazz.newInstance();
                for (int i = 0; i < dbFields.length; i++) {
                    SqlType.setFieldValue(cursor, index[i], obj, dbFields[i]);
                }
                list.add((T) obj);
            }
            cursor.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //判断某个值是否存在
    public boolean isExists(String key, String value) {
        return query(new String[]{key}, new String[]{value}).size() != 0;
    }
}
