package com.lcc.tyf.lcc.database;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.lcc.tyf.lcc.models.Package;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 6/15/17.
 */

public class PackageDao {

    private DBHelper mDBHelper;
    private Context context;
    Dao dao;

    public PackageDao(Context context){
        this.context = context;
        dao = getHelper().getPackage();
    }

    private DBHelper getHelper() {
        if (mDBHelper == null) {
            mDBHelper = OpenHelperManager.getHelper(context, DBHelper.class);
        }
        return mDBHelper;
    }

    public void createPackages( ArrayList<Package> vehicles){
        for (int i = 0; i < vehicles.size() ; i++) {
            try {
                dao.create(vehicles.get(i));
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public List<Package> getPackages(){
        List <Package> data = new ArrayList<Package>();

        try {
            data = dao.queryForAll();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

    public List<Package> getPackagesDelivered(int status){
        List <Package> data = new ArrayList<Package>();

        try {
            data = dao.queryForEq("statePick",status);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

    public void updatePackageStatePick(int id, int state){
        Package packages = new Package();
        UpdateBuilder<Package,Intent> updateBuilder = dao.updateBuilder();
        try {
            updateBuilder.where().eq("id",id);
            updateBuilder.updateColumnValue("statePick", state);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePackageStateDeliver(int id, int state){
        Package packages = new Package();
        UpdateBuilder<Package,Intent> updateBuilder = dao.updateBuilder();
        try {
            updateBuilder.where().eq("id",id);
            updateBuilder.updateColumnValue("stateDeliver", state);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePackageStateDeliverSend(int id, boolean send){
        Package packages = new Package();
        UpdateBuilder<Package,Intent> updateBuilder = dao.updateBuilder();
        try {
            updateBuilder.where().eq("id",id);
            updateBuilder.updateColumnValue("send", send);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePackageStimatedDate(int id, String estimated_date){
        Package packages = new Package();
        UpdateBuilder<Package,Intent> updateBuilder = dao.updateBuilder();

        Log.v("DATA", "value");
        Log.v("DATA", String.valueOf(id));
        Log.v("DATA", estimated_date);

        try {
            updateBuilder.where().eq("id",id);
            updateBuilder.updateColumnValue("estimated_date", estimated_date);
            updateBuilder.update();
            Log.v("DATA", "exitoso el update");
        } catch (SQLException e) {
            e.printStackTrace();

            Log.v("DATA", "fallo el update");
        }

        List<Package> packagesitos = getPackages();
        Log.v("DATA", "total packges" + String.valueOf( packagesitos.size() ));
        for(int i = 0;i < packagesitos.size(); i++ ){
            Log.v("DATA", String.valueOf( packagesitos.get(i).getId()));
            if(packagesitos.get(i).getId() == id){
                Log.v("DATA", String.valueOf(id) + " " +packagesitos.get(i).getEstimated_date()  );
            }
        }

    }

    public void updatePackagePath(int id, String imgPath){
        Package packages = new Package();
        UpdateBuilder<Package,Intent> updateBuilder = dao.updateBuilder();
        try {
            updateBuilder.where().eq("id",id);
            updateBuilder.updateColumnValue("imgPath", imgPath);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearPackages(){

        List<Package> data = getPackages();
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
