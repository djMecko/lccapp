package com.lcc.tyf.lcc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lcc.tyf.lcc.adapter.AdapterDelivered;
import com.lcc.tyf.lcc.adapter.AdapterPicked;
import com.lcc.tyf.lcc.database.PackageDao;
import com.lcc.tyf.lcc.database.StatesDao;
import com.lcc.tyf.lcc.models.GroupToDispatch;
import com.lcc.tyf.lcc.models.Package;
import com.lcc.tyf.lcc.utils.HandlerSharedPreferences;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by max on 6/6/17.
 */

public class DeliverItemsActivity extends ActionBarActivity {

    private ListView lv_deliveritems;
    private PackageDao packageDao;
    private StatesDao statesDao;
    private ProgressDialog progressDialog;
    private Urls urls;
    private HandlerSharedPreferences hsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveritems);

        toolbar();
        widgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delivered, menu);
        return true;
    }

    public void toolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        toolbar.setSubtitleTextColor(android.graphics.Color.WHITE);
    }

    public void widgets(){
        lv_deliveritems = (ListView) findViewById(R.id.lv_deliveritems);
        packageDao = new PackageDao(this);
        statesDao = new StatesDao(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        urls = new Urls();
        hsp = new HandlerSharedPreferences(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_quit:
                finish();
                return true;
            case R.id.action_refresh:
                progressDialog.show();
                sendPackagesFromSql();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void refreshAdapter(){
        boolean finishDelivers = true;

        ArrayList <Package> packages = (ArrayList<Package>) packageDao.getPackagesDelivered(statesDao.getInProgress());
        Collections.sort(packages);

        AdapterDelivered adapterDelivered = new AdapterDelivered(this,packages);
        lv_deliveritems.setAdapter(adapterDelivered);

        for (int i=0;i < packages.size();i++){
            if(packages.get(i).getStateDeliver() == -1){
                finishDelivers = false;
            }
        }

        if(finishDelivers){
            boolean sendAllPackages = true;
            Log.v("DATA", "SEND ALL PACKAGES");
            for(int i=0;i<packages.size();i++){
                if(packages.get(i).getSend() == false){
                    Log.v("DATA", String.valueOf(packages.get(i).getId() + " " + String.valueOf(packages.get(i).getSend())));
                    sendAllPackages = false;
                }
            }
            Toast.makeText(this,"Se han entregado todos los paquetes", Toast.LENGTH_LONG).show();
            if(sendAllPackages){
                closeGtd(hsp.getGtdId());
                packageDao.clearPackages();

            }else{
                Intent intent = new Intent(DeliverItemsActivity.this,LoginActivity.class);
                startActivity(intent);
                Toast.makeText(this,"Conectese a internet para sincronizar informacion", Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshAdapter();
    }

    public void onRefresh(){
        refreshAdapter();
    }

    void closeGtd(final int gtdId){
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getAceptgtd() + "?idgtd=" + String.valueOf(gtdId) + "&id_status=4";
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObj = new JSONObject(response);

                    if(jsonObj.get("success").toString().equals("true")){

                    }else{
                        Toast.makeText(DeliverItemsActivity.this, jsonObj.get("message").toString(),Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();

                    Intent intent = new Intent(getApplicationContext(), GrouptodispatchActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(DeliverItemsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(DeliverItemsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void sendPackagesFromSql(){

        ArrayList<Package> packages = (ArrayList<Package>) packageDao.getPackages();

        JSONArray jsonArray = new JSONArray();

        Log.v("DATA","Load from SQL");

        boolean goingTosendData = true;

        for (int i=0;i<packages.size();i++){
            JSONObject jsonObject = new JSONObject();
            if(packages.get(i).getStateDeliver() != -1 ){ // Si no fue recojido
                if(packages.get(i).getSend() == false ){ // Si no se envio

                    Toast.makeText(getApplicationContext(),"Actualizando...",Toast.LENGTH_LONG).show();

                    goingTosendData = false;
                    progressDialog.hide();
                    try {
                        jsonObject.put("id",packages.get(i).getId());
                        jsonObject.put("id_status",packages.get(i).getStateDeliver());
                        if(packages.get(i).getStateDeliver() != 4 ){ // Si fue entregado, no hay foto para enviar
                            sendImage(packages.get(i).getId(),packages.get(i).getImgPath() );
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);

                }
            }

        }

        if(goingTosendData){
            progressDialog.hide();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        final String requestBody = jsonArray.toString();

        Log.v("DATA",requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.getUpdatePackagesStates(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //closeGtd(hsp.getGtdId());

                ArrayList<Package> packages = (ArrayList<Package>) packageDao.getPackages();

                Log.v("DATA", "CHANGE ALL PACKAGES");
                for (int i=0;i<packages.size();i++){

                    if(packages.get(i).getStateDeliver() != -1){ // Si no fue recojido
                        if(packages.get(i).getSend() == false ){ // Si no se envio
                            Log.v("DATA", String.valueOf(packages.get(i).getId() + " " + String.valueOf(packages.get(i).getSend())));
                            packageDao.updatePackageStateDeliverSend(packages.get(i).getId(),true);

                            if(packages.get(i).getStateDeliver() != 4 ){  // Si no esta en progreso

                                sendImage(packages.get(i).getId(),packages.get(i).getImgPath() );
                            }
                        }
                    }
                }

                //packageDao.clearPackages();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("IMG","imagen no enviada");
                progressDialog.dismiss();
                Toast.makeText(DeliverItemsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }


    public void sendImage(final int id, String path){

        try {

            Log.v("DATA","Envio de imagen "  + String.valueOf(id) + " " +path);
            Bitmap bm = BitmapFactory.decodeFile(path);
            Bitmap bitmap = Bitmap.createScaledBitmap(bm, 500, 500, true);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); //compress to which format you want.
            byte [] byte_arr = stream.toByteArray();
            //String image_str = Base64.encodeBytes(byte_arr);
            String encodedImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id",id);
                jsonObject.put("photo",encodedImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(this);

            final String requestBody = jsonObject.toString();
            /*
            Log.v("DATA", "debug");
            Log.v("DATA", path);
            Log.v("DATA", requestBody);
            Log.v("DATA", "id" + String.valueOf(id));
            */
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.getNewPhoto(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("DATA","xxxxxxxxxxxxxxxxx " + String.valueOf(id) + "  img enviada");
                    Log.v("DATA","img enviada");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DATA","xxxxxxxxxxxxxxxxx " + String.valueOf(id) + "  img fallada");
                    Log.v("DATA","img fallada");

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return String.format("application/json; charset=utf-8");
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);

        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
