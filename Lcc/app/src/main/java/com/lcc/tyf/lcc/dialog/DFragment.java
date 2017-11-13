package com.lcc.tyf.lcc.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.lcc.tyf.lcc.R;
import com.lcc.tyf.lcc.database.StatesDao;
import com.lcc.tyf.lcc.database.StatesVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 6/6/17.
 */

public class DFragment extends DialogFragment{

    private Spinner sp_fail_pick;
    private Button btn_fail_acept;
    private Button btn_fail_cancel;
    private DFragment dFragment;
    private StatesDao statesDao;
    private List<StatesVo> failedPickstates;

    private OnclickAceptListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialogfragment, container, false);
        getDialog().setTitle("Motivo de Fallo");

        setCancelable(false);
        
        sp_fail_pick = (Spinner) rootView.findViewById(R.id.sp_fail_pick);
        btn_fail_acept = (Button) rootView.findViewById(R.id.btn_fail_acept);
        btn_fail_cancel = (Button) rootView.findViewById(R.id.btn_fail_cancel);
        dFragment = this;
        statesDao = new StatesDao(getActivity());

        ArrayAdapter<String> adapter;
        List<String> list = new ArrayList<String>();

        failedPickstates = statesDao.getFailedPick();

        for (int i=0;i<failedPickstates.size();i++){
            list.add( failedPickstates.get(i).getMotive());
        }

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_fail_pick.setAdapter(adapter);

        btn_fail_acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dFragment.dismiss();
                listener.onclickAcept( failedPickstates.get(sp_fail_pick.getSelectedItemPosition()).getId() );
            }
        });

        btn_fail_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dFragment.dismiss();
                listener.onclickAcept( -1 );
            }
        });


        return rootView;
    }

    public interface OnclickAceptListener{
        public void onclickAcept(int failId);
    }

    public void setOnAceptListener( OnclickAceptListener l)
    {
        listener = l;
    }

}
