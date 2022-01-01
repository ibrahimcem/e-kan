package com.icu.kandeneme;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotification {

    public SendNotification(RequestQueue requestQueue,String blood,String type,String Hospital,String topic) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("to", "/topics/"+topic);
            JSONObject notification = new JSONObject();
            notification.put("title", type);
            notification.put("body", Hospital+" "+blood+" "+type);
            jsonObject.put("notification", notification);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send",
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> stringMap = new HashMap<>();
                    stringMap.put("content-type", "application/json");
                    stringMap.put("authorization","key");
                    return stringMap;
                }
            };
                requestQueue.add(request);
            }catch(JSONException e){
                e.printStackTrace();
            }
    }
}
