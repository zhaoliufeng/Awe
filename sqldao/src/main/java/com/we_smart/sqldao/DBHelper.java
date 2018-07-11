package com.we_smart.sqldao;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  Created by Zhao Liufeng on 2018/4/18.
 */

public class DBHelper {
    //操作原子锁 判断数据库是否空闲
    private AtomicBoolean mDataBaseFree = new AtomicBoolean(true);
    private static SQLiteDatabase mDatabase;
    private static SQLiteOpenHelper mOpenHelper;
    private static DBHelper mInstance;

    public void initDBHelper(SQLiteOpenHelper openHelper){
        mOpenHelper = openHelper;
    }

    //获取单例对象
    public static DBHelper getInstance(){
        if (mInstance == null){
            synchronized (DBHelper.class){
                if (mInstance == null){
                    mInstance = new DBHelper();
                }
            }
        }
        return mInstance;
    }

    //打开获取数据库
    public synchronized SQLiteDatabase openDatabase(){
        if (mDataBaseFree.get()){
            mDataBaseFree.set(false);
            mDatabase = mOpenHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    //关闭数据库
    public synchronized void closeDatabase(){
        if (!mDataBaseFree.get()){
            mDataBaseFree.set(true);
            mDatabase.close();
        }
    }
}
