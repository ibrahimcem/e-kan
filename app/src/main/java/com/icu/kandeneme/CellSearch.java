package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.icu.kandeneme.User.Cities;
import static com.icu.kandeneme.User.Diseases;

public class CellSearch extends Fragment {
    EditText patientName, patientAge, contactNumber, hospitalName;
    AutoCompleteTextView patientCity, patientDiseases;
    Button cellSearch;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    RequestQueue requestQueue;

    public static CellSearch newInstance() {
        return new CellSearch();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cell_search, container, false);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        patientName = rootView.findViewById(R.id.patientName);
        contactNumber = rootView.findViewById(R.id.patientContact);
        patientAge = rootView.findViewById(R.id.patientAge);
        hospitalName = rootView.findViewById(R.id.hospitalName);
        patientCity = rootView.findViewById(R.id.patientCity);
        patientDiseases = rootView.findViewById(R.id.patientDiseases);
        cellSearch = rootView.findViewById(R.id.cellSearch);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.drop_down, Cities);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.drop_down, Diseases);
        patientCity.setThreshold(0);
        patientCity.setAdapter(adapter);
        patientCity.setOnTouchListener((v, event) -> {
            closeKeyboaard();
            patientCity.requestFocus();
            patientCity.setShowSoftInputOnFocus(false);
            patientCity.showDropDown();
            return false;
        });

        patientDiseases.setThreshold(0);
        patientDiseases.setAdapter(adapter2);
        patientDiseases.setOnTouchListener((v, event) -> {
            closeKeyboaard();
            patientDiseases.requestFocus();
            patientDiseases.setShowSoftInputOnFocus(false);
            patientDiseases.showDropDown();
            return false;
        });
        cellSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = patientName.getText().toString().trim();
                String age = patientAge.getText().toString().trim();
                String number = contactNumber.getText().toString().trim();
                String hospital = hospitalName.getText().toString().trim();
                String city = patientCity.getText().toString().trim();
                String diseases = patientDiseases.getText().toString().trim();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = new Date();
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                if (name.isEmpty()) {
                    patientName.setError("Boş bırakılamaz.");
                } else if (age.isEmpty()) {
                    patientAge.setError("Boş bırakılamaz.");
                } else if (number.isEmpty() | number.length()!=11){
                    contactNumber.setError("Boş veya eksik bırakılamaz.");
                } else if (hospital.isEmpty()) {
                    hospitalName.setError("Boş bırakılamaz.");
                } else if (city.isEmpty()) {
                    patientCity.setError("Seçim yapınız.");
                } else if (diseases.isEmpty()) {
                    patientDiseases.setError("Seçim yapınız.");
                } else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity(), R.style.ShowAlertDialogTheme);
                    ad.setTitle("Uyarı");
                    ad.setMessage("Lütfen bilgileri doğru girdiğinizden emin olun.");
                    ad.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String email = null;
                            if (firebaseUser != null) {
                                email = Objects.requireNonNull(firebaseUser.getEmail());
                            }
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("name", name);
                                jsonObject.put("age", age);
                                jsonObject.put("number", number);
                                jsonObject.put("hospital", hospital);
                                jsonObject.put("city", city);
                                jsonObject.put("diseases", diseases);
                                jsonObject.put("userEmail", email);
                                jsonObject.put("date", df.format(date));
                                String url = "https://e-kan.herokuapp.com/cell_search";
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String oncomplete="oncomplete";
                                            if (response.getString("oncomplete").equals(oncomplete)) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Kayıt Başarılı, bildirim Gönderildi.", Toast.LENGTH_SHORT).show();
                                                String x = String.valueOf(Arrays.asList(Cities).indexOf(city));
                                                String y = "Kök Hücre İhtiyacı";
                                                new SendNotification(requestQueue, diseases, y, hospital, x);
                                                CellSeekers cellSeekers = new CellSeekers();
                                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.frame_layout, cellSeekers);
                                                fragmentTransaction.commit();
                                            } else {
                                                Toast.makeText(getActivity().getApplicationContext(), "tamamBir sorunla karşılaşıldı. Daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getActivity().getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(getActivity().getApplicationContext(), "Bir sorunla karşılaşıldı. Daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    ad.show();
                }
            }
        });
        return rootView;

    }

    private void closeKeyboaard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
