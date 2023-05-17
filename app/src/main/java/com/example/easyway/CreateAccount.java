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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import auxFunctions.FuncionesAux;


public class CreateAccount extends AppCompatActivity {

    private ImageButton volver;

    private Button createAccountBtn;
    private EditText emailBox, passwordBox, confPasswordBox;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    FuncionesAux aux = new FuncionesAux();
    private ImageView mailAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailBox = findViewById(R.id.email);
        passwordBox = findViewById(R.id.password);
        confPasswordBox = findViewById(R.id.confirmPassword);
        mailAlert = findViewById(R.id.incorrectMail);
        volver = findViewById(R.id.backButton);
        createAccountBtn = findViewById(R.id.createAccount);

        //Comprobamos que el formato del correo sea válido
        emailBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mailAlert.setVisibility(View.GONE); //Antes de escribir el aviso no se verá
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(aux.mailCorrect(emailBox.getText().toString())){
                    mailAlert.setVisibility(View.GONE); //Si el correo es válido el aviso no se mostrara
                }else{
                    mailAlert.setVisibility(View.VISIBLE); //Sino se mostrara un aviso
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(emailBox.getText().toString().isEmpty()){
                    mailAlert.setVisibility(View.GONE); //Si no hay nada escrito el aviso se va
                }else{
                    if(aux.mailCorrect(emailBox.getText().toString())){
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


        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailBox.getText().toString().trim();
                String password = passwordBox.getText().toString().trim();
                String confirmPass = confPasswordBox.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()){ //si algun camp esta buit, mostra un missatge informant de que s'han d'omplir tots els camps
                    Toast.makeText(CreateAccount.this, "Es necesario introducir todos los datos", Toast.LENGTH_SHORT).show();
                }else if(!password.equals(confirmPass)) { //si el camp de contrasenya no coincideix amb el de confirmar contrasenya, mostra un missatge indicant-ho
                    Toast.makeText(CreateAccount.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }else if(mailAlert.getVisibility() == View.VISIBLE){
                    Toast.makeText(CreateAccount.this, "La dirección de correo no es valida", Toast.LENGTH_SHORT).show();
                }else{ //si tot es correcte, crea un nou compte per iniciar sessió i crea l'usuari a la base de dades
                    createAccount(email, password);
                }
            }
        });
    }
    public void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            createUser(email);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void createUser(String email){
        Map<String, Object> user = new HashMap<>();
        user.put("admin", false);

        //Se actualiza el documento
        db.collection("Usuarios").document(email)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}