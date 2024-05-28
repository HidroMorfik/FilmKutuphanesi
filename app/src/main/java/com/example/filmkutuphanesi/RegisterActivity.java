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

import com.example.filmkutuphanesi.database.MovieDatabase;
import com.example.filmkutuphanesi.database.MovieDatabaseSingleton;
import com.example.filmkutuphanesi.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    MovieDatabase MovieDB;
    private Button buttonSave;
    private TextView buttonLogin;
    User registeredUser;
    private EditText editTextUsername, editTextPassword, editTextCPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        initComponents();
        MovieDB =  MovieDatabaseSingleton.getInstance(RegisterActivity.this);


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Kontrolleri yap
                if (!editTextPassword.getText().toString().equals(editTextCPassword.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Şifreler uyuşmuyor!", Toast.LENGTH_SHORT).show();
                } else if (editTextUsername.getText().toString().length() < 5 || editTextPassword.getText().toString().length() < 5) {
                    Toast.makeText(RegisterActivity.this, "Kullanıcı adı ve şifre en az 5 karakter olmalıdır.", Toast.LENGTH_SHORT).show();
                } else {
                    // Tüm kontroller geçildiyse kullanıcıyı kaydet
                    User u1 = new User(editTextUsername.getText().toString(), editTextPassword.getText().toString());
                    checkUserInBackground(u1);
                }

            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);            }
        });


    }

    @SuppressLint("WrongViewCast")
    private void initComponents() {
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        editTextCPassword = findViewById(R.id.cpassword);
        buttonSave = findViewById(R.id.saveButton);
        buttonLogin =  findViewById(R.id.loginButton);
    }

    public void checkUserInBackground(User user){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler (Looper.getMainLooper());
        executorService. execute(new Runnable() {
            @Override
            public void run() {

                registeredUser = MovieDB.getUserDAO().getLogin(user.getUsername(), user.getPassword());
                System.out.println(registeredUser);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (registeredUser != null){
                            Toast.makeText(RegisterActivity.this,"Bu kullanıcı daha önce üye olmuş!",Toast.LENGTH_SHORT).show();
                        }else{
                            addUserInBackground(user);

                        }
                    }
                });
            }
        });
    }

    public void addUserInBackground(User user){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler (Looper.getMainLooper());
        executorService. execute(new Runnable() {
            @Override
            public void run() {

                MovieDB.getUserDAO().insertUser(user);



                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this,"Tebrikler! Üye oldunuz.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}