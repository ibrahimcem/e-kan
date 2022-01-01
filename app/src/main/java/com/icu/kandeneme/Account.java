package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.icu.kandeneme.User.Blood;
import static com.icu.kandeneme.User.Cities;

public class Account extends Fragment {
    TextInputEditText myAccountEmail, myAccountNumber, myAccountCity, myAccountName, myAccountBlood;
    Button updatePassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Context context;

    public static Account newInstance() {
        return new Account();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("kandeneme", Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        context=getActivity().getApplicationContext();
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        myAccountName = rootView.findViewById(R.id.myAccountName);
        String name = sharedPreferences.getString("name", "Kayıt yok.");
        myAccountName.setText(name);
        myAccountBlood = rootView.findViewById(R.id.myAccountBlood);
        String blood = sharedPreferences.getString("blood", "Kayıt yok.");
        myAccountBlood.setText(blood);
        myAccountEmail = rootView.findViewById(R.id.myAccountEmail);
        myAccountEmail.setEnabled(true);
        myAccountEmail.setTextIsSelectable(true);
        myAccountEmail.setFocusable(false);
        myAccountEmail.setFocusableInTouchMode(false);
        String registeredEmail = sharedPreferences.getString("userEmail", "Kayıt yok.");
        myAccountEmail.setText(registeredEmail);
        myAccountNumber = rootView.findViewById(R.id.myAccountNumber);
        myAccountNumber.setEnabled(true);
        myAccountNumber.setTextIsSelectable(true);
        myAccountNumber.setFocusable(false);
        myAccountNumber.setFocusableInTouchMode(false);
        String registeredNumber = sharedPreferences.getString("telephoneNumber", "Kayıt yok.");
        myAccountNumber.setText(registeredNumber);
        myAccountCity = rootView.findViewById(R.id.myAccountCity);
        myAccountCity.setEnabled(true);
        myAccountCity.setTextIsSelectable(true);
        myAccountCity.setFocusable(false);
        myAccountCity.setFocusableInTouchMode(false);
        String registeredCity = sharedPreferences.getString("city", "Kayıt yok");
        myAccountCity.setText(registeredCity);
        myAccountEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.update_alert_view, null);
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity(), R.style.ShowAlertDialogTheme);
                TextInputEditText updateEmail = view.findViewById(R.id.updateEdit);
                TextInputLayout updateInput = view.findViewById(R.id.updateInput);
                updateInput.setHint("E-posta");
                updateEmail.setText(myAccountEmail.getText());
                ad.setTitle("Güncelle");
                ad.setView(view);
                ad.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = Objects.requireNonNull(updateEmail.getText()).toString().trim();
                        String url ="https://e-kan.herokuapp.com/email_update";
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("userEmail",email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        InformationUpdate informationUpdate=new InformationUpdate(requestQueue,context,url,jsonObject,"userEmail",email);
                        myAccountEmail.setText(email);
                        firebaseUser.updateEmail(email);
                    }
                });
                ad.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ad.create().show();

            }
        });

        myAccountNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad2 = new AlertDialog.Builder(getActivity(), R.style.ShowAlertDialogTheme);
                View view2 = getLayoutInflater().inflate(R.layout.update_alert_view, null);
                TextInputEditText updateNumber = view2.findViewById(R.id.updateEdit);
                TextInputLayout updateInput2 = view2.findViewById(R.id.updateInput);
                updateInput2.setHint("Telefon Numarası");
                updateNumber.setText(myAccountNumber.getText());
                ad2.setTitle("Güncelle");
                ad2.setView(view2);
                ad2.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String number = (Objects.requireNonNull(updateNumber.getText()).toString().trim());
                        String url ="https://e-kan.herokuapp.com/telephone_number_update";
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("number",number);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        InformationUpdate informationUpdate=new InformationUpdate(requestQueue,context,url,jsonObject,"telephoneNumber",number);
                        myAccountNumber.setText(number);
                    }
                });
                ad2.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ad2.create().show();
            }
        });

        myAccountCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad3 = new AlertDialog.Builder(getActivity(), R.style.ShowAlertDialogTheme);
                View view3 = getLayoutInflater().inflate(R.layout.update_alert_view, null);
                TextInputEditText updateCity = view3.findViewById(R.id.updateEdit);
                TextInputLayout updateInput3 = view3.findViewById(R.id.updateInput);
                updateInput3.setHint("Yaşadığın Şehir");
                updateCity.setText(myAccountCity.getText());
                ad3.setTitle("Güncelle");
                ad3.setView(view3);
                ad3.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String x = String.valueOf(Arrays.asList(Cities).indexOf(registeredCity));
                        String y = String.valueOf(Arrays.asList(Blood).indexOf(blood));
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(x+y);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(x);
                        String city = (Objects.requireNonNull(updateCity.getText()).toString().trim());
                        String z = String.valueOf(Arrays.asList(Cities).indexOf(city));
                        FirebaseMessaging.getInstance().subscribeToTopic(z+y);
                        FirebaseMessaging.getInstance().subscribeToTopic(z);
                        String url ="https://e-kan.herokuapp.com/city_update";
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("city",city);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        InformationUpdate informationUpdate=new InformationUpdate(requestQueue,context,url,jsonObject,"city",city);
                        myAccountCity.setText(city);
                    }
                });
                ad3.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ad3.create().show();
            }
        });
        updatePassword = rootView.findViewById(R.id.updatePassword);
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Sifre değiştirme e-postası gönderildi.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });
        return rootView;
    }
}
