package com.alfredo.dividirred;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.alfredo.dividirred.Utility.FirebaseHelper;

public class MainActivity extends AppCompatActivity {

    private TextView textView_name, textView_password;
    private CardView cardView_login;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar

        textView_name = findViewById(R.id.editTextTextPersonName_name);
        textView_password = findViewById(R.id.editTextTextPassword_password);
        cardView_login = findViewById(R.id.cardView_login_login);

        cardView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "Cargando",
                        "Porfa espera...", true);
                dialog.show();
                firebaseHelper.IsUser(MainActivity.this,textView_name.getText().toString(),textView_password.getText().toString(),dialog);

            }
        });




    }
}