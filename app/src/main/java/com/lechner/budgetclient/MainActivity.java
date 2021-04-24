package com.lechner.budgetclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lechner.budgetclient.category.CategoryFavorits;
import com.lechner.budgetclient.category.CategoryHandler;
import com.lechner.budgetclient.konto.Konto;
import com.lechner.budgetclient.konto.KontoFavorits;
import com.lechner.budgetclient.konto.KontoHandler;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainActivity<spinnerAdapter> extends AppCompatActivity {
    //
    //String baseurl = "http://192.168.2.123:8080/";
    // String baseurl = "http://localhost:8092/";
    public String baseurl;
    // String resp="";
    private PropertyReader propertyReader;
    private Context context;
    private Properties properties;
    KontoHandler kh = new KontoHandler();
    CategoryHandler ch = new CategoryHandler();

    //private List<Category> categorielist;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //   StringRequest stringRequest;
        //   Bon bon = new Bon();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        propertyReader = new PropertyReader(context);
        properties = propertyReader.getMyProperties("budget.properties");
        this.baseurl=properties.getProperty("url");
        kh.getKonten(baseurl,this);
       // getKontenFavorits(baseurl);
        ch.getCategorie(baseurl,this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveTransaction(View view) {
        Log.d("budgetserver","saveTransaction...");
        Transaction trans=new Transaction(0,"default",6,0.0,"2011-01-01","partner","beschreibung",0,0,0,"n");
        // TextView textView = (TextView)findViewById(R.id.transaktionField);

        trans.setName(""+((TextView)findViewById(R.id.transaktionField)).getText());
        trans.setWert(new Double(""+((TextView)findViewById(R.id.betragField)).getText()));
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault());
        trans.setDatum( DATE_TIME_FORMATTER.format(new Date().toInstant()));
        //setzt Kategorie
        Log.d("budgetserver","setze Categotie...");
        String cat = ((Spinner)findViewById(R.id.categorieChoice)).getSelectedItem().toString();
        int catId =0;
        for (int i=0; i< ch.getCategorylist().size(); i++)
        {
            Category categorie= ch.getCategorylist().get(i);
            if (categorie.getName().equals(cat))
            {
                catId=categorie.getId();
                trans.setKategorie(catId);
                break;
            }
        }
      //Setze Konto
        Log.d("budgetserver","setze Konto...");
        String konto = ((Spinner)findViewById(R.id.KontoField)).getSelectedItem().toString();
        Log.d("budgetserver",konto);
        int kontoId =0;
        for (int i=0; i< kh.getKontolist().size(); i++)
        {

            Konto k= kh.getKontolist().get(i);
            if (k.getKontoname().equals(konto))
            {
                kontoId=k.getId();
                trans.setKonto_id(kontoId);
                break;
            }
        }
        sendTransaction(trans);

        setKontoFavorite( kontoId);
        setCategoryFavorite( catId);
        // Setze Konten Favorite


        // Log.d("budgetserver", "speichern "+textView.getText());
    }

    private void setKontoFavorite(int  kontoId) {
        for (int i=0; i< kh.getKontofavoritslist().size(); i++)
        {
            KontoFavorits kf = kh.getKontofavoritslist().get(i);
            if (kf.getKonto() == kontoId)
            {   int hints = kf.getHits() +1;
                kf.setHits(hints);
                kh.sendKontoFavorits(baseurl,kf,this);
                return;

            }
        }
        //Konto war noch nicht in Kontofavorits. Lege ein neues an
        KontoFavorits kf = new KontoFavorits(kontoId, kontoId,1);
        kh.sendKontoFavorits(baseurl,kf,this);
        // Log.d("budgetserver", "speichern "+textView.getText());
    }

    private void setCategoryFavorite(int  catId) {
        for (int i=0; i< ch.getCategoryfavoritslist().size(); i++)
        {
            CategoryFavorits cf = ch.getCategoryfavoritslist().get(i);
            if (cf.getCategory() == catId)
            {   int hints = cf.getHits() +1;
                cf.setHits(hints);
                ch.sendCategoryFavorits(baseurl,cf,this);
                return;

            }
        }
        //Konto war noch nicht in Kontofavorits. Lege ein neues an
        CategoryFavorits cf = new CategoryFavorits(catId, catId,1);
        ch.sendCategoryFavorits (baseurl,cf,this);
        // Log.d("budgetserver", "speichern "+textView.getText());
    }





   /* private void setKontolist ( List<Konto> outputList) {

        Log.d("budgetserver","setKontoList");
        kh.setKontolist(outputList);
        kh.getKontenFavorits(baseurl,this);
    }*/





    public void sendTransaction(Transaction trans){
        try {
            Log.d("budgetserver","SendTransaction...");
            RequestQueue queue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            Gson gson = new Gson();
            final String mRequestBody=  gson.toJson(trans);
            //jsonBody.put("username", "Shozib@gmail.com");
            //jsonBody.put("password", "Shozib123");
           // final String mRequestBody = jsonBody.toString();
            final String url = baseurl+"transaction";
            StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/json");
                    return params;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

            };

            queue.add(putRequest);
        } catch (Exception ex ){
            System.err.println(ex.getStackTrace());
        }
    }
    public class PropertyReader {

        private Context context;
        private Properties properties;

        public PropertyReader(Context context){
            this.context=context;
            properties = new Properties();
        }

        public Properties getMyProperties(String file){
            try{
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open(file);
                properties.load(inputStream);

            }catch (Exception e){
                System.out.print(e.getMessage());
            }

            return properties;
        }
    }
}
