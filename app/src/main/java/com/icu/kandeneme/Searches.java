package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class Searches extends Fragment {
    RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private SearchesAdapter searchesAdapter;
    TextView textView;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<CardModel> cardModels;

    public static Searches newInstance() {
        return new Searches();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.searches, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view2);
        textView = rootView.findViewById(R.id.tv_no_data);
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        cardModels = new ArrayList<>();
        herokuAppConnect();
        searchesAdapter = new SearchesAdapter(getActivity(), cardModels);
        recyclerView.setAdapter(searchesAdapter);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.refreshDrawableState();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity().getBaseContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (cardModels.isEmpty()) {
                    textView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    cardModels.clear();
                    herokuAppConnect();
                    searchesAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    textView.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        return rootView;
    }
    private void herokuAppConnect(){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("kandeneme", MODE_PRIVATE);
        String email=sharedPreferences.getString("userEmail","");
        String url="https://e-kan.herokuapp.com/searches";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        String nameSurname = jsonObject.getString("nameSurname");
                        String dateOfSearch = jsonObject.getString("dateOfSearch");
                        String blood_or_age = jsonObject.getString("blood_or_age");
                        String city = jsonObject.getString("city");
                        String hospital = jsonObject.getString("hospital");
                        String type = jsonObject.getString("type");
                        String contact = jsonObject.getString("contact");
                        CardModel cardModel = new CardModel(nameSurname, dateOfSearch,blood_or_age, city,
                                hospital, type, contact);
                        cardModels.add(i,cardModel);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                searchesAdapter.notifyDataSetChanged();
                if (cardModels.isEmpty()) {
                    textView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("key","key");
                stringMap.put("userEmail", email);
                return stringMap;
            }
        };requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}