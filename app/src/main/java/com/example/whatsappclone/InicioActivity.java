package com.example.whatsappclone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InicioActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager myviewpager;
    private TabLayout mytablayout;
    private AcesoTabsAdapter myacesoTabsAdapter;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        toolbar = (Toolbar) findViewById(R.id.app_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("whatsappClone");

        myviewpager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myacesoTabsAdapter = new AcesoTabsAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(myacesoTabsAdapter);


        mytablayout = (TabLayout) findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewpager);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        RootRef = FirebaseDatabase.getInstance().getReference().child("Grupos");
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        if (curUser == null){
            EnviarALogin();
        }else{
            VerificarUsuario();
        }
    }

    private void VerificarUsuario() {
        final String currentUserID = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(currentUserID)){
                    CompletarDatosUsuario();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    private void CompletarDatosUsuario() {
        Intent intent = new Intent(InicioActivity.this, SeptupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void EnviarALogin() {
        Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menus_opciones, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.creargrupo){
            CrearNuevoGrupo();
        }
        if (item.getItemId() == R.id.miperfil){
            Intent intent = new Intent(InicioActivity.this, MiperfilActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.cerrar){
            mAuth.signOut();
            EnviarALogin();
        }

        return true;
    }

    private void CrearNuevoGrupo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this, R.style.AlertDialog);
        builder.setTitle("Nombre del grupo: ");

        final EditText nombregrupo = new EditText(InicioActivity.this);
        builder.setView(nombregrupo);

        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String nombreg = nombregrupo.getText().toString();

                if(TextUtils.isEmpty(nombreg)){
                    Toast.makeText(InicioActivity.this, "Ingrese el nombre del grupo", Toast.LENGTH_SHORT).show();
                }else{
                    CrearGrupoFirebase(nombreg);
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void CrearGrupoFirebase(String nombreg){

         RootRef.child(nombreg).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(InicioActivity.this, "Grupo creado con exito....", Toast.LENGTH_SHORT).show();
                }else{
                    String err = task.getException().getMessage().toString();
                    Toast.makeText(InicioActivity.this, "Error"+ err, Toast.LENGTH_SHORT).show();
                }
             }
         });
    }
}