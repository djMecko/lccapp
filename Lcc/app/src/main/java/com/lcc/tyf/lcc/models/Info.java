package com.lcc.tyf.lcc.models;

/**
 * Created by max on 1/19/18.
 */

public class Info {
    private Boolean success;
    private String status;
    private String motive;
    private String client;
    private String estimated_date;

    public Info(){

    }

    public Info(Boolean success, String status, String motive, String client, String estimated_date){
        this.success = success;
        this.status = status;
        this.motive = motive;
        this.client = client;
        this.estimated_date = estimated_date;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
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

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getEstimated_date() {
        return estimated_date;
    }

    public void setEstimated_date(String estimated_date) {
        this.estimated_date = estimated_date;
    }
}
