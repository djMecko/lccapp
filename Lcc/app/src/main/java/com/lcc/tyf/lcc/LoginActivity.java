package com.lcc.tyf.lcc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.lcc.tyf.lcc.database.PackageDao;
import com.lcc.tyf.lcc.database.StatesDao;
import com.lcc.tyf.lcc.database.StatesVo;
import com.lcc.tyf.lcc.models.Package;
import com.lcc.tyf.lcc.utils.GPSTracker;
import com.lcc.tyf.lcc.utils.HandlerSharedPreferences;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    private Button btn_login;
    private EditText edt_dni;
    private EditText edt_password;
    private ProgressDialog progressDialog;
    private RadioGroup rbg_type;
    private Urls urls;
    private String type;
    private HandlerSharedPreferences hsp;
    private StatesDao statesDao;
    private PackageDao packageDao;
    private GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar();
        widgets();
        // Driver
        //edt_dni.setText("19483641");
        //edt_password.setText("1948");

        // Seller
        edt_dni.setText("46762585");
        edt_password.setText("4676");


        ArrayList<Package> packages = (ArrayList<Package>) packageDao.getPackagesDelivered(3);
        boolean pendingDeliver = false;
        for(int i=0;i<packages.size();i++){
            if(packages.get(i).getStateDeliver() == -1){
                pendingDeliver = true;
            }
        }

        if(pendingDeliver){
            Intent intent = new Intent(LoginActivity.this, DeliverItemsActivity.class);
            startActivity(intent);
            finish();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.rb_driver:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.rb_seller:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

    public void toolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        toolbar.setSubtitleTextColor(android.graphics.Color.WHITE);
    }

    public void widgets(){
        btn_login = (Button) findViewById(R.id.btn_login);
        edt_dni = (EditText) findViewById(R.id.edt_dni);
        edt_password = (EditText) findViewById(R.id.edt_password);
        rbg_type = (RadioGroup) findViewById(R.id.rbg_type);
        urls = new Urls();

        hsp = new HandlerSharedPreferences(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        btn_login.setOnClickListener(this);
        statesDao = new StatesDao(this);
        packageDao = new PackageDao(this);

        gpsTracker = new GPSTracker(getApplicationContext());
    }

    public void loadStates(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getStatusDelivery() ;
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("DATA",response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<StatesVo> states = new ArrayList<>();
                    //
                    for (int i = 0;i<jsonArray.length();i++){
                        states.add(new StatesVo(
                                jsonArray.getJSONObject(i).getInt("id"),
                                jsonArray.getJSONObject(i).getString("status"),
                                jsonArray.getJSONObject(i).getString("motive")
                        ));
                    }
                    //packageDao.clearPackages();
                    statesDao.clearStates();
                    statesDao.createStates(states);

                    ArrayList<Package> packages = (ArrayList<Package>) packageDao.getPackagesDelivered(statesDao.getInProgress());

                    if(packages.size() == 0){
                        pendingToPickup();

                    }else{
                        boolean pendingDeliver = false;

                        for(int i=0;i<packages.size();i++){

                            if(packages.get(i).getStateDeliver() == -1){
                                pendingDeliver = true;
                            }
                        }

                        if(pendingDeliver){
                            Toast.makeText(LoginActivity.this, "Se continuara con un pedido pendiente", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, DeliverItemsActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Hay informacion pendiente que se esta enviando", Toast.LENGTH_LONG).show();
                            sendPackagesFromSql();
                        }

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void validateLogin(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getLogin() + "?dni=" + edt_dni.getText() + "&password="+ edt_password.getText()+"&type="+type;
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObj = new JSONObject(response);

                    if(jsonObj.get("success").toString().equals("true")){

                        if(type.equals("driver")){
                            hsp.putDriverId(jsonObj.getInt("id"));
                            loadStates();

                        }else{
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, DeliverInfoActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, jsonObj.get("message").toString(),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("DATA", "error conexion");
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
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

        for (int i=0;i<packages.size();i++){
            JSONObject jsonObject = new JSONObject();
            if(packages.get(i).getStateDeliver() != -1){ // Si no fue recojido
                if(packages.get(i).getSend() == false ){ // Si no se envio
                    try {
                        jsonObject.put("id",packages.get(i).getId());
                        jsonObject.put("id_status",packages.get(i).getStateDeliver());
                        if(packages.get(i).getStateDeliver() != 4 ){
                            sendImage(packages.get(i).getId(),packages.get(i).getImgPath() );
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);
                }
            }

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        final String requestBody = jsonArray.toString();

        Log.v("DATA",requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.getUpdatePackagesStates(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                closeGtd(hsp.getGtdId());

                ArrayList<Package> packages = (ArrayList<Package>) packageDao.getPackages();

                for (int i=0;i<packages.size();i++){

                    if(packages.get(i).getStateDeliver() != -1){ // Si no fue recojido
                        if(packages.get(i).getSend() == false ){ // Si no se envio
                            if(packages.get(i).getStateDeliver() != 4 ){
                                sendImage(packages.get(i).getId(),packages.get(i).getImgPath() );
                            }
                        }
                    }
                }

                packageDao.clearPackages();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("IMG","imagen no enviada");
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                int radioButtonID =  rbg_type.getCheckedRadioButtonId();
                View radioButton = rbg_type.findViewById(radioButtonID);
                int idx = rbg_type.indexOfChild(radioButton);

                RadioButton r = (RadioButton) rbg_type.getChildAt(idx);
                String selectedtext = r.getText().toString();

                if(selectedtext.equals("Conductor")){
                    type = "driver";
                }else{
                    type = "seller";
                }
                gpsTracker = new GPSTracker(this);
                if(gpsTracker.isGpsEnabled()){
                    progressDialog.show();
                    validateLogin();
                }else{
                    Toast.makeText(getApplicationContext(),"Debe habilitas el GPS", Toast.LENGTH_LONG).show();
                }


                break;
            default:
                break;
        }

    }

    public void pendingToPickup(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getGtdPending() + "?driver_id=" + String.valueOf(hsp.getDriverId() );
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("DATA",response);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if(jsonArray.length() == 0){
                        Intent intent = new Intent(LoginActivity.this, GrouptodispatchActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        int gtdId = jsonArray.getJSONObject(0).getInt("gtd_id");
                        hsp.putGtdId(gtdId);
                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        intent.putExtra("id",gtdId);
                        finish();
                        startActivity(intent);

                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);


    }

    void closeGtd(final int gtdId){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getAceptgtd() + "?idgtd=" + String.valueOf(gtdId) + "&id_status=4";
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObj = new JSONObject(response);

                    if(jsonObj.get("success").toString().equals("true")){
                        progressDialog.dismiss();
                        packageDao.clearPackages();
                        Intent intent = new Intent(LoginActivity.this, GrouptodispatchActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, jsonObj.get("message").toString(),Toast.LENGTH_LONG).show();
                    }
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void sendImage(final int id, String path){
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

    }

}