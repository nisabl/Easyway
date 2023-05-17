package com.example.easyway;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private Button createAccount, loginBtn;

    private EditText emailBox, passwordBox;
    private TextView forgetPass;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;

    private SignInButton signGoogleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Obtenemos instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //Opciones de configuración de inicio de sesión con Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //ID de proyecto Firebase
                .requestEmail() //Correo del usuario que inicia sesión
                .build(); //Devuelve el objeto gso configurado


        //Se crea un usuario de inicio de sesión con la configuración anterior
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Declaramos elementos gráficos
        emailBox = findViewById(R.id.email);
        passwordBox = findViewById(R.id.password);
        createAccount = findViewById(R.id.createAccountButton);
        forgetPass = findViewById(R.id.passRecovery);
        loginBtn = findViewById(R.id.loginButton);
        signGoogleButton = findViewById(R.id.googleButton);

        //Si clicamos sobre el botón de Google iniciara sesión con Google
        signGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mueve a la pantalla de crear cuenta
                Intent intent = new Intent(Login.this, CreateAccount.class);
                startActivity(intent);
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mueve a la pantalla de recuperar contraseña
                Intent intent = new Intent(Login.this, PasswordRecovery.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Recoge la informacion de los dos campos de texto
                String email = emailBox.getText().toString().trim();
                String password = passwordBox.getText().toString().trim();

                //Si alguno de los dos campos esta vacio lo indica con un mensaje
                if(email.isEmpty() || password.isEmpty()){ //si algun camp està buit, mostra el següent missatge
                    Toast.makeText(Login.this, "Es necesario introducir todos los datos", Toast.LENGTH_SHORT).show();
                }else{
                    signIn(email, password);
                }

            }
        });
    }

    //Cuando se inicia la aplicación comprueba si hay algun usuario logeado, si es asi pasa al menu principal y se salta el login
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
    }

    //Funcion que se encarga de hacer que los usuarios inicien sesión
    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Si es correcto, pasa al menu principal
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            //Si falla muestra un mensaje al usuario informando de la situación
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Inicia la actividad de inicio de sesión
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    //Realiza todas las acciones para iniciar sesión con Google
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }



    //Si ya se ha iniciado sesión con Google, este se salta la ventana de login
    private void updateUI(FirebaseUser user) {
        user = mAuth.getCurrentUser();
        if(user != null); {
            startActivity(new Intent(Login.this, MainActivity.class));
        }

    }

}