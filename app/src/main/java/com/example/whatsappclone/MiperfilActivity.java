package com.example.whatsappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MiperfilActivity extends AppCompatActivity {


    private EditText nombre, ciudad, edad, estado, genero;
    private Button botonmiperfil;
    private CircleImageView imagenmiperfil;
    private Toolbar toolbar;
    private  String CurrentuserID;
    private FirebaseAuth auth;
    private DatabaseReference RootRef;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miperfil);

        Componentes();

        dialog = new ProgressDialog(this);
        botonmiperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActualizarInformacion();
            }
        });

        RootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        CurrentuserID = auth.getCurrentUser().getUid();

        RootRef.child("Usuarios").child(CurrentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    String nom1 = snapshot.child("nombre").getValue().toString();
                    String ciu1 = snapshot.child("ciudad").getValue().toString();
                    String eda1 = snapshot.child("edad").getValue().toString();
                    String gen1 = snapshot.child("genero").getValue().toString();
                    String est1 = snapshot.child("estado").getValue().toString();

                    nombre.setText(nom1);
                    ciudad.setText(ciu1);
                    edad.setText(eda1);
                    genero.setText(gen1);
                    estado.setText(est1);
                }
                }@Override public void onCancelled(@NonNull DatabaseError error) { } });

    }


    private void ActualizarInformacion() {
        String nom = nombre.getText().toString();
        String ciu = ciudad.getText().toString();
        String eda = edad.getText().toString();
        String gen = genero.getText().toString();
        String est = estado.getText().toString();

        if(TextUtils.isEmpty(nom)){
            Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(ciu)){
            Toast.makeText(this, "Ingrese su ciudad", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(eda)){
            Toast.makeText(this, "Ingrese su edad", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(gen)){
            Toast.makeText(this, "Ingrese su genero", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(est)){
            Toast.makeText(this, "Ingrese su estado", Toast.LENGTH_LONG).show();
        }
        else{
            HashMap<String, String> profile = new HashMap<>();
            profile.put("uid",CurrentuserID);
            profile.put("nombre",nom);
            profile.put("ciudad",ciu);
            profile.put("edad", eda);
            profile.put("genero", gen);
            profile.put("estado",est);
            RootRef.child("Usuarios").child(CurrentuserID).setValue(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MiperfilActivity.this, "Guardado exitosamente.....", Toast.LENGTH_SHORT).show();
                            }else{
                                    String err = task.getException().getMessage().toString();
                                Toast.makeText(MiperfilActivity.this, "Error: "+ err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
    private void EnviarInicio() {
        Intent intent = new Intent(MiperfilActivity.this, InicioActivity.class);
        startActivity(intent);
    }


    private void Componentes(){
        nombre= findViewById(R.id.nombre_miperfil);
        ciudad= findViewById(R.id.ciudad_miperfil);
        edad= findViewById(R.id.edad_miperfil);
        estado= findViewById(R.id.estado_miperfil);
        genero= findViewById(R.id.genero_miperfil);

        botonmiperfil= findViewById(R.id.boton_miperfil);
        imagenmiperfil= findViewById(R.id.imagen_s);
        toolbar=findViewById(R.id.toolbar_Miperfil);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mi perfil");
    }

}