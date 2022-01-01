package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.icu.kandeneme.User.Blood;
import static com.icu.kandeneme.User.BloodType;
import static com.icu.kandeneme.User.Cities;

public class SignUp extends AppCompatActivity {
    EditText nameText, telephoneNumberText, emailText, passwordText;
    AutoCompleteTextView cityText, bloodText;
    private FirebaseAuth firebaseAuth;
    RequestQueue requestQueue;
    ProgressBar progressBar;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        nameText = findViewById(R.id.signUpNameText);
        progressBar = findViewById(R.id.progressBar1);
        telephoneNumberText = findViewById(R.id.signUpNumberText);
        requestQueue= Volley.newRequestQueue(this);
        emailText = findViewById(R.id.signUpEmailText);
        passwordText = findViewById(R.id.signUpPasswordText);
        cityText = findViewById(R.id.signUpCityText);
        bloodText = findViewById(R.id.signUpBloodText);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drop_down, Cities);
        cityText.setThreshold(0);
        cityText.setAdapter(adapter);
        cityText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboaard();
                cityText.requestFocus();
                cityText.setShowSoftInputOnFocus(false);
                cityText.showDropDown();
                return false;
            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.drop_down, BloodType);
        bloodText.setThreshold(0);
        bloodText.setAdapter(adapter1);
        bloodText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboaard();
                bloodText.requestFocus();
                bloodText.setShowSoftInputOnFocus(false);
                bloodText.showDropDown();
                return false;
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void signUpClicked(View view) throws JSONException {
        String fullName = nameText.getText().toString().trim();
        String telephoneNumber = telephoneNumberText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString();
        String city = cityText.getText().toString().trim();
        String blood = bloodText.getText().toString().trim();
        JSONObject userInformation = new JSONObject();
        userInformation.put("fullName", fullName);
        userInformation.put("telephoneNumber", telephoneNumber);
        userInformation.put("userEmail", email);
        userInformation.put("city", city);
        userInformation.put("blood", blood);
        SharedPreferences sharedPreferences = getSharedPreferences("kandeneme", MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
        editor.putString("name", fullName);
        editor.putString("telephoneNumber", telephoneNumber);
        editor.putString("userEmail", email);
        editor.putString("city", city);
        editor.putString("blood", blood);
        editor.apply();
        String name = sharedPreferences.getString("name", "");
            if (fullName.isEmpty()) {
                nameText.setError("Boş bırakılamaz.");
            } else if (city.isEmpty()) {
                cityText.setError("Lütfen seçiniz.");
            } else if (blood.isEmpty()) {
                bloodText.setError("Lütfen seçiniz.");
            } else if (telephoneNumber.isEmpty() | telephoneNumber.length()!=11) {
                telephoneNumberText.setError("Eksik veya boş bırakılamaz.");
            } else if (email.isEmpty()) {
                emailText.setError("Boş bırakılamaz.");
            } else if (password.length() < 4) {
                passwordText.setError("4 karakterden az olamaz.");
            } else {
                if (!name.isEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        new UserProfileChangeRequest.Builder().setDisplayName(fullName).setPhotoUri(null).build();
                        Intent intent = new Intent(SignUp.this, UserInterface.class);
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("fullName", name);
                                        jsonObject.put("telephoneNumber", telephoneNumber);
                                        jsonObject.put("city", city);
                                        jsonObject.put("blood", blood);
                                        jsonObject.put("userEmail", email);
                                        String url = "https://e-kan.herokuapp.com/sign_up";
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String oncomplete="oncomplete";
                                                    if (response.getString("oncomplete").equals(oncomplete)) {
                                                        String x = String.valueOf(Arrays.asList(Cities).indexOf(city));
                                                        String y = String.valueOf(Arrays.asList(Blood).indexOf(blood));
                                                        FirebaseMessaging.getInstance().subscribeToTopic(x);
                                                        FirebaseMessaging.getInstance().subscribeToTopic(x+y);
                                                        startActivity(intent);
                                                        finish();
                                                        Toast.makeText(getApplicationContext(), "Kullanıcı doğrulama e-postası gönderildi.", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        Toast.makeText(SignUp.this, "Bir sorunla karşılaşıldı. Daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(SignUp.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(SignUp.this, "Bir sorunla karşılaşıldı. Daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                                        user.delete();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUp.this, "Bir sorunla karşılaşıldı daha sonra tekrar deneyin.", Toast.LENGTH_SHORT).show();
                                    user.delete();
                                }
                            });
                        }

                    }).addOnFailureListener(e -> Toast.makeText(SignUp.this, "Bir sorunla karşılaşıldı daha sonra tekrar deneyin.", Toast.LENGTH_SHORT).show());


                }else {
                    Toast.makeText(SignUp.this,"Bir sorunla karşılaşıldı daha sonra tekrar deneyiniz.",Toast.LENGTH_SHORT).show();
                }
                }

    }

    public void contidions(View view) {
        String url = "https://www.kizilay.org.tr/sss?id=6#:~:text=Kimler%20kan%20ba%C4%9F%C4%B1%C5%9F%C4%B1nda%20bulunabilir%3F,61%20ya%C5%9F%C4%B1ndan%20g%C3%BCn%20almam%C4%B1%C5%9F%20olmal%C4%B1";
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void backClicked(View view) {
        onBackPressed();
        finish();
    }
    private void closeKeyboaard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
