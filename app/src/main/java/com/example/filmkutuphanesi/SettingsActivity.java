package com.example.filmkutuphanesi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.filmkutuphanesi.database.MovieDatabase;
import com.example.filmkutuphanesi.database.MovieDatabaseSingleton;
import com.example.filmkutuphanesi.model.User;
import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    MovieDatabase MovieDB;
    private View toolbar,headerView;
    private TextView username;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CardView usersettings;
    private int userID;
    private Switch tema_switch;
    boolean nightMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        initComponents();
        Intent intent2 = getIntent();
        userID = intent2.getIntExtra("userID", 0);
        initComponents();
        setSupportActionBar((Toolbar) toolbar);

        MovieDB =  MovieDatabaseSingleton.getInstance(SettingsActivity.this);
        getUserName();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(SettingsActivity.this,drawerLayout,(Toolbar) toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences=getSharedPreferences("MODE", MODE_PRIVATE);
        nightMODE= sharedPreferences.getBoolean("night",false);

        if(nightMODE){
            tema_switch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        tema_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nightMODE){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor=sharedPreferences.edit();
                    editor.putBoolean("night",false);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor=sharedPreferences.edit();
                    editor.putBoolean("night",true);
                }
                editor.apply();
            }
        });


        usersettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this, UsersettingsActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
    }

    @SuppressLint("WrongViewCast")
    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        headerView = navigationView.getHeaderView(0);
        username = headerView.findViewById(R.id.username);
        usersettings = findViewById(R.id.usersettings);
        tema_switch=findViewById(R.id.tema_switch);
    }

    public void getUserName(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler (Looper.getMainLooper());
        executorService. execute(new Runnable() {
            @Override
            public void run() {

                User user  = MovieDB.getUserDAO().getPerson(userID);
                username.setText(user.getUsername());


                handler.post(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        username.setText(user.getUsername());
                    }
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        }else if (menuItem.getItemId() == R.id.nav_favourites){
            Intent intent = new Intent(SettingsActivity.this, FavouritesActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        }else if (menuItem.getItemId() == R.id.nav_logout){
            // Çıkış yapılacağı zaman kullanıcıya bir uyarı ver
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Çıkış Yap");
            builder.setMessage("Çıkış yapmak istediğinize emin misiniz?");
            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Evet'e tıklanırsa çıkış işlemini gerçekleştir
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Hayır'a tıklanırsa dialog'u kapat
                    dialog.dismiss();
                }
            });
            builder.show();
        }else if (menuItem.getItemId() == R.id.nav_settings){
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }
        return true;
    }
}