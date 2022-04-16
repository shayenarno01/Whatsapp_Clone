package com.example.whatsappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeptupActivity extends AppCompatActivity {

    private EditText nombre, ciudad, provincia, edad, genero;
    private Button guardarinfo;
    private CircleImageView imagen_setup;

    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserID;
    private Toolbar toolbar;
    final static int Gallery_PICK = 1;
    private StorageReference UserProfileImagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_septup);

        nombre = (EditText) findViewById(R.id.nombre_setup);
        ciudad = (EditText) findViewById(R.id.ciudad_setup);
        provincia = (EditText) findViewById(R.id.provincia_setup);
        edad = (EditText) findViewById(R.id.edad_setup);
        genero = (EditText) findViewById(R.id.genero_setup);
        guardarinfo = (Button) findViewById(R.id.boton_setup);
        imagen_setup = (CircleImageView) findViewById(R.id.imagen_setup);

        toolbar = (Toolbar) findViewById(R.id.toolbar_setup);
        //setSupportActionBar(toolbar); revisar
        getSupportActionBar().setTitle("Completa tu perfil");
        dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        CurrentUserID = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        UserProfileImagen = FirebaseStorage.getInstance().getReference().child("imagesPerfil");

        guardarinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuardarInfoDB();
            }
        });

        imagen_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallery_PICK);
            }
        });
    }

    //@Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
  //      super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==Gallery_PICK && requestCode==RESULT_OK && data != null){
  //          Uri imageUri = data.getData();
    //        CropImage.activity()
              //      .setGuidelines(CropImageView.Guidelines.ON)
                //    .start(this);

      //  }
//        if (re)
    //}

    private void GuardarInfoDB() {
        String nom =nombre.getText().toString();
        String ciu =ciudad.getText().toString();
        String prov =provincia.getText().toString();
        String eda =edad.getText().toString();
        String gen =genero.getText().toString();

        if(TextUtils.isEmpty(nom)){
            Toast.makeText(this, "Debe ingresar su nombre", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(ciu)){
            Toast.makeText(this, "Debe ingresar su ciudad", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(prov)){
            Toast.makeText(this, "Debe ingresar su provincia", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(eda)){
            Toast.makeText(this, "Debe ingresar su edad", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(gen)){
            Toast.makeText(this, "Debe ingresar su genero", Toast.LENGTH_SHORT).show();
        }else{
            dialog.setTitle("Guardando sus datos");
            dialog.setMessage("Por favor espere a que finalice el proceso");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            HashMap map = new HashMap();
            map.put("nombre",nom);
            map.put("ciudad",ciu);
            map.put("provincia",prov);
            map.put("edad",eda);
            map.put("genero",gen);

            UserRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SeptupActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        EnviarAlInicio();
                    }else{
                        String err = task.getException().getMessage();
                        Toast.makeText(SeptupActivity.this, "Error"+err, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void EnviarAlInicio() {

        Intent intent = new Intent(SeptupActivity.this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}