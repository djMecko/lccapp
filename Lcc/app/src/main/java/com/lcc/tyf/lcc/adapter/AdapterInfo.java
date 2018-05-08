package com.lcc.tyf.lcc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lcc.tyf.lcc.R;
import com.lcc.tyf.lcc.models.GroupToDispatch;
import com.lcc.tyf.lcc.models.Info;

import java.util.ArrayList;

/**
 * Created by max on 1/19/18.
 */

public class AdapterInfo extends ArrayAdapter<Info> {

    private final Context context;
    private final ArrayList<Info> values;

    public AdapterInfo(Context context, ArrayList<Info> values) {
        super(context, R.layout.list_info, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_info, parent, false);

        TextView tv_package_status = (TextView) rowView.findViewById(R.id.tv_package_status);
        TextView tv_package_motive = (TextView) rowView.findViewById(R.id.tv_package_motive);
        TextView tv_package_date = (TextView) rowView.findViewById(R.id.tv_package_date);
        TextView tv_package_client = (TextView) rowView.findViewById(R.id.tv_package_client);
        TextView tv_package_company = (TextView) rowView.findViewById(R.id.tv_package_company);
        TextView tv_package_document_type = (TextView) rowView.findViewById(R.id.tv_package_document_type);
        TextView tv_package_serie = (TextView) rowView.findViewById(R.id.tv_package_serie);
        TextView tv_package_document_number = (TextView) rowView.findViewById(R.id.tv_package_document_number);


        //if(values.get(position).getSuccess().toString().equals("true")){

            if(values.get(position).getSuccess() == true){
                tv_package_status.setText("Estado: " +  values.get(position).getStatus());
            }else {
                tv_package_status.setText("Estado: " +  "Sin exito");
            }

            if(values.get(position).getMotive().equals("NULL")){
                tv_package_motive.setText("Motivo: " );
            }
            if( values.get(position).getMotive().toString().equals("PENDIENTE POR FALTA DE TIEMPO")){
                tv_package_status.setText("Estado: EN CAMINO");
                tv_package_motive.setText("Motivo: "+ values.get(position).getMotive() );

            }else{
                tv_package_motive.setText("Motivo: " + values.get(position).getMotive());
            }
            tv_package_date.setText("Fecha: " + values.get(position).getEstimated_date());
            tv_package_client.setText("Cliente: " + values.get(position).getClient());
            tv_package_company.setText("Empresa: " + values.get(position).getCompany());
            tv_package_document_type.setText("T. Documento: " + values.get(position).getDocument_type());
            tv_package_serie.setText("Serie: "+ values.get(position).getSerie());
            tv_package_document_number.setText("N. Documento: " + values.get(position).getDocument_number());


        /*
        }else{

            tv_package_status.setText("Estado: " );
            tv_package_motive.setText("Motivo: " );
            tv_package_date.setText("Fecha: " );
            tv_package_client.setText("Cliente: " );

        }
        */
        return rowView;
    }
}
