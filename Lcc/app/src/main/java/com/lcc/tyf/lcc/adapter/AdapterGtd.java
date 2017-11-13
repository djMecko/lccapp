package com.lcc.tyf.lcc.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lcc.tyf.lcc.DeliverInfoActivity;
import com.lcc.tyf.lcc.GrouptodispatchActivity;
import com.lcc.tyf.lcc.LoginActivity;
import com.lcc.tyf.lcc.MapsActivity;
import com.lcc.tyf.lcc.R;
import com.lcc.tyf.lcc.models.GroupToDispatch;
import com.lcc.tyf.lcc.utils.HandlerSharedPreferences;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by max on 6/5/17.
 */

public class AdapterGtd extends ArrayAdapter<GroupToDispatch> {

    private final Context context;
    private final ArrayList<GroupToDispatch> values;
    private Urls urls;
    private ProgressDialog progressDialog;
    private HandlerSharedPreferences hsp;

    public AdapterGtd(Context context, ArrayList<GroupToDispatch> values) {
        super(context, R.layout.list_gtd, values);
        this.context = context;
        this.values = values;
        urls = new Urls();
        progressDialog = new ProgressDialog(context);
        hsp = new HandlerSharedPreferences(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_gtd, parent, false);
        Button btn_aceptgtd = (Button) rowView.findViewById(R.id.btn_aceptgtd);
        TextView tv_code = (TextView) rowView.findViewById(R.id.tv_code);
        TextView tv_distance = (TextView) rowView.findViewById(R.id.tv_distance);
        TextView tv_amount = (TextView) rowView.findViewById(R.id.tv_amount);

        tv_code.setText("Ruta:" + values.get(position).getCodigo());
        tv_distance.setText( values.get(position).getDistance() );
        tv_amount.setText("Entregas: " +  + values.get(position).getTotal() );

        btn_aceptgtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptGtd(values.get(position).getId());
            }
        });

        return rowView;
    }

    void aceptGtd(final int gtdId){
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = urls.getAceptgtd() + "?idgtd=" + String.valueOf(gtdId) + "&id_status=3";
        Log.v("DATA",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObj = new JSONObject(response);

                    if(jsonObj.get("success").toString().equals("true")){
                        hsp.putGtdId(gtdId);
                        Intent intent = new Intent(context, MapsActivity.class);
                        intent.putExtra("id",gtdId);
                        context.startActivity(intent);
                        ((GrouptodispatchActivity)context).finish();

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(context, jsonObj.get("message").toString(),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(context, "Error de conexion",Toast.LENGTH_LONG).show();
                }


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
}
