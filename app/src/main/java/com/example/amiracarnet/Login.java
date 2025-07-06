package com.example.amiracarnet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private UserDatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation des vues
        emailEditText = findViewById(R.id.username); // ID pour le champ email
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        dbHelper = new UserDatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        loginButton.setOnClickListener(v -> loginUser());

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Inscription.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                UserDatabaseHelper.TABLE_USERS,
                new String[]{UserDatabaseHelper.COL_ID, UserDatabaseHelper.COL_EMAIL},
                UserDatabaseHelper.COL_EMAIL + " = ? AND " + UserDatabaseHelper.COL_PASSWORD + " = ?",
                new String[]{email, password},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            // Connexion réussie
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COL_ID));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("id", userId);
            editor.putString("email", email);
            editor.apply();

            Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this, UploadFile.class));
            finish();
        } else {
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }
}
