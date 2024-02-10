package com.example.varosok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListItemsActivity extends AppCompatActivity {

    private Button buttonBack;
    private ListView listViewData;
    private List<City> cities = new ArrayList<>();
    private String url = "https://retoolapi.dev/axrWrP/data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);
        init();
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListItemsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void init(){
        buttonBack = findViewById(R.id.buttonBack);
        listViewData = findViewById(R.id.listViewData);
        listViewData.setAdapter(new CityAdapter());
    }

    private class CityAdapter extends ArrayAdapter<City> {
        public CityAdapter(){
            super(ListItemsActivity.this, R.layout.city_list_items, cities);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            LayoutInflater inflater = getLayoutInflater();
            View view  = inflater.inflate(R.layout.city_list_items, null, false);
            TextView textViewDelete = view.findViewById(R.id.textViewDelete);
            TextView textViewModify = view.findViewById(R.id.textViewModify);
            TextView textViewId = view.findViewById(R.id.textViewId);
            TextView textViewCity = view.findViewById(R.id.textViewCity);
            TextView textViewCountry = view.findViewById(R.id.textViewCountry);
            TextView textViewPopulation = view.findViewById(R.id.textViewPopulation);
            City actualCity = cities.get(position);

            textViewId.setText(String.valueOf(actualCity.getId()));
            textViewCity.setText(actualCity.getCity());
            textViewCountry.setText(actualCity.getCountry());
            textViewPopulation.setText(String.valueOf(actualCity.getPopulation()));

            textViewModify.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(ListItemsActivity.this, UpdateActivity.class);
                    //Todo: Az módosításhoz az activityt indító intentbe helyezd el az elem azonosítóját, vagy használj shared preference-t.
                    startActivity(intent);
                    finish();
                }
            });

            textViewDelete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    RequestTask task = new RequestTask(url, "DELETE", String.valueOf(actualCity.getId()));
                    task.execute();

                }
            });

            return view;
        }
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

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = com.example.varosok.RequestHandler.get(requestUrl);
                        break;
                    case "DELETE":
                        response = com.example.varosok.RequestHandler.delete(requestUrl + "/" + requestParams);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(ListItemsActivity.this,
                        e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }



        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(ListItemsActivity.this,
                        "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError:", response.getContent());
            }
            switch (requestType) {
                case "GET":
                    City[] cityArray = converter.fromJson(
                            response.getContent(), City[].class);
                    cities.clear();
                    cities.addAll(Arrays.asList(cityArray));
                    Toast.makeText(ListItemsActivity.this, "Sikeres adatlekérdezés", Toast.LENGTH_SHORT).show();
                    break;

                case "DELETE":
                    int id = Integer.parseInt(requestParams);
                    cities.removeIf(city1 -> city1.getId() == id);
                    Toast.makeText(ListItemsActivity.this, "Sikeres törlés", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}