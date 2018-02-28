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
    private String company;
    private String document_type;
    private String serie;
    private String document_number;

    public Info(){

    }

    public Info(Boolean success, String status, String motive, String client, String estimated_date, String company, String document_type, String serie, String document_number){
        this.success = success;
        this.status = status;
        this.motive = motive;
        this.client = client;
        this.estimated_date = estimated_date;
        this.company = company;
        this.document_type = document_type;
        this.serie = serie;
        this.document_number = document_number;
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

    public String getCompany(){return company; }

    public void setCompany(String company) {this.company = company; }

    public String getDocument_type(){return document_type; }

    public void setDocument_type(){this.document_type = document_type; }

    public String getSerie(){return serie; }

    public void setSerie(){this.serie = serie; }

    public String getDocument_number(){return document_number; }

    public void setDocument_number(){this.document_number = document_number; }
}
