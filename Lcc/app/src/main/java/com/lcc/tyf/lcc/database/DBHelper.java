package com.lcc.tyf.lcc.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lcc.tyf.lcc.models.Package;

/**
 * Created by max on 6/13/17.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "LCC0.1.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<StatesVo, Integer> statesDao;
    private Dao<Package, Integer> packageDao;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try {
            Log.i(DBHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, StatesVo.class);
            TableUtils.createTable(connectionSource, Package.class);

        } catch (java.sql.SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(DBHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int i, int i1) {

        try {

            TableUtils.dropTable(connectionSource, StatesVo.class,true);
            TableUtils.dropTable(connectionSource, Package.class,true);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onCreate(db, connectionSource);
    }

    public Dao<StatesVo, Integer> getStatesVo() throws SQLException {
        if (statesDao == null) {
            try {
                statesDao = getDao(StatesVo.class);
            } catch (java.sql.SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return statesDao;
    }

    public Dao<Package, Integer> getPackage() throws SQLException {
        if (packageDao == null) {
            try {
                packageDao = getDao(Package.class);
            } catch (java.sql.SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return packageDao;
    }

    @Override
    public void close() {
        super.close();
        statesDao = null;
        packageDao = null;
    }
}
