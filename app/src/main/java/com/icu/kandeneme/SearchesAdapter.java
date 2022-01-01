package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SearchesAdapter extends RecyclerView.Adapter<SearchesAdapter.MyViewHolder> {
    private final ArrayList<CardModel> cardModels;
    private final LayoutInflater inflater;
    RequestQueue requestQueue;
    Context context;

    public SearchesAdapter(Context context, ArrayList<CardModel> cardModels) {
        inflater = LayoutInflater.from(context);
        requestQueue= Volley.newRequestQueue(context);
        this.cardModels = cardModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_searches, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardModel selectedCardModel = cardModels.get(position);
        holder.setData(selectedCardModel, position);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("kandeneme", Context.MODE_PRIVATE);
                String email = sharedPreferences.getString("userEmail", "");
                String date = selectedCardModel.getDateOfSearch();
                String url = "https://e-kan.herokuapp.com/delete";
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("userEmail",email);
                    jsonObject.put("date",date);
                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String oncomplete="oncomplete";
                            try {
                                if (response.getString("oncomplete").equals(oncomplete)) {
                                    Toast.makeText(context, "Kayıt Silindi.", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "Bir sorunla karşılaşıldı, daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();

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
                    }){
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
                    Toast.makeText(context,"Bir sorunla karşılaşıldı, daha sonra tekrar deneyiniz.",Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return cardModels.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtNameSurname;
        private final TextView txtDateOfSearch;
        private final TextView txtBlood;
        private final TextView txtCity;
        private final TextView txtContactNumber;
        private final TextView txtType;
        private final TextView txtHospital;
        private final Button deleteButton;

        public MyViewHolder(View itemView) {
            super(itemView);


            txtNameSurname = itemView.findViewById(R.id.txtNameSurname2);
            txtDateOfSearch = itemView.findViewById(R.id.txtDateOfSearch2);
            txtBlood = itemView.findViewById(R.id.txtBlood2);
            txtCity = itemView.findViewById(R.id.txtCity2);
            txtContactNumber = itemView.findViewById(R.id.txtContactNumber2);
            txtHospital = itemView.findViewById(R.id.txtHospital2);
            txtType = itemView.findViewById(R.id.txtType2);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        @SuppressLint("SetTextI18n")
        public void setData(CardModel selectedModelExample, int position) {
            String nameSurname = selectedModelExample.getNameSurname();
            String blood = selectedModelExample.getBlood();
            String dateOfSearch = String.valueOf(selectedModelExample.getDateOfSearch());
            String city = selectedModelExample.getCity();
            String contactNumber = selectedModelExample.getContact();
            String hospital = selectedModelExample.getHospital();
            String type = selectedModelExample.getType();

            txtNameSurname.setText(nameSurname);
            txtDateOfSearch.setText(dateOfSearch);
            txtBlood.setText(blood);
            txtCity.setText(city);
            txtContactNumber.setText(contactNumber);
            txtHospital.setText(hospital);
            txtType.setText(type);


        }
    }
}
