package com.ws.mesh.awe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zhao Liufeng on 2018/4/20.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    public DBOpenHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MeshDAO.getInstance().createTable(db);
        DeviceDAO.getInstance().createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
