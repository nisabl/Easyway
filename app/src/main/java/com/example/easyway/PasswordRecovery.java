package com.example.easyway;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import auxFunctions.FuncionesAux;

public class PasswordRecovery extends AppCompatActivity {

    private ImageButton volver;
    private Button sendMail;
    private EditText mail;

    private ImageView mailAlert;

    FuncionesAux aux = new FuncionesAux();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_recovery);

        volver = findViewById(R.id.backButton);
        sendMail = findViewById(R.id.passwordRecover);
        mail = findViewById(R.id.email);
        mailAlert = findViewById(R.id.incorrectMail);

        //Comprobamos que el formato del correo sea válido
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mailAlert.setVisibility(View.GONE); //Antes de escribir el aviso no se verá
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(aux.mailCorrect(mail.getText().toString())){
                    mailAlert.setVisibility(View.GONE); //Si el correo es válido el aviso no se mostrara
                }else{
                    mailAlert.setVisibility(View.VISIBLE); //Sino se mostrara un aviso
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mail.getText().toString().isEmpty()){
                    mailAlert.setVisibility(View.GONE); //Si no hay nada escrito el aviso se va
                }else{
                    if(aux.mailCorrect(mail.getText().toString())){
                        mailAlert.setVisibility(View.GONE); //Si el correo es válido el aviso no se mostrara
                    }else{
                        mailAlert.setVisibility(View.VISIBLE);//Sino se mostrara un aviso
                    }
                }

            }
        });


        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = mail.getText().toString().trim();
                if(emailAddress.isEmpty()) {
                    Toast.makeText(PasswordRecovery.this, "Introduce una direción de correo", Toast.LENGTH_SHORT).show();
                }else if(mailAlert.getVisibility() != View.VISIBLE){
                    Toast.makeText(PasswordRecovery.this, "La dirección de correo no es valida", Toast.LENGTH_SHORT).show();
                }else{
                    sendMail(emailAddress);
                    finish();
                }
            }
        });
    }

    //Envia un correo a la direccion de correo introducida con las instrucciones para cambiar la contraseña
    public void sendMail(String emailAddress){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(PasswordRecovery.this, "Revise su correo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean mailCorrect(String email){

        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Matcher mather = pattern.matcher(email);

        if (mather.find() == true) {
            return true;
        } else {
            return false;
        }
    }
}