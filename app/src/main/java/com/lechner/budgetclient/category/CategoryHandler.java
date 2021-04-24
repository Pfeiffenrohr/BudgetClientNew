package com.lechner.budgetclient.category;

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

public class CategoryHandler {
    final int MY_SOCKET_TIMEOUT_MS  = 5000;
    public List<Category> getCategorylist() {
        return Categorylist;
    }

    private List<Category> Categorylist =   new ArrayList<Category>();
    private List<CategoryFavorits> Categoryfavoritslist = new ArrayList<CategoryFavorits>();



    public void setCategorylist(List<Category> Categorylist) {
        this.Categorylist = Categorylist;
    }

    public void setCategoryfavoritslist(List<CategoryFavorits> Categoryfavoritslist) {
        this.Categoryfavoritslist = Categoryfavoritslist;
    }

    public List<CategoryFavorits> getCategoryfavoritslist() {
        return Categoryfavoritslist;
    }

    public List<Category> sortCategoryList(List<CategoryFavorits> favorits , List<Category> Category ){
        Log.d("budgetserver","start Categorie sortList ...");
        List<Category> helper = new ArrayList<Category>();
        List<Category> newCategory = new ArrayList<Category>();
        for (int i=0; i< favorits.size();i++)
        {
            CategoryFavorits kf = favorits.get(i);
            for (int j=0; j < Category.size();j++)
            {
                Category f = Category.get(j);
                if (f.getId() == kf.getCategory())
                {
                    newCategory.add(f);
                    helper.add(f);
                    Category.remove(j);
                    continue;
                }
            }
        }
        newCategory.addAll(Category);
        Category.addAll(helper);
        return newCategory;
    }

    public void sendCategoryFavorits(String baseurl,  CategoryFavorits favorit, MainActivity ma){
        try {
            Log.d("budgetserver","SendCategoryFavorits...");
            RequestQueue queue = Volley.newRequestQueue(ma);
            JSONObject jsonBody = new JSONObject();
            Gson gson = new Gson();
            final String mRequestBody=  gson.toJson(favorit);
            //jsonBody.put("username", "Shozib@gmail.com");
            //jsonBody.put("password", "Shozib123");
            // final String mRequestBody = jsonBody.toString();
            final String url = baseurl+"categoryFavorit";
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
    public void getCategorie(String baseurl,final MainActivity ma){
        Log.d("budgetserver","start getCategorie");
        //Hoöe alle Categories
        RequestQueue queue = Volley.newRequestQueue(ma);
        String url = baseurl+"categories";
        Log.d("budgetserver",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("budgetserver","getCategories");
                List<Category> outputList;
                Gson gson = new Gson();
                Type listOfMyClassObject = new TypeToken<ArrayList<Category>>() {}.getType();
                outputList = gson.fromJson(response, listOfMyClassObject);
                //  String size = "Found "+outputList.size()+" Categories";
                //  Log.d("budgetserver",outputList.toString());
                setCategorylist(outputList);
                getCategoriesFavorits(ma.baseurl,ma);
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
        //Hole alle CategoryFavoriten
    }

    public void setCategoryItems( List<CategoryFavorits> outputFavoritList, MainActivity ma){
        Log.d("budgetserver","Set Category Itemst");
        List<Category> sortedList = sortCategoryList(outputFavoritList,getCategorylist());
        String str = new String();
        if (sortedList == null)
        {
            Log.e("budgetserver","Categorylist=leer");
            return;
        }
        Spinner spinner = ma.findViewById(R.id.categorieChoice);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(ma, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        //spinnerAdapter.add("value1");

        for (int i=0;i<sortedList.size();i++)
        {
            spinnerAdapter.add(sortedList.get(i).getName());
        }
        spinnerAdapter.notifyDataSetChanged();

    }


    public void getCategoriesFavorits(String baseurl, final MainActivity ma){
        //Hoöe alle Categories
        RequestQueue queue = Volley.newRequestQueue(ma);
        String url = baseurl+"categoryFavorits";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("budgetserver","getCategoryFavorits");
                Gson gson = new Gson();
                Type listOfMyClassObject = new TypeToken<ArrayList<CategoryFavorits>>() {}.getType();
                Categoryfavoritslist = gson.fromJson(response, listOfMyClassObject);
                //  String size = "Found "+outputList.size()+" Categories";
                //  Log.d("budgetserver",outputList.toString());
                setCategoryItems(Categoryfavoritslist,ma);
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
        //Hole alle CategoryFavoriten
    }

}

