package com.lcc.tyf.lcc.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Comparator;

/**
 * Created by max on 6/13/17.
 */

@DatabaseTable
public class Package implements Comparable<Package> {

    public static final String IDDB = "IDDB";
    public static final String ADDRESS = "ADDRESS";
    public static final String REFERENCE = "REFERENCE";
    public static final String ID = "ID";
    public static final String NOMBRE = "NOMBRE";
    public static final String NOMBRE_DE_CONTACTO = "NOMBRE_DE_CONTACTO";
    public static final String NUMERO_DE_CONTACTO = "NUMERO_DE_CONTACTO";
    public static final String NOMBRE_DE_CLIENTE = "NOMBRE_DE_CLIENTE";
    public static final String STATEPICK = "STATEPICK";
    public static final String STATEDELIVER = "STATEDELIVER";
    public static final String SEND = "SEND";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String IMGPATH = "IMGPATH";
    public static final String DELIVERY_ORDER = "DELIVERY_ORDER";
    public static final String DOCUMENT_TYPE = "DOCUMENT_TYPE";
    public static final String DOCUMENT_NUMBER = "DOCUMENT_NUMBER";
    public static final String COMPANY = "COMPANY";

    @DatabaseField(generatedId = true, columnName = IDDB)
    private int iddb;
    @DatabaseField(columnName = ADDRESS)
    private String address;
    @DatabaseField(columnName = REFERENCE)
    private String reference;
    @DatabaseField(columnName = ID)
    private int id;
    @DatabaseField(columnName = NOMBRE)
    private String nombre;
    @DatabaseField(columnName = NOMBRE_DE_CONTACTO)
    private String nombre_de_contacto;
    @DatabaseField(columnName = NUMERO_DE_CONTACTO)
    private String numero_de_contacto;
    @DatabaseField(columnName = NOMBRE_DE_CLIENTE)
    private String nombre_de_cliente;
    @DatabaseField(columnName = STATEPICK)
    private int statePick;
    @DatabaseField(columnName = STATEDELIVER)
    private int stateDeliver;
    @DatabaseField(columnName = SEND)
    private boolean send;
    @DatabaseField(columnName = LATITUDE)
    private double latitude;
    @DatabaseField(columnName = LONGITUDE)
    private double longitude;
    @DatabaseField(columnName = IMGPATH)
    private String imgPath;
    @DatabaseField(columnName = DELIVERY_ORDER)
    private int delivery_order;
    @DatabaseField(columnName = DOCUMENT_TYPE)
    private String document_type;
    @DatabaseField(columnName = DOCUMENT_NUMBER)
    private String document_number;
    @DatabaseField(columnName = COMPANY)
    private String company;


    public Package(){

    }

    public Package(String address, String reference, int id, String nombre, String nombre_de_contacto, String numero_de_contacto, String nombre_de_cliente, double latitude, double longitude, int delivery_order, String document_type, String document_number, String company){
        this.address = address;
        this.reference = reference;
        this.id = id;
        this.nombre = nombre;
        this.nombre_de_contacto = nombre_de_contacto;
        this.numero_de_contacto = numero_de_contacto;
        this.nombre_de_cliente = nombre_de_cliente;
        this.latitude = latitude;
        this.longitude = longitude;
        this.delivery_order = delivery_order;
        this.document_type = document_type;
        this.document_number = document_number;
        this.company = company;
    }

    public int getIddb() {
        return iddb;
    }

    public void setIddb(int iddb) {
        this.iddb = iddb;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre_de_contacto() {
        return nombre_de_contacto;
    }

    public void setNombre_de_contacto(String nombre_de_contacto) {
        this.nombre_de_contacto = nombre_de_contacto;
    }

    public String getNumero_de_contacto() {
        return numero_de_contacto;
    }

    public void setNumero_de_contacto(String numero_de_contacto) {
        this.numero_de_contacto = numero_de_contacto;
    }

    public String getNombre_de_cliente() {
        return nombre_de_cliente;
    }

    public void setNombre_de_cliente(String nombre_de_cliente) {
        this.nombre_de_cliente = nombre_de_cliente;
    }

    public int getStatePick() {
        return statePick;
    }

    public void setStatePick(int state) {
        this.statePick = state;
    }

    public int getStateDeliver() {
        return stateDeliver;
    }

    public void setStateDeliver(int stateDeliver) {
        this.stateDeliver = stateDeliver;
    }

    public boolean getSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getDelivery_order() {
        return delivery_order;
    }

    public void setDelivery_order(int delivery_order) {
        this.delivery_order = delivery_order;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public String getDocument_number() {
        return document_number;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int compareTo(Package packag) {
        if(delivery_order > packag.delivery_order){
            return 1;
        }else{
            return -1;
        }
    }


}
