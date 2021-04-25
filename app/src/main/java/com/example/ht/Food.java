package com.example.ht;

import android.os.StrictMode;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Food {

    private String name;
    private String type;
    private int calories;

    public Food(String givenName, String givenType){
        name = givenName;
        type = givenType;
        calories = retrieveCalories(name);
    }

    private int retrieveCalories(String foodName){
        /* Fetches the requested food item's calorie information
        *  from THL's Fineli API and returns the result as an integer. */
        String baseUrl = "https://fineli.fi/fineli/api/v1/foods?q=x";
        baseUrl = baseUrl.replace("x", foodName);
        System.out.println(baseUrl);
        String temp = "";
        String JSONString = "";
        int returnValue = 0;

        /* Enables HTTP-requests from the main UI-thread. This could've (should've) been
        * implemented with asynchronous HTTP requests. */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream iS = conn.getInputStream();
            BufferedReader bR = new BufferedReader(new InputStreamReader(iS));

            /* Reads the received information into a JSON-string. */
            while ((temp = bR.readLine()) != null){
                JSONString = JSONString.concat(temp);
                JSONString = JSONString.concat("\n");
            }
            bR.close();
            iS.close();
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        try{
            /* Fineli's API results start with [square brackets] instead of {curly brackets}
            * so the file is one big array instead of an object. */
            JSONArray foodArray = new JSONArray(JSONString);
            for (int i = 0; i < foodArray.length(); i++){
                JSONObject ob = foodArray.getJSONObject(i);
                JSONObject name = ob.getJSONObject("name");
                String currentName = name.getString("fi");
                /* Restart the loop if current object isn't the correct one. */
                if (!currentName.equals(foodName))
                    continue;
                /* The calories are displayed as doubles by default so they need to be rounded
                * when converting to integers. */
                returnValue = (int) Math.round(Double.parseDouble(ob.getString("energyKcal")));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        System.out.println(returnValue);
        return returnValue;
    }

    public String getName()     {   return name;     }
    public String getType()     {   return type;     }
    public int getCalories()    {   return calories; }
}
