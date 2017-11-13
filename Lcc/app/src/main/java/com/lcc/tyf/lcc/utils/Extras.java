package com.lcc.tyf.lcc.utils;

import android.util.Log;

/**
 * Created by max on 6/22/17.
 */

public class Extras {

    public Extras(){

    }

    public boolean euclideanDistance(double lat1, double  lon1,double lat2,double lon2){

        double cal = Math.sqrt( (lat2-lat1 )*(lat2-lat1) + (lon2)*(lon1) );
        Log.v("DATA",String.valueOf(cal));
        if(cal > 70){
            return true;
        }else{
            return false;
        }
    }
}
