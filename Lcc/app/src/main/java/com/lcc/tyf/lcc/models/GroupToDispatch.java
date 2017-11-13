package com.lcc.tyf.lcc.models;

/**
 * Created by max on 6/12/17.
 */

public class GroupToDispatch {
    private  int id;
    private String codigo;
    private int total;
    private String distance;

    public GroupToDispatch(int id, String codigo, int total, String distance){
        this.id = id;
        this.codigo = codigo;
        this.total = total;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
