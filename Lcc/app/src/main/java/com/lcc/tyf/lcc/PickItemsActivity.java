package com.lcc.tyf.lcc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lcc.tyf.lcc.adapter.AdapterPicked;
import com.lcc.tyf.lcc.database.PackageDao;
import com.lcc.tyf.lcc.database.StatesDao;
import com.lcc.tyf.lcc.models.Package;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by max on 6/6/17.
 */

public class PickItemsActivity extends ActionBarActivity implements View.OnClickListener {

    private ListView lv_pickeditems;
    private Button btn_aceptPicked;
    private int gtdId;
    private AdapterPicked adapterPicked;
    private Urls urls;
    private ProgressDialog progressDialog;
    private ArrayList<Package> packages;
    private PackageDao packageDao;
    private StatesDao statesDao;
    private ToggleButton tb_global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickitems);

        toolbar();
        widgets();

        progressDialog.show();
        gtdId = getIntent().getIntExtra("id",0);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getPackages() + "?gtdid=" + String.valueOf(gtdId);
        Log.e("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("DATA", response);
                progressDialog.hide();
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    packages = new ArrayList<>();
                    for(int i=0; i< jsonArray.length();i++){
                        Log.v("DATA", "String for replace" );
                        Log.v("DATA", jsonArray.getJSONObject(i).getString("latitude").replaceAll("\"","") );

                        packages.add(new Package(
                                jsonArray.getJSONObject(i).getString("address"),
                                jsonArray.getJSONObject(i).getString("note"),
                                jsonArray.getJSONObject(i).getString("reference"),
                                jsonArray.getJSONObject(i).getInt("id"),
                                jsonArray.getJSONObject(i).getString("nombre"),
                                jsonArray.getJSONObject(i).getString("nombre_de_contacto"),
                                jsonArray.getJSONObject(i).getString("numero_de_contacto"),
                                jsonArray.getJSONObject(i).getString("nombre_de_cliente"),
                                Double.valueOf(jsonArray.getJSONObject(i).getString("latitude").replaceAll("\"","")),
                                Double.valueOf(jsonArray.getJSONObject(i).getString("longitude").replaceAll("\"","")),
                                jsonArray.getJSONObject(i).getInt("delivery_order"),
                                jsonArray.getJSONObject(i).getString("document_type"),
                                jsonArray.getJSONObject(i).getString("document_number"),
                                jsonArray.getJSONObject(i).getString("company")

                                ));
                        adapterPicked = new AdapterPicked(PickItemsActivity.this, packages);
                        lv_pickeditems.setAdapter(adapterPicked);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PickItemsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PickItemsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

        tb_global.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(adapterPicked != null){
                        adapterPicked.updateStates();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void toolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        toolbar.setSubtitleTextColor(android.graphics.Color.WHITE);
    }

    public void widgets(){
        lv_pickeditems = (ListView) findViewById(R.id.lv_pickeditems);
        btn_aceptPicked = (Button) findViewById(R.id.btn_aceptPicked);

        btn_aceptPicked.setOnClickListener(this);
        urls = new Urls();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        packageDao = new PackageDao(this);
        statesDao = new StatesDao(this);
        tb_global = (ToggleButton) findViewById(R.id.tb_global);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_quit:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_aceptPicked:

                ArrayList<Integer> pickedStatus = adapterPicked.getPickedStatus();
                boolean isCompleted = true;

                for (int i=0;i<pickedStatus.size();i++){
                    if(pickedStatus.get(i) == -1){
                        isCompleted = false;
                    }
                }
                if(isCompleted){

                    for (int i=0;i<pickedStatus.size();i++){
                        packages.get(i).setStatePick(pickedStatus.get(i));
                        packages.get(i).setStateDeliver(-1);
                        packages.get(i).setSend(false);
                    }
                    updatePickStatus(packages);

                }else{
                    Toast.makeText(this,"Todos los paquetes deben tener estado",Toast.LENGTH_LONG).show();
                }

                break;
            default:
                break;

        }
    }

    public void updatePickStatus(final ArrayList<Package> packages){
        JSONArray jsonArray = new JSONArray();

        for (int i=0;i<packages.size();i++){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id",packages.get(i).getId());
                jsonObject.put("id_status",packages.get(i).getStatePick());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        final String requestBody = jsonArray.toString();
        Log.v("DATA",requestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.getUpdatePackagesStates(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                packageDao.createPackages(packages);


                /* */
                RequestQueue queue = Volley.newRequestQueue(PickItemsActivity.this);
                String url = urls.getPackages() + "?gtdid=" + String.valueOf(gtdId);
                Log.v("DATA",url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String newresponse) {
                        Log.v("DATA", newresponse);
                        try {

                            JSONArray jsonArray = new JSONArray(newresponse);
                            ArrayList<Package> newpackages = new ArrayList<>();
                            for(int i=0; i< jsonArray.length();i++){

                                //Log.v("DATA", String.valueOf(jsonArray.getJSONObject(i).getInt("id")));
                                //Log.v("DATA", jsonArray.getJSONObject(i).getString("estimated_date") );

                                packageDao.updatePackageStimatedDate(jsonArray.getJSONObject(i).getInt("id"),
                                        jsonArray.getJSONObject(i).getString("estimated_date")
                                );
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PickItemsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                        }

                        /* */

                        Log.v("DATA",newresponse);
                        progressDialog.dismiss();
                        Intent intent = new Intent(PickItemsActivity.this,DeliverItemsActivity.class);
                        startActivity(intent);
                        finish();

                        /* */
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PickItemsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);

                /* */
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(PickItemsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }

}
