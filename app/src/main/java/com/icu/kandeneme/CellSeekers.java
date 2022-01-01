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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CellSeekers extends Fragment {
    private CellSeekersAdapter cellSeekersAdapter;
    private RecyclerView recyclerView3;
    private ArrayList<CardModel2> cardModels;
    private ArrayList<CardModel2> allCardModels;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout2;
    TextView textViewNoData;

    public static CellSeekers newInstance() {
        return new CellSeekers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cell_seekers, container, false);
        cardModels = new ArrayList<>();
        allCardModels = new ArrayList<>();
        swipeRefreshLayout2=rootView.findViewById(R.id.swipeRefreshLayout2);
        textViewNoData=rootView.findViewById(R.id.tv_no_data2);
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        recyclerView3 = rootView.findViewById(R.id.recycler_view3);
        herokuAppConnect();
        cellSeekersAdapter = new CellSeekersAdapter(getActivity(), cardModels, allCardModels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity().getBaseContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView3.setLayoutManager(linearLayoutManager);
        recyclerView3.setHasFixedSize(false);
        recyclerView3.setAdapter(cellSeekersAdapter);
        setHasOptionsMenu(true);
        swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (cardModels.isEmpty()) {
                    textViewNoData.setVisibility(View.VISIBLE);
                    swipeRefreshLayout2.setRefreshing(false);
                } else {
                    cardModels.clear();
                    allCardModels.clear();
                    herokuAppConnect();
                    cellSeekersAdapter.notifyDataSetChanged();
                    swipeRefreshLayout2.setRefreshing(false);
                    textViewNoData.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout2.setRefreshing(false);
                }
            }
        });
        return rootView;
    }
    private void herokuAppConnect(){
        String url="https://e-kan.herokuapp.com/cell_seekers";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        String nameSurname = jsonObject.getString("nameSurname");
                        String dateOfSearch = jsonObject.getString("dateOfSearch");
                        String age = jsonObject.getString("age");
                        String city = jsonObject.getString("city");
                        String hospital = jsonObject.getString("hospital");
                        String diseases = jsonObject.getString("type");
                        String contact = jsonObject.getString("contact");
                        CardModel2 cardModel2 = new CardModel2(nameSurname, dateOfSearch, age, city,
                                hospital, diseases, contact);
                        cardModels.add(i,cardModel2);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(),"tamam"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                allCardModels.addAll(cardModels);
                cellSeekersAdapter.notifyDataSetChanged();
                if (cardModels.isEmpty()) {
                    textViewNoData.setVisibility(View.VISIBLE);
                    swipeRefreshLayout2.setRefreshing(false);
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
        };
        requestQueue.add(jsonArrayRequest);
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
                cellSeekersAdapter.getFilter().filter(newText);
                System.out.println(newText);
                return false;
            }
        });
    }
}
