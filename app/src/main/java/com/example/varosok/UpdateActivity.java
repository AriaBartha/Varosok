package com.example.varosok;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    private EditText editTextUpdateCity;
    private EditText editTextUpdateCountry;
    private EditText editTextUpdatePopulation;
    private Button buttonUpdateItem;
    private Button buttonBackToList;
    private List<City> cities = new ArrayList<>();
    private String url = "https://retoolapi.dev/axrWrP/data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        init();
        buttonUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextUpdateCity.getText().toString();
                String country = editTextUpdateCountry.getText().toString();
                int population = Integer.parseInt(editTextUpdatePopulation.getText().toString());

                if (city.isEmpty() || country.isEmpty()){
                    Toast.makeText(UpdateActivity.this,
                            "Kötelező megadni a várost és az országot!", Toast.LENGTH_SHORT).show();
                    return;
                }

                City cityUpdated = new City(0, city, country, population);
                Gson jsonConverter = new Gson();
                UpdateActivity.RequestTask task = new UpdateActivity.RequestTask(url, "PUT", jsonConverter.toJson(cityUpdated));
                task.execute();
            }
        });
        buttonBackToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this, ListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void init(){
        editTextUpdateCity = findViewById(R.id.editTextUpdateCity);
        editTextUpdateCountry = findViewById(R.id.editTextUpdateCountry);
        editTextUpdatePopulation = findViewById(R.id.editTextUpdatePopulation);
        buttonUpdateItem = findViewById(R.id.buttonUpdateItem);
        buttonBackToList = findViewById(R.id.buttonBackToList);
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        //doInBackground metódus létrehozása a kérés elküldéséhez
        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                if (requestType.equals("PUT")) {
                    response = RequestHandler.put(requestUrl, requestParams);
                }
            }catch (IOException e) {
                Toast.makeText(UpdateActivity.this,
                        e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if(response.getResponseCode() >= 400){
                Toast.makeText(UpdateActivity.this, "Hiba történt a feldolgozás során", Toast.LENGTH_SHORT).show();
                return;
            }
            if(requestType.equals("PUT")){
                City updateCity = converter.fromJson(
                        response.getContent(), City.class);
                cities.replaceAll(city1 ->
                        city1.getId() == updateCity.getId() ? updateCity : city1);

                Toast.makeText(UpdateActivity.this, "Sikeres módosítás", Toast.LENGTH_SHORT).show();
            }
        }
    }
}