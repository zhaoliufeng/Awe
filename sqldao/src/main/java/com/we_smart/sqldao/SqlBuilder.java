package com.we_smart.sqldao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.we_smart.sqldao.Annotation.DBFiled;

import java.lang.reflect.Field;

/**
 * 生成可执行的sql语句
 * Created by zhaol on 2018/4/19.
 */

class SqlBuilder {

    private static SqlBuilder mInstance;
    private SQLiteDatabase mDataBase;

    private SqlBuilder() {
        mDataBase = DBHelper.getInstance().openDatabase();
    }

    //single instance
    static SqlBuilder getInstance() {
        if (mInstance == null) {
            mInstance = new SqlBuilder();
        }
        return mInstance;
    }

    /**
     * 创建sql数据表 表名为当前类的类名
     *
     * @param clazz 需要创建表的实体类
     *              通过@DBFiled注解判断字段是否需要写入到数据库
     *              通过isPrimary表示字段是否是主键字段
     *              通过isAutoIncrement表示字段是否需要自动递增
     */
    String createTable(Class<?> clazz) {
        Field fields[] = clazz.getFields();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("CREATE TABLE IF NOT EXISTS %s(", clazz.getSimpleName()));
        //添加数据表字段(字段名 字段类型 字段附加属性)
        for (Field field : fields) {
            if (field.isAnnotationPresent(DBFiled.class)) {
                //获取字段在数据库的名称 如果没有设置 nameInDb字段 默认为字段名
                String fieldName = field.getAnnotation(DBFiled.class).nameInDb().equals("") ?
                        field.getName() : field.getAnnotation(DBFiled.class).nameInDb();

                sb.append(fieldName).append(" ").append(SqlType.getStringType(field));
                if (field.getAnnotation(DBFiled.class).isPrimary()) {
                    sb.append(" ").append("PRIMARY KEY");
                }

                if (field.getAnnotation(DBFiled.class).isAutoIncrement()) {
                    sb.append(" ").append("AUTOINCREMENT");
                }
                sb.append(",");
            }
        }
        //删除最后一个多余的，
        sb.deleteCharAt(sb.length() - 1);
        return sb.append(")").toString();
    }

    /**
     * 插入数据
     *
     * @param obj 需要写入数据库的对象实体
     * @return 是否插入成功
     */
    boolean insertObject(Object obj) {
        long row;
        try {
            String tableName = obj.getClass().getSimpleName();
            Field fields[] = obj.getClass().getFields();
            ContentValues contentValues = new ContentValues();
            for (Field field : fields) {
                if (field.isAnnotationPresent(DBFiled.class)) {
                    //如果字段是自动递增的 则不为改字段插入数据
                    if (!field.getAnnotation(DBFiled.class).isAutoIncrement()) {
                        putValues(contentValues, obj, field);
                    }

                }
            }
            checkDatabases();
            row = mDataBase.insert(tableName, null, contentValues);
            return row != -1;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新数据
     *
     * @param obj 更新的数据对象实体
     * @return 是否更新成功
     */
    public boolean updateObject(Object obj, String[] whereKey) {
        long row;
        try {
            String tableName = obj.getClass().getSimpleName();
            Field fields[] = obj.getClass().getFields();
            ContentValues contentValues = new ContentValues();
            String[] whereValue = null;
            if (whereKey != null &&
                    whereKey.length != 0) {
                whereValue = new String[whereKey.length];
            }

            for (Field field : fields) {
                if (field.isAnnotationPresent(DBFiled.class)) {
                    if (!field.getAnnotation(DBFiled.class).isAutoIncrement() ||
                            !field.getAnnotation(DBFiled.class).isPrimary()) {
                        putValues(contentValues, obj, field);
                    }
                }

                if (whereKey != null) {
                    //查找where key对应的属性值
                    for (int i = 0; i < whereKey.length; i++) {
                        if (whereKey[i].equals(field.getName())) {
                            whereValue[i] = String.valueOf(field.get(obj));
                            break;
                        }
                    }
                }
            }
            checkDatabases();
            row = mDataBase.update(tableName, contentValues, getSelection(whereKey), whereValue);
            return row != -1;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除数据
     */

    boolean deleteObject(Object obj, String[] whereKey) {
        long row;
        try {
            String tableName = obj.getClass().getSimpleName();
            Field fields[] = obj.getClass().getFields();

            String[] whereValue = null;
            if (whereKey != null &&
                    whereKey.length != 0) {
                whereValue = new String[whereKey.length];
            }

            for (Field field : fields) {
                if (whereKey != null) {
                    //查找where key对应的属性值
                    for (int i = 0; i < whereKey.length; i++) {
                        if (whereKey[i].equals(field.getName())) {
                            whereValue[i] = String.valueOf(field.get(obj));
                            break;
                        }
                    }
                }
            }
            checkDatabases();
            row = mDataBase.delete(tableName, getSelection(whereKey), whereValue);
            return row != -1;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    //查
    protected Cursor query(Class clazz, String[] whereKey, String[] whereValue) {
        return getQueryCursor(clazz, whereKey, whereValue);
    }

    /**
     * 插入或是更新时 组合contentValues信息
     *
     * @param field 字段信息
     * @throws IllegalAccessException 判断当前字段类型 存入contentValues
     */
    private void putValues(ContentValues contentValues, Object obj, Field field) throws IllegalAccessException {
        //获取字段在数据库的名称 如果没有设置 nameInDb字段 默认为字段名
        String nameInDb = field.getAnnotation(DBFiled.class).nameInDb().equals("") ?
                field.getName() : field.getAnnotation(DBFiled.class).nameInDb();
        if (TypeParser.isString(field.getType())) {
            contentValues.put(nameInDb, String.valueOf(field.get(obj)));
        } else if (TypeParser.isInteger(field.getType())) {
            contentValues.put(nameInDb, (Integer) field.get(obj));
        } else if (TypeParser.isLong(field.getType())) {
            contentValues.put(nameInDb, (Long) field.get(obj));
        } else if (TypeParser.isBoolean(field.getType())) {
            contentValues.put(nameInDb, (Boolean) field.get(obj));
        } else if (TypeParser.isFloat(field.getType())) {
            contentValues.put(nameInDb, (Float) field.get(obj));
        }
    }

    /**
     * 封装where查询语句 where key = value AND key = value
     *
     * @param whereKey where 查询key集合
     * @return where查询语句
     * 例: 传入 whereKey = {"id", "name"};
     * 返回 "id=? AND name=?"
     */
    private String getSelection(String[] whereKey) {
        StringBuilder selection = new StringBuilder();
        if (whereKey != null) {
            for (int i = 0; i < whereKey.length; i++) {
                if (i < whereKey.length - 1) {
                    selection.append(String.format("%s=?", whereKey[i])).append(" AND ");
                } else {
                    selection.append(String.format("%s=?", whereKey[i]));
                }
            }
        }
        return selection.toString();
    }


    private Cursor getQueryCursor(Class clazz, String[] whereKey, String[] whereValue) {
        String tableName = clazz.getSimpleName();
        checkDatabases();
        return mDataBase.query(
                tableName,
                null,
                whereKey == null ? null : getSelection(whereKey),
                whereValue,
                null,
                null,
                null);
    }

    private void checkDatabases(){
        if (mDataBase == null){
            mDataBase = DBHelper.getInstance().openDatabase();
        }
    }
}
