package com.lcc.tyf.lcc;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lcc.tyf.lcc.adapter.AdapterDelivered;
import com.lcc.tyf.lcc.database.PackageDao;
import com.lcc.tyf.lcc.database.StatesDao;
import com.lcc.tyf.lcc.database.StatesVo;
import com.lcc.tyf.lcc.models.Package;
import com.lcc.tyf.lcc.utils.PhotoMultipartRequest;
import com.lcc.tyf.lcc.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by max on 6/7/17.
 */

public class FailedDeliveredActivity extends ActionBarActivity implements View.OnClickListener{

    private Spinner sp_fail_delivered;
    private Button btn_picture;
    private Button btn_picture_cancel;
    private Button btn_picture_acept;
    private StatesDao statesDao;
    private PackageDao packageDao;
    private ArrayList<StatesVo> failedDeliveres;
    private int id;
    private ImageView iv_house;
    private ProgressDialog progressDialog;
    private Urls urls;

    int TAKE_PHOTO_CODE = 0;
    private static  final int MY_REQUEST_WRITE_PHOTO=1;
    public static int count=0;
    private String dir;
    public String file;
    private Bitmap myBitmap;
    final List<String> list = new ArrayList<String>();
    private Boolean takePhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_faileddelivered);

        toolbar();
        widgets();

        id = getIntent().getIntExtra("id",0);
        failedDeliveres = (ArrayList<StatesVo>) statesDao.getFailedDelivered();

        ArrayAdapter<String> adapter;

        for (int i=0;i<failedDeliveres.size();i++){
            list.add(failedDeliveres.get(i).getMotive());
        }

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        sp_fail_delivered.setAdapter(adapter);

        // Toast.makeText(getApplicationContext(),list.get(position).toString(), Toast.LENGTH_LONG ).show();
        // Toast.makeText(getApplicationContext(),"HOLA",Toast.LENGTH_LONG).show();

        sp_fail_delivered.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                takePhoto = false;

                if(list.get(position).equals("LOCAL CERRADO")){
                    takePhoto = true;
                }
                if(list.get(position).equals("NO CUENTA CON EFECTIVO")){
                    takePhoto = true;
                }
                if(list.get(position).equals("MERCADERIA DANIADA")){
                    takePhoto = true;
                }

                if(takePhoto == false){
                    iv_house.setVisibility(View.INVISIBLE);
                    btn_picture.setVisibility(View.INVISIBLE);
                }else{
                    iv_house.setVisibility(View.VISIBLE);
                    btn_picture.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        sp_fail_delivered = (Spinner) findViewById(R.id.sp_fail_delivered);
        btn_picture = (Button) findViewById(R.id.btn_picture);
        btn_picture_cancel = (Button) findViewById(R.id.btn_picture_cancel);
        btn_picture_acept = (Button) findViewById(R.id.btn_picture_acept);
        iv_house = (ImageView) findViewById(R.id.iv_house);

        statesDao = new StatesDao(this);

        btn_picture_cancel.setOnClickListener(this);
        btn_picture_acept.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        packageDao = new PackageDao(this);

        progressDialog = new ProgressDialog(this);
        urls = new Urls();

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();
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
            case R.id.btn_picture:

                count++;
                file = dir+String.valueOf(id)+"_"+count+"_pf.jpg";

                File newfile =null;

                try {
                    newfile =  new File(file);
                    newfile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, MY_REQUEST_WRITE_PHOTO);

                    Uri outputFileUri;
                    Log.e("DATA", "SDK " + String.valueOf(Build.VERSION.SDK_INT ));

                    if(Build.VERSION.SDK_INT >= 24.0){
                        outputFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", newfile);
                    }else{
                        outputFileUri = Uri.fromFile(newfile);
                    }

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

                }catch (SecurityException ex){
                    Toast.makeText(this, "Permisos de cámara denegados", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_picture_cancel:

                finish();
                break;
            case R.id.btn_picture_acept:

                if(file != null || takePhoto == false){
                    /*
                    if(!myBitmap.isRecycled()){
                        myBitmap.recycle();
                    }
                    */
                    packageDao.updatePackageStateDeliver(id , failedDeliveres.get(sp_fail_delivered.getSelectedItemPosition()).getId() );
                    updatePackageState(id, failedDeliveres.get(sp_fail_delivered.getSelectedItemPosition()).getId() );

                }else{
                    Toast.makeText(this,"Usted debe tomar foto", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();

        float width = (float) wantedWidth / bitmap.getWidth();
        float heigth = (float) wantedHeight / bitmap.getHeight();
        Log.v("DATA", String.valueOf(width));
        Log.v("DATA", String.valueOf(heigth));

        m.setScale(width ,heigth );
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            //  -- Toast.makeText(getActivity(), file + " Pic Saved", Toast.LENGTH_LONG).show();
            File imgFile = null;

            try{
                imgFile = new  File(file);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            Log.v("DATA","img route" + String.valueOf(file));
            assert imgFile != null;
            if(imgFile.exists()){

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);

                if(myBitmap == null){
                    options.inSampleSize = 8;
                    myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
                }

                if(myBitmap == null){
                    options.inSampleSize = 5;
                    myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
                }

                if(myBitmap == null){
                    options.inSampleSize = 3;
                    myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
                }

                BitmapDrawable drawable = (BitmapDrawable) iv_house.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                int width = bitmap.getWidth();
                int heigth = bitmap.getHeight();
                Bitmap b = scaleBitmap(myBitmap,width,heigth);
                iv_house.setImageBitmap(b);

            }else{
                Toast.makeText(this, "Por favor, intente tomar foto nuevamente", Toast.LENGTH_SHORT).show();
            }
        }else{
            // -- Toast.makeText(getActivity(), file + " Pic not Saved", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Foto no guardada", Toast.LENGTH_LONG).show();
            iv_house.setImageResource(R.drawable.photo);
            file = null;
        }
    }

    public void updatePackageState(final int id, int state){
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
        RequestQueue queue = Volley.newRequestQueue(this);

        final String requestBody = jsonArray.toString();

        Log.v("DATA", requestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.getUpdatePackagesStates(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("DATA", response);

                packageDao.updatePackageStateDeliverSend(id,true);
                if(takePhoto == true){
                    sendImage(file);
                }else{
                    progressDialog.dismiss();
                    packageDao.updatePackageStateDeliverSend(id,true);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                packageDao.updatePackageStateDeliverSend(id,false);
                packageDao.updatePackagePath(id,file);
                Log.e("IMG", "SAVE "+ String.valueOf(id)+ " " + file);
                Toast.makeText(FailedDeliveredActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                finish();
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

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }


        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public void sendImage(String path){
        Log.e("DATA","--- Funcion ---");
        Log.e("DATA", path);


        Bitmap bmResize = decodeSampledBitmapFromFile(path, 640, 480);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmResize.compress(Bitmap.CompressFormat.JPEG, 100, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String encodedImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",id);
            jsonObject.put("photo",encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        final String requestBody = jsonObject.toString();

        Log.e("DATA", requestBody);
        Log.e("DATA", String.valueOf(requestBody.length()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.getNewPhoto(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("DATA", response);
                progressDialog.dismiss();
                packageDao.updatePackageStateDeliverSend(id,true);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("DATA","fallo");
                progressDialog.dismiss();

                //packageDao.updatePackageStateDeliverSend(id,false);
                //packageDao.updatePackagePath(id,file);

                //Toast.makeText(FailedDeliveredActivity.this, "Error de conexion",Toast.LENGTH_LONG).show();
                finish();
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
