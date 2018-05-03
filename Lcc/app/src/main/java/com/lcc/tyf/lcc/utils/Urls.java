package com.lcc.tyf.lcc.utils;

/**
 * Created by max on 6/11/17.
 */

public class Urls {
    public static String base_url;

    public Urls(){
        // desarrollo
        //base_url = "http://192.168.1.3/lcc/public/";
        // produccion
        base_url = "http://190.116.179.8:8080/";

    }

    public String getLogin(){
        return base_url + "loginapp";
    }

    public String getAceptgtd(){
        return base_url + "aceptgtd";
    }

    public String getServices(){
        return base_url + "services";
    }

    public String getNewClient(){
        return base_url + "newclient";
    }

    public String getPickedLocation(){
        return base_url + "pickedlocation";
    }

    public String getPackages(){
        return base_url + "packages";
    }

    public String getStatusDelivery(){
        return base_url + "getstatusdelivery";
    }

    public String getOnlyStatusDelivery(){
        return base_url + "statusdelivery";
    }

    public String getDeliveriesbycodeseller(){
        return base_url + "getdeliveriesbycodeseller";
    }

    public String getUpdatePackagesStates(){
        return base_url + "packagesstate";
    }

    public String getNewPhoto(){
        return  base_url + "newphoto";
    }

    public String getClientId(){
        return base_url + "getidclient";
    }

    public String getSetCoordinates(){
        return base_url + "setcoordenadasclient";
    }

    public String getGtdPending(){
        return base_url + "gtdpendent";
    }
}
