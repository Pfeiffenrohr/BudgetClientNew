package com.lechner.budgetclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity<spinnerAdapter> extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setKontoItems();
        setCategorieItems();

    }

    public void sendMessage(View view) {
        Log.d("Speichern","Button pressed");

    }

  public void setKontoItems(){
      Log.d("init","Set Konto Items");
        Spinner spinner = (Spinner)findViewById(R.id.KontoField);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.add("value1");
        spinnerAdapter.add("value2");
        spinnerAdapter.add("value3");
        spinnerAdapter.notifyDataSetChanged();

    }

    public void setCategorieItems(){
        Log.d("init","Set Categorie Items");
        Spinner spinner = (Spinner)findViewById(R.id.categorieChoice);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.add("cat1");
        spinnerAdapter.add("cat2");
        spinnerAdapter.add("cat3");
        spinnerAdapter.notifyDataSetChanged();

    }

}
