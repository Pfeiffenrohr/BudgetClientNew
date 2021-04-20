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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MainActivity<spinnerAdapter> extends AppCompatActivity {
   //
    //String baseurl = "http://192.168.2.123:8080/";
  // String baseurl = "http://localhost:8092/";
   String baseurl;
   // String resp="";
   private PropertyReader propertyReader;
    private Context context;
    private Properties properties;
    private List<Konto> kontolist;
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
        getKonten(baseurl);
        getKontenFavorits(baseurl);
        getCategories(baseurl);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveTransaction(View view) {
        Transaction trans=new Transaction(0,"default",6,0.0,"2011-01-01","partner","beschreibung",0,0,0,"n");
      // TextView textView = (TextView)findViewById(R.id.transaktionField);
        trans.setName(""+((TextView)findViewById(R.id.transaktionField)).getText());
        trans.setWert(new Double(""+((TextView)findViewById(R.id.betragField)).getText()));
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault());
        trans.setDatum( DATE_TIME_FORMATTER.format(new Date().toInstant()));
        
       // Log.d("budgetserver", "speichern "+textView.getText());
    }

public void getKonten(String baseurl){
        //Hoöe alle Konten
    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
    String url = baseurl+"kontos";
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("budgetserver","getKonten");
            List<Konto> outputList;
            Gson gson = new Gson();
            Type listOfMyClassObject = new TypeToken<ArrayList<Konto>>() {}.getType();
            outputList = gson.fromJson(response, listOfMyClassObject);
          //  String size = "Found "+outputList.size()+" Konten";
          //  Log.d("budgetserver",outputList.toString());
            setKontolist(outputList);
            // JsonParser parser = new JsonParser();

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("budgetserver",error.toString());
        }
    });

    queue.add(stringRequest);
    //Hole alle KontoFavoriten
}

    public void getKontenFavorits(String baseurl){
        //Hoöe alle Konten
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = baseurl+"kontosFavorits";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                 Log.d("budgetserver","getKontenFavorits");
                List<KontoFavorits> outputList;
                Gson gson = new Gson();
                Type listOfMyClassObject = new TypeToken<ArrayList<KontoFavorits>>() {}.getType();
                outputList = gson.fromJson(response, listOfMyClassObject);
                //  String size = "Found "+outputList.size()+" Konten";
                //  Log.d("budgetserver",outputList.toString());
              setKontoItems(outputList);
                // JsonParser parser = new JsonParser();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("budgetserver",error.toString());
            }
        });
        queue.add(stringRequest);
        //Hole alle KontoFavoriten
    }

    public void getCategories(String baseurl){
        //Hoöe alle Konten
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = baseurl+"categories";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("budgetserver","getCategories");
                List<Categorie> outputList;
                Gson gson = new Gson();
                Type listOfMyClassObject = new TypeToken<ArrayList<Categorie>>() {}.getType();
                outputList = gson.fromJson(response, listOfMyClassObject);
                //  String size = "Found "+outputList.size()+" Konten";
                // Log.d("budgetserver",size);
                setCategorieItems(outputList);
                // JsonParser parser = new JsonParser();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("budgetserver",error.toString());
            }
        });
        queue.add(stringRequest);

    }

    private void setKontolist ( List<Konto> outputList) {

        Log.d("budgetserver","setKontoList");
        kontolist=outputList;
    }

  public void setKontoItems( List<KontoFavorits> outputFavoritList){
      Log.d("budgetserver","Set Konto Itemst");
      String str = new String();
      if (kontolist == null)
      {
          Log.e("budgetserver","Kontolist=leer");
          return;
      }
      str=str+kontolist.size();
      Log.d("budgetserver", str);
        Spinner spinner = findViewById(R.id.KontoField);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        //spinnerAdapter.add("value1");

        for (int i=0;i<kontolist.size();i++)
        {
            spinnerAdapter.add(kontolist.get(i).getKontoname());
        }
        spinnerAdapter.notifyDataSetChanged();

    }

    public void setCategorieItems(List<Categorie> outputList){
        Log.d("budgetserver","Set Categorie Items");
        Spinner spinner = findViewById(R.id.categorieChoice);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        for (int i=0;i<outputList.size();i++)
        {
            spinnerAdapter.add(outputList.get(i).getName());
        }
        spinnerAdapter.notifyDataSetChanged();

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
