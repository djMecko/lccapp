package com.lcc.tyf.lcc.database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by max on 6/13/17.
 */

@DatabaseTable
public class StatesVo {

    public static final String IDDB = "IDDB";
    public static final String ID = "ID";
    public static final String STATUS = "STATUS";
    public static final String MOTIVE = "MOTIVE";

    @DatabaseField(generatedId = true, columnName = IDDB)
    private int iddb;
    @DatabaseField(columnName = ID)
    private int id;
    @DatabaseField(columnName = STATUS)
    private String status;
    @DatabaseField(columnName = MOTIVE)
    private String motive;

    public StatesVo(){

    }
    public StatesVo(int id, String status, String motive){
        this.id = id;
        this.status = status;
        this.motive = motive;
    }

    public int getIddb() {
        return iddb;
    }

    public void setIddb(int iddb) {
        this.iddb = iddb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotive() {
        return motive;
    }

    public void setMotive(String motive) {
        this.motive = motive;
    }
}
