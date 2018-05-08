package com.lcc.tyf.lcc.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lcc.tyf.lcc.PickItemsActivity;
import com.lcc.tyf.lcc.R;
import com.lcc.tyf.lcc.database.StatesDao;
import com.lcc.tyf.lcc.dialog.DFragment;
import com.lcc.tyf.lcc.models.Package;

import java.util.ArrayList;

/**
 * Created by max on 6/6/17.
 */

public class AdapterPicked extends ArrayAdapter<Package> {

    private final Context context;
    private final ArrayList<Package> values;
    private ArrayList<Integer> pickedStatus;
    private StatesDao statesDao;
    private int statusInProgress;
    private ArrayList<ToggleButton> tbchecks;
    private ArrayList<ToggleButton> untbchecks;

    // ESTADOS
    // SIN ESTADO -1
    // PICKED      pickState
    // UNPICKED    X

    public AdapterPicked(Context context, ArrayList<Package> values) {
        super(context, R.layout.list_picked, values);
        this.context = context;
        this.values = values;
        pickedStatus = new ArrayList<Integer>();
        statesDao = new StatesDao(context);
        statusInProgress = statesDao.getInProgress();
        tbchecks = new ArrayList<>();
        untbchecks = new ArrayList<>();
        for (int i=0;i<values.size();i++){
            pickedStatus.add(-1);
            tbchecks.add(null);
            untbchecks.add(null);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_picked, parent, false);

        TextView tv_dni = (TextView) rowView.findViewById(R.id.tv_dni);
        TextView tv_product = (TextView) rowView.findViewById(R.id.tv_product);
        TextView tv_adress = (TextView) rowView.findViewById(R.id.tv_adress);
        TextView tv_facture = (TextView) rowView.findViewById(R.id.tv_facture);
        TextView tv_entreprice = (TextView) rowView.findViewById(R.id.tv_entreprice);
        TextView tv_contacto = (TextView) rowView.findViewById(R.id.tv_contacto);
        //tv_contacto.setTypeface(null, Typeface.BOLD);

        tbchecks.set(position , (ToggleButton) rowView.findViewById(R.id.tb_checkPick));
        untbchecks.set(position, (ToggleButton) rowView.findViewById(R.id.tb_uncheckPick));

        String sourceString = "Cliente: " + "<b>" + values.get(position).getNombre_de_cliente() + "</b>";
        tv_dni.setText( Html.fromHtml(sourceString) );
        tv_contacto.setText("Contacto: " + values.get(position).getNombre_de_contacto()  + ", "+ values.get(position).getNumero_de_contacto());
        tv_adress.setText("Direccion entrega: " + values.get(position).getAddress());
        tv_facture.setText(values.get(position).getDocument_type() + ": " + values.get(position).getDocument_number());
        tv_entreprice.setText("Empresa: " + values.get(position).getCompany());
        tv_product.setText("Nota: "  + values.get(position).getNote());

        if(pickedStatus.get(position) == -1){

        }else if(pickedStatus.get(position) ==  statusInProgress){
            tbchecks.get(position).setChecked(true);
        }else{
            untbchecks.get(position).setChecked(true);
        }

        tbchecks.get(position).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    untbchecks.get(position).setChecked(false);
                    pickedStatus.set(position,statusInProgress);
                    pickedStatus.set(position,statusInProgress);
                }else{
                    untbchecks.get(position).setChecked(true);
                }
            }
        });

        untbchecks.get(position).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbchecks.get(position).setChecked(false);
                    pickedStatus.set(position, statusInProgress);

                    FragmentManager fm = ((PickItemsActivity)context).getFragmentManager();
                    DFragment dFragment = new DFragment();
                    dFragment.show(fm, "Dialog Fragment");

                    dFragment.setOnAceptListener(new DFragment.OnclickAceptListener() {
                        @Override
                        public void onclickAcept(int failId) {
                            Toast.makeText(getContext(),String.valueOf(position) + " - "+String.valueOf(failId), Toast.LENGTH_LONG).show();
                            if(failId == -1){
                                tbchecks.get(position).setChecked(true);
                                untbchecks.get(position).setChecked(false);
                                pickedStatus.set(position,statusInProgress);
                            }else{
                                pickedStatus.set(position,failId);
                            }
                        }
                    });
                }else{
                    tbchecks.get(position).setChecked(true);
                    pickedStatus.set(position, statusInProgress);
                }
            }
        });
        return rowView;
    }

    public ArrayList<Integer> getPickedStatus(){
        return pickedStatus;
    }

    public void updateStates(){
        for (int i=0; i < pickedStatus.size();i++){
            pickedStatus.set(i,statusInProgress);
            if(tbchecks.get(i) != null){
                tbchecks.get(i).setChecked(true);
            }

        }
    }

}
