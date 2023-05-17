package com.example.easyway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.easyway.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment()); //Al crear esta activity, muestra el fragmento de menu principal

        mAuth = FirebaseAuth.getInstance();

        System.out.println("CORREO ELECTRONICO: " + mAuth.getCurrentUser().getEmail());

        //Dependiendo del item que seleccione el usuario, se movera a una fragmento o a otro
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch(item.getItemId()){

                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.shopCart:
                    replaceFragment(new ShoppingCartFragment());
                    break;
                case R.id.userConf:
                    replaceFragment((new UserConfFragment()));
                    break;
                case R.id.logout:
                    logout();
                    break;
            }

            return true;
        });
    }

    //Función para cerrar la sesión del usuario y volver a la pantalla de login
    public void logout(){
        mAuth.signOut();
        Toast.makeText(this, "Se cerró la sesión", Toast.LENGTH_SHORT).show();
        finish();

        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    //Funcion que sirve para moverse entre los fragmentos destacados en la barra de menú
    private void replaceFragment(Fragment fragment){ //Recibe un fragmento que representa el fragmento de destino
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}