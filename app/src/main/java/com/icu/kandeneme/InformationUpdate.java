package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.icu.kandeneme.User.Blood;
import static com.icu.kandeneme.User.Cities;

public class InformationUpdate {
    public InformationUpdate(RequestQueue requestQueue, Context context, String url, JSONObject jsonObject,String key,String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences("kandeneme", MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail","");
        try {
            jsonObject.put("email", email);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("kandeneme", MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                    String oncomplete="oncomplete";
                    try {
                        if (response.getString("oncomplete").equals(oncomplete)) {
                            editor.putString(key,value);
                            editor.apply();

                    }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> stringMap = new HashMap<>();
                    stringMap.put("content-type", "application/json");
                    stringMap.put("key","key");
                    return stringMap;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Bir sorunla karşılaşıldı. Daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();

        }
    }
}
