package com.example.varosok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;


public class InsertActivity extends AppCompatActivity {

    private EditText editTextCity;
    private EditText editTextCountry;
    private EditText editTextPopulation;
    private Button buttonInsertNew;
    private Button buttonBackToMain;
    private String url = "https://retoolapi.dev/axrWrP/data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        init();
        buttonInsertNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString();
                String country = editTextCountry.getText().toString();
                int population = Integer.parseInt(editTextPopulation.getText().toString());

                if (city.isEmpty() || country.isEmpty()){
                    Toast.makeText(InsertActivity.this,
                            "Kötelező megadni a várost és az országot!", Toast.LENGTH_SHORT).show();
                    return;
                }

                City cityNew = new City(0, city, country, population);
                Gson jsonConverter = new Gson();
                RequestTask task = new RequestTask(url, "POST", jsonConverter.toJson(cityNew));
                task.execute();
            }
        });
        buttonBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void init(){
        editTextCity = findViewById(R.id.editTextCity);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextPopulation = findViewById(R.id.editTextPopulation);
        buttonInsertNew = findViewById(R.id.buttonInsertNew);
        buttonBackToMain = findViewById(R.id.buttonBackToMain);
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
                if (requestType.equals("POST")) {
                    response = RequestHandler.post(requestUrl, requestParams);
                }
            }catch (IOException e) {
                Toast.makeText(InsertActivity.this,
                        e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if(response.getResponseCode() >= 400){
                Toast.makeText(InsertActivity.this, "Hiba történt a feldolgozás során", Toast.LENGTH_SHORT).show();
                return;
            }
            if(requestType.equals("POST")){
                Toast.makeText(InsertActivity.this, "Sikeres hozzáadás", Toast.LENGTH_SHORT).show();
            }
        }
    }
}