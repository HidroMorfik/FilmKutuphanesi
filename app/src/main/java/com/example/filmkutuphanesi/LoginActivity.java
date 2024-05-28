package com.example.filmkutuphanesi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.filmkutuphanesi.database.MovieDatabase;
import com.example.filmkutuphanesi.database.MovieDatabaseSingleton;
import com.example.filmkutuphanesi.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    MovieDatabase MovieDB;
    List<User> users;
    User userID;
    private Button buttonLogin;
    private TextView buttonSave;
    private EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initComponents();

        MovieDB =  MovieDatabaseSingleton.getInstance(LoginActivity.this);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserLogin(editTextUsername.getText().toString(), editTextPassword.getText().toString());
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    @SuppressLint("WrongViewCast")
    private void initComponents() {
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        buttonSave = findViewById(R.id.signupText);
        buttonLogin = findViewById(R.id.loginButton);
        editTextUsername.requestFocus();
    }

    
    public void getUserLogin(String username, String password){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler (Looper.getMainLooper());
        executorService. execute(new Runnable() {
            @Override
            public void run() {

                userID = MovieDB.getUserDAO().getLogin(username, password);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (userID == null){
                            Toast.makeText(LoginActivity.this,"Hatalı Kullanıcı Adı veya Şifre!",Toast.LENGTH_SHORT).show();
                        }else{

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userID", userID.getId());
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}