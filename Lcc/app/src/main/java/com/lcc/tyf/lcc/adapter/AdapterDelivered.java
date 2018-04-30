package com.lcc.tyf.lcc.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lcc.tyf.lcc.DeliverInfoActivity;
import com.lcc.tyf.lcc.DeliverItemsActivity;
import com.lcc.tyf.lcc.FailedDeliveredActivity;
import com.lcc.tyf.lcc.LoginActivity;
import com.lcc.tyf.lcc.MapsActivity;
import com.lcc.tyf.lcc.PickItemsActivity;
import com.lcc.tyf.lcc.R;
import com.lcc.tyf.lcc.database.PackageDao;
import com.lcc.tyf.lcc.database.StatesDao;
import com.lcc.tyf.lcc.models.Package;
import com.lcc.tyf.lcc.utils.Extras;
import com.lcc.tyf.lcc.utils.GPSTracker;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by max on 6/6/17.
 */

public class AdapterDelivered extends ArrayAdapter<Package> {

    private final Context context;
    private final ArrayList<Package> values;
    private PackageDao packageDao;
    private StatesDao statesDao;
    private ProgressDialog progressDialog;
    private Urls urls;
    private GPSTracker gpsTracker;
    private Extras extras;

    public AdapterDelivered(Context context, ArrayList<Package> values) {
        super(context, R.layout.list_delivered, values);
        this.context = context;
        this.values = values;
        packageDao = new PackageDao(context);
        statesDao = new StatesDao(context);
        progressDialog = new ProgressDialog(context);
        urls = new Urls();
        gpsTracker = new GPSTracker(context);
        extras = new Extras();
        gpsTracker.getLocation();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_delivered, parent, false);

        TextView tv_delivered_dni = (TextView) rowView.findViewById(R.id.tv_delivered_dni);
        TextView tv_delivered_product = (TextView) rowView.findViewById(R.id.tv_delivered_product);
        TextView tv_delivered_address = (TextView) rowView.findViewById(R.id.tv_delivered_address);
        TextView tv_delivered_note = (TextView) rowView.findViewById(R.id.tv_delivered_note);
        TextView tv_delivered_contacto = (TextView) rowView.findViewById(R.id.tv_delivered_contacto);
        TextView tv_delivered_facture = (TextView) rowView.findViewById(R.id.tv_delivered_facture);
        TextView tv_delivered_entreprice = (TextView) rowView.findViewById(R.id.tv_delivered_entreprice);
        TextView tv_delivered_estimated = (TextView) rowView.findViewById(R.id.tv_delivered_estimated);
        Button btn_delivered_navigation = (Button) rowView.findViewById(R.id.btn_delivered_navigation);

        ImageView iv_update = (ImageView) rowView.findViewById(R.id.iv_update);

        final ToggleButton tb_delivered_checkPick = (ToggleButton) rowView.findViewById(R.id.tb_delivered_checkPick);
        final ToggleButton tb_delivered_uncheckPick = (ToggleButton) rowView.findViewById(R.id.tb_delivered_uncheckPick);

        String sourceString = "Cliente: " + "<b>" + values.get(position).getNombre_de_cliente() + "</b> ";
        tv_delivered_dni.setText( Html.fromHtml(sourceString) );
        tv_delivered_contacto.setText( "Contacto: " + values.get(position).getNombre_de_contacto() + ", " + values.get(position).getNumero_de_contacto());
        tv_delivered_note.setText("Nota: "  + values.get(position).getNote());
        tv_delivered_address.setText("Direccion entrega: " + values.get(position).getAddress());
        tv_delivered_facture.setText(values.get(position).getDocument_type() + ": " + values.get(position).getDocument_number());
        tv_delivered_entreprice.setText("Empresa: " + values.get(position).getCompany());
        String stimatedDate = values.get(position).getEstimated_date();
        tv_delivered_estimated.setText("Tiempo estimado:" + stimatedDate.substring(0,stimatedDate.length() - 4) );

        if( values.get(position).getStateDeliver() != -1 ){
            tb_delivered_checkPick.setVisibility(View.INVISIBLE);
            tb_delivered_uncheckPick.setVisibility(View.INVISIBLE);
        }

        btn_delivered_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String uri = "http://maps.google.com/maps?q=loc:"+String.valueOf(values.get(position).getLatitude())+","+String.valueOf(values.get(position).getLongitude())+" ("+"Arequipa"+")";
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", "", values.get(position).getLatitude(), values.get(position).getLongitude() ), "UTF-8");

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.setData(Uri.parse(uri));
                context.startActivity(intent);
            }
        });

        tb_delivered_checkPick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tb_delivered_uncheckPick.setChecked(false);

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("Entrega de paquete")
                            .setMessage("Usted desea confirmar la entrega de este paquete")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    packageDao.updatePackageStateDeliver(values.get(position).getId() , statesDao.getDelivered());
                                    updatePackageState( values.get(position).getId() , statesDao.getDelivered() , position);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    tb_delivered_checkPick.setChecked(false);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        tb_delivered_uncheckPick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked) {
                tb_delivered_checkPick.setChecked(false);
                Intent intent = new Intent(getContext(), FailedDeliveredActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("id", values.get(position).getId());
                getContext().startActivity(intent);
            }

            }
        });

        iv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Actualizar direccion de recojo")
                        .setMessage("¿Desea actualizar la direccion de este cliente?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updatePositionClient(values.get(position).getId());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((DeliverItemsActivity)context).onRefresh();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        return rowView;
    }

    public void updatePackageState(final int id, int state, final int position){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id",id);
            jsonObject.put("id_status",state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject);

        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);

        final String requestBody = jsonArray.toString();
        Log.v("DATA", requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.getUpdatePackagesStates(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("DATA", response);
                progressDialog.dismiss();
                packageDao.updatePackageStateDeliverSend(id,true);

                ((DeliverItemsActivity)context).onRefresh();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                packageDao.updatePackageStateDeliverSend(id,false);
                Toast.makeText(context, "Error de conexion",Toast.LENGTH_LONG).show();
                ((DeliverItemsActivity)context).onRefresh();
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
        queue.add(stringRequest);
    }

    public void updatePositionClient(int packageId){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = urls.getClientId() + "?id_delivery=" + String.valueOf(packageId);
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int id_client = 0;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    id_client = jsonObject.getInt("id_client");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                updateCoordinates(id_client);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Error de conexion",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);

    }

    public void updateCoordinates(int idClient){
        gpsTracker.getLocation();
        Log.v("LOCATION", String.valueOf(gpsTracker.getLatitude() )+ " "+String.valueOf(gpsTracker.getLongitude()));
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = urls.getSetCoordinates() + "?id_client=" + String.valueOf(idClient) + "&longitude="+ String.valueOf(gpsTracker.getLongitude()) + "&latitude=" + String.valueOf(gpsTracker.getLatitude());
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("DATA", response);
                ((DeliverItemsActivity)context).onRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((DeliverItemsActivity)context).onRefresh();
                progressDialog.dismiss();
                Toast.makeText(context, "Error de conexion",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

}
