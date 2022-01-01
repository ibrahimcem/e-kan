package com.icu.kandeneme;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserInterface extends AppCompatActivity {
    private CellSeekers cellSeekers;
    private BloodSeekers bloodSeekers;
    private Searches searches;
    private Account account;
    private StartFragments startFragments;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    FrameLayout frameLayout;
    BottomAppBar bottomAppBar;
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ok)));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
        frameLayout = findViewById(R.id.frame_layout);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        cellSeekers = new CellSeekers();
        bloodSeekers = new BloodSeekers();
        searches = new Searches();
        account = new Account();
        startFragments = new StartFragments();
        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.tamam);
        fab.isSelected();
        setFragment(startFragments);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        setFragment(cellSeekers);
                        getSupportActionBar().setTitle("Kök Hücre Arayanlar");
                        return true;
                    case R.id.account:
                        setFragment(account);
                        getSupportActionBar().setTitle("Hesabım");
                        return true;
                    case R.id.searchBlood:
                        setFragment(bloodSeekers);
                        getSupportActionBar().setTitle("Kan Arayanlar");
                        return true;
                    case R.id.mySearchBlood:
                        setFragment(searches);
                        getSupportActionBar().setTitle("Aramalarım");
                        return true;
                    default:
                        return false;


                }
            }

        });



    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("ResourceAsColor")
    public void fabOnclicked(View view) {
        getSupportActionBar().setTitle("E-Kan");
        bottomNavigationView.setSelectedItemId(R.id.tamam);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, StartFragments.newInstance());
        fragmentTransaction.commit();
    }
}