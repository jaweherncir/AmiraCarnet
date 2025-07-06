package com.example.amiracarnet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Inscription extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);

        dbHelper = new UserDatabaseHelper(this);

        registerButton.setOnClickListener(v -> registerUser());

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(Inscription.this, Login.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Vérifier si l'email existe déjà
        Cursor cursor = db.query(
                UserDatabaseHelper.TABLE_USERS,
                null,
                UserDatabaseHelper.COL_EMAIL + " = ?",
                new String[]{email},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Email déjà utilisé", Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return;
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COL_EMAIL, email);
        values.put(UserDatabaseHelper.COL_PASSWORD, password); // en prod, hasher !

        long newRowId = db.insert(UserDatabaseHelper.TABLE_USERS, null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Inscription.this, UploadFile.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Échec de l'inscription", Toast.LENGTH_SHORT).show();
        }
    }
}
