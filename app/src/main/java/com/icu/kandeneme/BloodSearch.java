package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.icu.kandeneme.User.Blood;
import static com.icu.kandeneme.User.Cities;
import static com.icu.kandeneme.User.Type;

public class BloodSearch extends Fragment {
    AutoCompleteTextView patientCity, patientBlood, patientType;
    Button bloodSearch;
    EditText patientName, patientHospital, patientContactNumber;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    RequestQueue requestQueue;

    public static BloodSearch newInstance() {
        return new BloodSearch();
    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.blood_search, container, false);
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        bloodSearch = rootView.findViewById(R.id.bloodSearch);
        patientName = rootView.findViewById(R.id.bloodPatientName);
        patientHospital = rootView.findViewById(R.id.bloodPatientHospital);
        patientContactNumber = rootView.findViewById(R.id.bloodPatientContactNumber);
        patientCity = rootView.findViewById(R.id.bloodPatientCity);
        patientBlood = rootView.findViewById(R.id.bloodPatientBlood);
        patientType = rootView.findViewById(R.id.bloodPatientType);
        List<String> cities= Arrays.asList(Cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.drop_down, Cities);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.drop_down, Blood);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.drop_down, Type);
        patientCity.setThreshold(0);
        patientCity.setAdapter(adapter);
        patientCity.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboaard();
                patientCity.requestFocus();
                patientCity.setShowSoftInputOnFocus(false);
                patientCity.showDropDown();
                return false;
            }
        });
        patientBlood.setThreshold(0);
        patientBlood.setAdapter(adapter1);
        patientBlood.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboaard();
                patientBlood.requestFocus();
                patientBlood.setShowSoftInputOnFocus(false);
                patientBlood.showDropDown();
                return false;
            }
        });
        patientType.setThreshold(0);
        patientType.setAdapter(adapter2);
        patientType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboaard();
                patientType.requestFocus();
                patientType.setShowSoftInputOnFocus(false);
                patientType.showDropDown();
                return false;
            }
        });
        bloodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = patientCity.getText().toString().trim();
                String blood = patientBlood.getText().toString().trim();
                String type = patientType.getText().toString().trim();
                String name = patientName.getText().toString().trim();
                String hospital = patientHospital.getText().toString().trim();
                String number = patientContactNumber.getText().toString().trim();
                if (name.isEmpty()) {
                   patientName.setError("Boş bırakılamaz.");
                } else if (number.isEmpty() | number.length()!=11) {
                    patientContactNumber.setError("Eksik veya boş bırakılamaz.");
                } else if (hospital.isEmpty()) {
                    patientHospital.setError("Boş bırakılamaz.");
                } else if (city.isEmpty()) {
                    patientCity.setError("Seçim yapınız.");
                } else if (blood.isEmpty()) {
                    patientBlood.setError("Seçim yapınız.");
                } else if (type.isEmpty()) {
                    patientType.setError("Seçim yapınız.");
                } else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity(), R.style.ShowAlertDialogTheme);
                    ad.setTitle("Uyarı");
                    ad.setMessage("Lütfen bilgileri doğru girdiğinizden emin olun.");
                    ad.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseUser = firebaseAuth.getCurrentUser();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            Date date = new Date();
                            String email=null;
                            if (firebaseUser != null) {
                                email = Objects.requireNonNull(firebaseUser.getEmail());
                            }
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("patient", name);
                                jsonObject.put("blood", blood);
                                jsonObject.put("contact", number);
                                jsonObject.put("hospital", hospital);
                                jsonObject.put("city", city);
                                jsonObject.put("type", type);
                                jsonObject.put("userEmail", email);
                                jsonObject.put("date", df.format(date));
                                String url = "https://e-kan.herokuapp.com/blood_search";
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String oncomplete="oncomplete";
                                            if (response.getString("oncomplete").equals(oncomplete)) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Kayıt Başarılı, bildirim Gönderildi.", Toast.LENGTH_SHORT).show();
                                                String x = String.valueOf(Arrays.asList(Cities).indexOf(city));
                                                String y = String.valueOf(Arrays.asList(Blood).indexOf(blood));
                                                new SendNotification(requestQueue,blood,type,hospital,x+y);
                                                BloodSeekers bloodSeekers = new BloodSeekers();
                                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.frame_layout, bloodSeekers);
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
                    });
                    ad.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
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
    private void closeKeyboaard(){
        View view = getActivity().getCurrentFocus();
        if (view!=null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}
