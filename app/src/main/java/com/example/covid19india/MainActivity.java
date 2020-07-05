package com.example.covid19india;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //Declaring an Spinner
    private Spinner stateList;

    //An ArrayAdapter for Spinner Items
    private ArrayAdapter<CharSequence> adapter;

    //JSON Array
    private JSONArray result;

    //JSON Object
    private JSONObject object;
    private JSONObject response1;

    //JSONObjectRequest
    private JsonObjectRequest request;

    //RequestQueue object declaration for fetching the data from API.
    private RequestQueue requestQueue;

    //TextViews to display details
    private TextView confirmed,active,recovered,death,currentData;

    //Url for fetching the data.
    String url="https://api.covid19india.org/data.json";
    String State;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing TextViews
        confirmed = (TextView) findViewById(R.id.textConfirmedNumber);
        active = (TextView) findViewById(R.id.textActiveNumber);
        recovered = (TextView) findViewById(R.id.textRecoveredNumber);
        death = (TextView) findViewById(R.id.textDeathNumber);

        currentData = (TextView) findViewById(R.id.textViewData);

        //Initializing Spinner
        stateList = (Spinner) findViewById(R.id.spinnerStateList);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this, R.array.states_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateList.setAdapter(adapter);

        //Adding an Item Selected Listener to our Spinner
        //As we have implemented the class Spinner.OnItemSelectedListener to this class iteself we are passing this to setOnItemSelectedListener
        stateList.setOnItemSelectedListener(this);

        //Creating a new Volley request for fetching the data.
        requestQueue= Volley.newRequestQueue(this);

        //Calling the method for fetching the data from the URL.
        parseJson();
    }

    //Method for fetching the API data.
    private void parseJson() {

        //Fetching JSON Object from the API.
        request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                response1 = response;
                try {
                    result = response.getJSONArray("statewise");
                    object = result.getJSONObject(0);
                    active.setText(object.getString("active"));
                    confirmed.setText(object.getString("confirmed"));
                    recovered.setText(object.getString("recovered"));
                    death.setText(object.getString("deaths"));
                    String lastUpdated="As on : "+ object.getString("lastupdatedtime");
                    currentData.setText(lastUpdated);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        //Adding the request to the RequestQueue object.
        requestQueue.add(request);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {

        //Fetching JSON Object from the API.
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array=response.getJSONArray("statewise");
                    GsonBuilder gsonBuilder=new GsonBuilder();
                    Gson gson=gsonBuilder.create();

                    //Using Gson library to convert JSON array to JAVA class objects array.
                    StateData[] stateData = gson.fromJson(String.valueOf(array), StateData[].class);

                    String StateName=stateData[i].getStateName();
                    Toast.makeText(MainActivity.this, ""+StateName, Toast.LENGTH_SHORT).show();

                    String Confirmed=stateData[i].getConfirmed();
                    String Active=stateData[i].getActive();
                    String Recovered=stateData[i].getRecovered();
                    String Deaths=stateData[i].getDeaths();

                    confirmed.setText(Confirmed);
                    active.setText(Active);
                    recovered.setText(Recovered);
                    death.setText(Deaths);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );

        requestQueue.add(request);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}