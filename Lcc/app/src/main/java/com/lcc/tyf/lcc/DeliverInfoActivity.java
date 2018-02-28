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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lcc.tyf.lcc.adapter.AdapterGtd;
import com.lcc.tyf.lcc.adapter.AdapterInfo;
import com.lcc.tyf.lcc.models.Info;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by max on 6/8/17.
 */

public class DeliverInfoActivity extends ActionBarActivity implements View.OnClickListener{

    private Button btn_newclient;
    private Button btn_search;
    private EditText edt_package_dni;
    private ListView lv_documents;

    private ProgressDialog progressDialog;
    private Urls urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverinfo);

        toolbar();
        widgets();
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
        btn_newclient = (Button) findViewById(R.id.btn_newclient);
        btn_search = (Button) findViewById(R.id.btn_search);
        edt_package_dni = (EditText) findViewById(R.id.edt_package_dni);
        lv_documents = (ListView) findViewById(R.id.lv_documents);

        btn_newclient.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        urls = new Urls();
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

    public void loadPackageStatus(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getOnlyStatusDelivery() + "?numero_doc=" + edt_package_dni.getText().toString().replaceAll(" ", "%20");
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray jsonArray = jsonObj.getJSONArray("deliveries");
                    ArrayList<Info> values = new ArrayList<>();
                    for (int i=0;i < jsonArray.length(); i++){
                        values.add(new Info(
                                jsonArray.getJSONObject(i).getBoolean("success"),
                                jsonArray.getJSONObject(i).getString("status"),
                                jsonArray.getJSONObject(i).getString("motive"),
                                jsonArray.getJSONObject(i).getString("client"),
                                jsonArray.getJSONObject(i).getString("estimated_date"),
                                jsonArray.getJSONObject(i).getString("company"),
                                jsonArray.getJSONObject(i).getString("document_type"),
                                jsonArray.getJSONObject(i).getString("serie"),
                                jsonArray.getJSONObject(i).getString("document_number")
                        ));
                    }

                    AdapterInfo adapterInfo = new AdapterInfo(getApplicationContext(), values);
                    lv_documents.setAdapter(adapterInfo);

                    /*
                    if(jsonObj.get("success").toString().equals("true")){
                        tv_package_status.setVisibility(View.VISIBLE);
                        tv_package_motive.setVisibility(View.VISIBLE);
                        tv_package_date.setVisibility(View.VISIBLE);
                        tv_package_client.setVisibility(View.VISIBLE);
                        tv_package_status.setText("Estado: " + jsonObj.getString("status"));
                        if(jsonObj.getString("motive").toString().equals("null")){
                            tv_package_motive.setText("Motivo: " );
                        }else{
                            tv_package_motive.setText("Motivo: " + jsonObj.getString("motive"));
                        }
                        tv_package_date.setText("Fecha: " + jsonObj.getString("estimated_date"));
                        tv_package_client.setText("Cliente: " + jsonObj.getString("client"));
                    }else{

                        tv_package_status.setText("Estado: " );
                        tv_package_motive.setText("Motivo: " );
                        tv_package_date.setText("Fecha: " );
                        tv_package_client.setText("Cliente: " );

                        Toast.makeText(DeliverInfoActivity.this, jsonObj.get("message").toString(),Toast.LENGTH_LONG).show();
                    }
                    */
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(DeliverInfoActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(DeliverInfoActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_newclient:
                Intent intent = new Intent(DeliverInfoActivity.this,FormActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_search:
                loadPackageStatus();
                break;
            default:
                break;
        }
    }
}

