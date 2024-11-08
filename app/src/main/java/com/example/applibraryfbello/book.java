package com.example.applibraryfbello;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class book extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText idBook, author, name;
    Switch sAvailable;
    Button bSave, bSearch, bEdit, bDelete,  bList;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        idBook = findViewById(R.id.etidBook);
        name = findViewById(R.id.etName);
        author = findViewById(R.id.etAuthor);
        sAvailable = findViewById(R.id.swAvailable);
        message = findViewById(R.id.tvMessageB);
        bSave = findViewById(R.id.btnSave);
        bSearch = findViewById(R.id.btnSearch);
        bEdit = findViewById(R.id.btnEdit);
        bDelete = findViewById(R.id.btnDelete);
        bList = findViewById(R.id.btnList);
        // Eventos
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!idBook.getText().toString().isEmpty()){
                    db.collection("book")
                            .whereEqualTo("idbook", idBook.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (!task.getResult().isEmpty()){
                                            for (QueryDocumentSnapshot document: task.getResult()){
                                                // Asignar el contenido de los campos a los datos de pantalla
                                                name.setText(document.getString("name"));
                                                author.setText(document.getString("author"));
                                                sAvailable.setChecked(document.getDouble("available") == 1);
                                            }
                                        }
                                        else{
                                            message.setTextColor(Color.parseColor("#E14227"));
                                            message.setText("El id del libro NO EXISTE. Inténtelo con otro...");
                                        }
                                    }
                                }
                            });

                }
                else{
                    message.setTextColor(Color.parseColor("#E14227"));
                    message.setText("Debe ingresar el id del libro a buscar...");
                }
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mIdBook = idBook.getText().toString();
                String mName = name.getText().toString();
                String mAuthor = author.getText().toString();
                int mAvailable = sAvailable.isChecked() ? 1 : 0;
                if (checkData(mIdBook, mName, mAuthor)){
                    // Buscar el id del libro y si no existe, que lo guarde
                    db.collection("book")
                            .whereEqualTo("idbook", idBook.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().isEmpty()){
                                            Map<String, Object> mapBook = new HashMap<>();
                                            mapBook.put("idbook", mIdBook);
                                            mapBook.put("name", mName);
                                            mapBook.put("author", mAuthor);
                                            mapBook.put("available", mAvailable);
                                            // Crear el documento (registro) en la colección book
                                            db.collection("book") // Método de tipo promesa
                                                    .add(mapBook)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            message.setTextColor(Color.parseColor("#1AA816"));
                                                            message.setText("Libro se ha agregado exitosamente, con id: "+documentReference.getId());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            message.setTextColor(Color.parseColor("#E14227"));
                                                            message.setText("No se agregó el libro. Inténtelo más tarde...");
                                                        }
                                                    });
                                        }
                                        else{
                                            message.setTextColor(Color.parseColor("#E14227"));
                                            message.setText("Id del libro EXISTENTE. Inténtelo con otro...");
                                        }
                                    }
                                }
                            });
                }
                else {
                    message.setTextColor(Color.parseColor("#FF4545"));
                    message.setText("Debe dilegenciar todos los datos...");
                }
            }
        });
    }

    private boolean checkData(String mIdBook, String mName, String mAuthor) {
        return !mIdBook.isEmpty() && !mName.isEmpty() && !mAuthor.isEmpty();
    }
}