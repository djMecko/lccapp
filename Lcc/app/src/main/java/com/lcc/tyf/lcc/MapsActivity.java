package com.lcc.tyf.lcc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lcc.tyf.lcc.adapter.AdapterGtd;
import com.lcc.tyf.lcc.models.GroupToDispatch;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Button btn_picked;
    private Button btn_pick_navegation;
    private Urls urls;
    private ProgressDialog progressDialog;
    private int gtdId;
    private double latitude;
    private double longitude;
    private TextView tv_picked_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        toolbar();
        widgets();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gtdId = getIntent().getIntExtra("id",0);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = urls.getPickedLocation() + "?gtdid=" + String.valueOf(gtdId);
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    latitude = jsonObject.getDouble("latitude");
                    longitude = jsonObject.getDouble("longitude");

                    tv_picked_address.setText("Direccion de Recojo: " + jsonObject.getString("address") );
                    LatLng sydney = new LatLng( latitude , longitude);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo( 15.0f ));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
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
        btn_picked  = (Button) findViewById(R.id.btn_picked);
        btn_pick_navegation = (Button) findViewById(R.id.btn_pick_navegation);
        btn_picked.setOnClickListener(this);
        tv_picked_address = (TextView) findViewById(R.id.tv_picked_address);

        urls = new Urls();
        progressDialog = new ProgressDialog(this);
        btn_pick_navegation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_picked:
                Intent intent = new Intent(this, PickItemsActivity.class);
                intent.putExtra("id",gtdId);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_pick_navegation:
                //String uri = "http://maps.google.com/maps?q=loc:"+String.valueOf(latitude)+","+String.valueOf(longitude)+" ("+"Arequipa"+")";
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", "", latitude, longitude ), "UTF-8");

                Intent navegationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                navegationIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                navegationIntent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                navegationIntent.setData(Uri.parse(uri));
                startActivity(navegationIntent);

                break;
            default:
                break;
        }
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
