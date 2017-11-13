package com.lcc.tyf.lcc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lcc.tyf.lcc.adapter.AdapterGtd;
import com.lcc.tyf.lcc.database.PackageDao;
import com.lcc.tyf.lcc.models.GroupToDispatch;
import com.lcc.tyf.lcc.models.Package;
import com.lcc.tyf.lcc.utils.HandlerSharedPreferences;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GrouptodispatchActivity extends ActionBarActivity {

    private ListView lv_gtd;
    private Urls urls;
    private ProgressDialog progressDialog;
    private HandlerSharedPreferences hsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouptodispatch);

        toolbar();
        widgets();

        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getServices() + "?id_driver=" + String.valueOf(hsp.getDriverId());
        Log.v("DATA", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                try {
                    JSONArray jsonArray= new JSONArray(response);

                    if(jsonArray.length() > 0){
                        ArrayList<GroupToDispatch> gtd = new ArrayList<GroupToDispatch>();
                        for (int i=0;i<jsonArray.length();i++){
                            gtd.add(new GroupToDispatch(
                                    jsonArray.getJSONObject(i).getInt("id"),
                                    jsonArray.getJSONObject(i).getString("codigo"),
                                    jsonArray.getJSONObject(i).getInt("total"),
                                    jsonArray.getJSONObject(i).getString("distance")
                            ));
                        }

                        AdapterGtd adapterGtd = new AdapterGtd(GrouptodispatchActivity.this, gtd);
                        lv_gtd.setAdapter(adapterGtd);

                    }else{
                        Toast.makeText(GrouptodispatchActivity.this, "No hay entregas disponibles",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GrouptodispatchActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GrouptodispatchActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        queue.add(stringRequest);
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
        lv_gtd = (ListView) findViewById(R.id.lv_gtd);
        progressDialog = new ProgressDialog(this);
        urls = new Urls();
        hsp = new HandlerSharedPreferences(this);
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
}
