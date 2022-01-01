package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.icu.kandeneme.User.Blood;
import static com.icu.kandeneme.User.Cities;

public class SignIn extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    EditText emailText, passwordText;
    RequestQueue requestQueue;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar2);
        emailText = findViewById(R.id.signUpEmailText);
        passwordText = findViewById(R.id.passwordText);
        requestQueue= Volley.newRequestQueue(this);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(SignIn.this, UserInterface.class);
            startActivity(intent);
            finish();
        }
    }

    public void signInClicked(View view) {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString();
        if (email.isEmpty()) {
            emailText.setError("Lütfen e-posta giriniz.");
        } else if (password.isEmpty()) {
            passwordText.setError("Lütfen şifrenizi giriniz.");
        }else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("userEmail", email);
                        String url = "https://e-kan.herokuapp.com/sign_in";
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                SharedPreferences sharedPreferences = getSharedPreferences("kandeneme", MODE_PRIVATE);
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear().apply();
                                try {
                                    String name=response.getString("name");
                                    String number=response.getString("number");
                                    String city=response.getString("city");
                                    String blood=response.getString("blood");
                                    editor.putString("name", name);
                                    editor.putString("telephoneNumber", number);
                                    editor.putString("userEmail", email);
                                    editor.putString("city", city);
                                    editor.putString("blood", blood);
                                    editor.apply();
                                    String fullName = sharedPreferences.getString("name", "");
                                    if (!fullName.isEmpty()){
                                        String x = String.valueOf(Arrays.asList(Cities).indexOf(city));
                                        String y = String.valueOf(Arrays.asList(Blood).indexOf(blood));
                                        FirebaseMessaging.getInstance().subscribeToTopic(x);
                                        FirebaseMessaging.getInstance().subscribeToTopic(x+y);
                                        Intent intent = new Intent(SignIn.this,UserInterface.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(SignIn.this, "Bir sorunla karşılaşıldı. Daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(SignIn.this, "Bir sorunla karşılaşıldı. Daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(SignIn.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(SignIn.this, "Bir sorunla karşılaşıldı. Daha sonra tekrar deneyiniz.", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignIn.this, "Lütfen geçerli bir kullanıcı ile giriş yapınız.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void signUpClicked(View view) {
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
    }

    public void password(View view) {
        Intent intent = new Intent(SignIn.this, ForgotPassword.class);
        startActivity(intent);
    }
}