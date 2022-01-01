package com.icu.kandeneme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BloodSeekers extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private BloodSeekersAdapter bloodSeekersAdapter;
    private ArrayList<CardModel> cardModels;
    private ArrayList<CardModel> allCardModels2;
    TextView textViewNoData;
    RequestQueue requestQueue;

    public static BloodSeekers newInstance() {
        return new BloodSeekers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.blood_seekers, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        cardModels = new ArrayList<>();
        allCardModels2 = new ArrayList<>();
        textViewNoData=rootView.findViewById(R.id.tv_no_data3);
        firebaseFirestore = FirebaseFirestore.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity().getBaseContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        herokuAppConnect();
        bloodSeekersAdapter = new BloodSeekersAdapter(getActivity(), cardModels, allCardModels2);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(bloodSeekersAdapter);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        setHasOptionsMenu(true);
        swipeRefreshLayout.refreshDrawableState();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (cardModels.isEmpty()) {
                    textViewNoData.setVisibility(View.VISIBLE);
                } else {
                    cardModels.clear();
                    allCardModels2.clear();
                    herokuAppConnect();
                    bloodSeekersAdapter.notifyDataSetChanged();
                    textViewNoData.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        return rootView;
    }

    private void herokuAppConnect(){
        String url="https://e-kan.herokuapp.com/blood_seekers";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        String nameSurname = jsonObject.getString("nameSurname");
                        String dateOfSearch = jsonObject.getString("dateOfSearch");
                        String blood = jsonObject.getString("blood");
                        String city = jsonObject.getString("city");
                        String hospital = jsonObject.getString("hospital");
                        String type = jsonObject.getString("type");
                        String contact = jsonObject.getString("contact");
                        CardModel cardModel = new CardModel(nameSurname, dateOfSearch,blood, city,
                                hospital, type, contact);
                        cardModels.add(i,cardModel);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                allCardModels2.addAll(cardModels);
                bloodSeekersAdapter.notifyDataSetChanged();
                if (cardModels.isEmpty()) {
                    textViewNoData.setVisibility(View.VISIBLE);
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
                return stringMap;
            }
        };requestQueue.add(jsonArrayRequest);
    }

    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchFilter = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchFilter.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Şehir arayınız...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bloodSeekersAdapter.getFilter().filter(newText);
                System.out.println(newText);
                return false;
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

    }
}

