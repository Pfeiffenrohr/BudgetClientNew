package com.lechner.budgetclient.konto;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lechner.budgetclient.MainActivity;
import com.lechner.budgetclient.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KontoHandler {
    final int MY_SOCKET_TIMEOUT_MS  = 5000;
    public List<Konto> getKontolist() {
        return kontolist;
    }

    private List<Konto> kontolist =   new ArrayList<Konto>();
    private List<KontoFavorits> kontofavoritslist = new ArrayList<KontoFavorits>();



    public void setKontolist(List<Konto> kontolist) {
        this.kontolist = kontolist;
    }

    public void setKontofavoritslist(List<KontoFavorits> kontofavoritslist) {
        this.kontofavoritslist = kontofavoritslist;
    }

    public List<KontoFavorits> getKontofavoritslist() {
        return kontofavoritslist;
    }

    public List<Konto> sortKontoList(List<KontoFavorits> favorits , List<Konto> konto ){
        Log.d("budgetserver","start sortList ...");
        List<Konto> helper = new ArrayList<Konto>();
        List<Konto> newKonto = new ArrayList<Konto>();
        for (int i=0; i< favorits.size();i++)
        {
            KontoFavorits kf = favorits.get(i);
            for (int j=0; j < konto.size();j++)
            {
                Konto f = konto.get(j);
                if (f.getId() == kf.getKonto())
                {
                    newKonto.add(f);
                    helper.add(f);
                    konto.remove(j);
                    continue;
                }
            }
        }
        newKonto.addAll(konto);
        konto.addAll(helper);
        return newKonto;
    }

    public void sendKontoFavorits(String baseurl,  KontoFavorits favorit, MainActivity ma){
        try {
            Log.d("budgetserver","SendKontoFavorits...");
            RequestQueue queue = Volley.newRequestQueue(ma);
            JSONObject jsonBody = new JSONObject();
            Gson gson = new Gson();
            final String mRequestBody=  gson.toJson(favorit);
            //jsonBody.put("username", "Shozib@gmail.com");
            //jsonBody.put("password", "Shozib123");
            // final String mRequestBody = jsonBody.toString();
            final String url = baseurl+"kontoFavorit";
            Log.d("budgetserver",url);
            StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
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
            putRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(putRequest);
        } catch (Exception ex ){
            System.err.println(ex.getStackTrace());
        }
    }
    public void getKonten(String baseurl,final MainActivity ma){
        //Hoöe alle Konten
        RequestQueue queue = Volley.newRequestQueue(ma);
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
                getKontenFavorits(ma.baseurl,ma);
                // JsonParser parser = new JsonParser();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("budgetserver",error.toString());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
        //Hole alle KontoFavoriten
    }

    public void setKontoItems( List<KontoFavorits> outputFavoritList, MainActivity ma){
        Log.d("budgetserver","Set Konto Itemst");
        List<Konto> sortedList = sortKontoList(outputFavoritList,getKontolist());
        String str = new String();
        if (sortedList == null)
        {
            Log.e("budgetserver","Kontolist=leer");
            return;
        }
        Spinner spinner = ma.findViewById(R.id.KontoField);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(ma, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        //spinnerAdapter.add("value1");

        for (int i=0;i<sortedList.size();i++)
        {
            spinnerAdapter.add(sortedList.get(i).getKontoname());
        }
        spinnerAdapter.notifyDataSetChanged();

    }


    public void getKontenFavorits(String baseurl, final MainActivity ma){
        //Hoöe alle Konten
        RequestQueue queue = Volley.newRequestQueue(ma);
        String url = baseurl+"kontoFavorits";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("budgetserver","getKontenFavorits");
                Gson gson = new Gson();
                Type listOfMyClassObject = new TypeToken<ArrayList<KontoFavorits>>() {}.getType();
                kontofavoritslist = gson.fromJson(response, listOfMyClassObject);
                //  String size = "Found "+outputList.size()+" Konten";
                //  Log.d("budgetserver",outputList.toString());
                setKontoItems(kontofavoritslist,ma);
                // JsonParser parser = new JsonParser();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("budgetserver",error.toString());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
        //Hole alle KontoFavoriten
    }

}
