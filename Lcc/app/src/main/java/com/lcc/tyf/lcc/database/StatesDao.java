package com.lcc.tyf.lcc.database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 6/13/17.
 */

public class StatesDao {

    private DBHelper mDBHelper;
    private Context context;
    Dao dao;

    public StatesDao(Context context){
        this.context = context;
        dao = getHelper().getStatesVo();
    }

    private DBHelper getHelper() {
        if (mDBHelper == null) {
            mDBHelper = OpenHelperManager.getHelper(context, DBHelper.class);
        }
        return mDBHelper;
    }

    public void createStates( ArrayList<StatesVo> vehicles){
        for (int i = 0; i < vehicles.size() ; i++) {
            try {
                dao.create(vehicles.get(i));
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public List <StatesVo> getStates(){
        List <StatesVo> data = new ArrayList<StatesVo>();

        try {
            data = dao.queryForAll();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

    public List <StatesVo> getFailedPick(){
        List <StatesVo> data = new ArrayList<StatesVo>();

        try {
            data = dao.queryForEq("status","NO RECOGIDO");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

    public List <StatesVo> getFailedDelivered(){
        List <StatesVo> data = new ArrayList<StatesVo>();

        try {
            data = dao.queryForEq("status","RECHAZADO");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

    public int getInProgress(){
        List <StatesVo> data = new ArrayList<StatesVo>();
        try {
            data = dao.queryForEq("status","EN CAMINO");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data.get(0).getId();
    }

    public int getDelivered(){
        List <StatesVo> data = new ArrayList<StatesVo>();
        try {
            data = dao.queryForEq("status","ENTREGADO");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data.get(0).getId();
    }


    public void clearStates(){

        List<StatesVo> data = getStates();
        if(data != null ){
            if(data.size() > 0){
                try {
                    dao.delete(data);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
