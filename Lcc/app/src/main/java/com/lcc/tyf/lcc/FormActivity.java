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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lcc.tyf.lcc.utils.GPSTracker;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 6/8/17.
 */

public class FormActivity extends ActionBarActivity implements View.OnClickListener{

    private Button btn_newContact;
    private EditText edt_name;
    private EditText edt_code;
    private EditText edt_deppot;
    private Spinner spn_city;
    private EditText edt_province;
    private EditText edt_district;
    private EditText edt_reference;
    private EditText edt_address;
    private EditText edt_contact;
    private EditText edt_phone;
    private ProgressDialog progressDialog;
    private Urls urls;
    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        toolbar();
        widgets();

        List<String> list = new ArrayList<String>();
        list.add("Arequipa");
        list.add("Puno");
        list.add("Cuzco");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        spn_city.setAdapter(dataAdapter);
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
        btn_newContact = (Button) findViewById(R.id.btn_newContact);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_code = (EditText) findViewById(R.id.edt_code);
        edt_deppot = (EditText) findViewById(R.id.edt_deppot);
        spn_city = (Spinner) findViewById(R.id.spn_city);
        edt_province = (EditText) findViewById(R.id.edt_province);
        edt_district = (EditText) findViewById(R.id.edt_district);
        edt_reference = (EditText) findViewById(R.id.edt_reference);
        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_contact = (EditText) findViewById(R.id.edt_contact);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        progressDialog = new ProgressDialog(this);
        urls = new Urls();

        btn_newContact.setOnClickListener(this);
        gpsTracker = new GPSTracker(this);
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
            case R.id.btn_newContact:
                progressDialog.show();

                RequestQueue queue = Volley.newRequestQueue(this);
                gpsTracker.getLocation();
                String url = urls.getNewClient() +
                        "?namecontact=" + edt_name.getText().toString().replaceAll(" ","%20") +
                        "&code="+ edt_code.getText().toString().replaceAll(" ","%20") +
                        "&longitud="+ String.valueOf(gpsTracker.getLongitude()) +
                        "&latitud="+ String.valueOf(gpsTracker.getLatitude()) +
                        "&city="+ spn_city.getSelectedItem().toString().replaceAll(" ","%20") +
                        "&province="+ edt_province.getText().toString().replaceAll(" ","%20") +
                        "&district="+ edt_district.getText().toString().replaceAll(" ","%20") +
                        "&address="+ edt_address.getText().toString().replaceAll(" ","%20") +
                        "&reference="+ edt_reference.getText().toString().replaceAll(" ","%20") +
                        "&name=" + edt_deppot.getText().toString().replaceAll(" ","%20") +
                        "&phone=" + edt_phone.getText().toString().replaceAll(" ","%20");

                Log.v("DATA",url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        try {
                            JSONObject jsonObj = new JSONObject(response);

                            if(jsonObj.get("success").toString().equals("true")){
                                Toast.makeText(FormActivity.this, "Se agrego un nuevo cliente",Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                Toast.makeText(FormActivity.this, jsonObj.get("message").toString(),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FormActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FormActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);

                break;
            default:
                break;
        }
    }
}
