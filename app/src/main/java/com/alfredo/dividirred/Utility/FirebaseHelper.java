package com.alfredo.dividirred.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alfredo.dividirred.ListaProyectos;
import com.alfredo.dividirred.MainActivity;
import com.alfredo.dividirred.interfaces.Proyectos;
import com.alfredo.dividirred.interfaces.subRedes;
import com.alfredo.dividirred.models.SubRed;
import com.alfredo.dividirred.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseHelper {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isUser;
    private subRedes subRed;
    private Proyectos proyectos;

    public void IsUser(final Context context, final String user, final String password, final ProgressDialog progressDialog){
        isUser = false;


        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean flag = false;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(String.valueOf(document.get("user")).equals(user) && String.valueOf(document.get("password")).equals(password)){
                                     flag = true;
                                    SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(context,"usuario");
                                    sharePreferencesHelper.write("user", document.getId());

                                    Intent intent = new Intent(context, com.alfredo.dividirred.ListaProyectos.class);

                                    context.startActivity(intent);
//                                    ((Activity)context).finish();

                                }
                            }
                            if (!flag){
                                Toast.makeText(context,"ERROR: Credenciales incorrectas",Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        progressDialog.dismiss();
                    }
                });


    }
    public void SetSubRed(subRedes subRed){
        this.subRed = subRed;
    }
    public void SetProyecto(Proyectos proyectos){
        this.proyectos = proyectos;
    }

    public void GetProyect(final Context context){

        final List <String> proyectosLista = new ArrayList<>();

        SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(context,"usuario");
        String usuario = sharePreferencesHelper.read("user");

        db.collection("/usuarios/"+ usuario + "/red1" )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                proyectosLista.add(document.getId());

                            }
                            proyectos.getAll(proyectosLista);

                        } else {
                            Toast.makeText(context,"Error al conectar a la base de datos proyectos",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    public void GetSubRed(final Context context){
        isUser = false;

        final List <String> subRedLista = new ArrayList<>();
        SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(context,"usuario");
        String usuario = sharePreferencesHelper.read("user");
        sharePreferencesHelper = new SharePreferencesHelper(context,"red");
        String red = sharePreferencesHelper.read("red");
////red1
        db.collection("/usuarios/"+ usuario + "/red1/" + red + "/" + red)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        Log.e("lista","entra");
                        if (task.isSuccessful()) {
//                            Log.e("lista","entra");
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.e("lista",document.getData().toString() + "\n");
//                                Log.e("lista",document.getId());
                                subRedLista.add("IP inicial: " + document.get("ipinicial") + "\n" +
                                        "IP final: " + document.get("ipfinal") + "\n" +
                                        "Mascara: " + document.get("mascara") + "\n" +
                                        "Nodos: " + document.get("nodos") + "\n" +
                                        "Descripcion: " + document.get("descripcion"));
//                                Log.e("lista","lista = " + subRedLista.get(0));

                            }
                            subRed.getAll(subRedLista);

                        } else {
                            Log.e( "Error", ":c");
                        }
                    }
                });


    }

    public void SaveData(List<String> listaNodos, final Context context) {

        String[] datos;
        String id;

        SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(context,"usuario");
        final String usuario = sharePreferencesHelper.read("user");
        sharePreferencesHelper = new SharePreferencesHelper(context,"red");
        final String red = sharePreferencesHelper.read("red");

        for (int i = 0 ; i < listaNodos.size() ; i ++){
            datos = listaNodos.get(i).split("\n");

            final Map<String,Object> nodo = new HashMap<>();
//
            id = datos[4].substring(datos[4].indexOf(":") + 2);
            nodo.put("ipinicial",datos[0].substring(datos[0].indexOf(":") + 2));
            nodo.put("ipfinal",datos[1].substring(datos[1].indexOf(":") + 2));
            nodo.put("mascara",datos[2].substring(datos[2].indexOf(":") + 2));
            nodo.put("nodos",datos[3].substring(datos[3].indexOf(":") + 2));
            nodo.put("descripcion",id);


            DocumentReference washingtonRef = db.collection("/usuarios/"+ usuario + "/red1/" + red + "/" + red).document(id);

            final String finalId = id;
            washingtonRef
                    .update(nodo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(context,"Editado correctamente",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            db.collection("/usuarios/"+ usuario + "/red1/" + red + "/" + red)
                                    .document(finalId).set(nodo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()){
//                                        Toast.makeText(context,"Agregado Correctamente" , Toast.LENGTH_LONG);
                                    }else {

                                        Toast.makeText(context, "no agregado" , Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                    });



        }

    }
    public void DeleteNode(final Context context , String nodo){
        SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(context,"usuario");
        final String usuario = sharePreferencesHelper.read("user");
        sharePreferencesHelper = new SharePreferencesHelper(context,"red");
        final String red = sharePreferencesHelper.read("red");

        db.collection("/usuarios/"+ usuario + "/red1/" + red + "/" + red).document(nodo)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(context, "Se elimino correctamente" , Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "No se elimino" , Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void addProyect(String id, final Context context) {

        SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(context,"usuario");
        final String usuario = sharePreferencesHelper.read("user");

        final Map<String,Object> nodo = new HashMap<>();
        nodo.put("id",id);


//        /usuarios/u1/red1/red1/red1
        db.collection("/usuarios/"+ usuario + "/red1")
                .document(id).set(nodo).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Error al eliminar",Toast.LENGTH_LONG).show();
            }
        });


    }

    public void deleteProyect(String red, final Context context) {

        SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(context,"usuario");
        final String usuario = sharePreferencesHelper.read("user");

        final Map<String,Object> nodo = new HashMap<>();
        nodo.put("id",red);


//        /usuarios/u1/red1/red1/red1
        db.collection("/usuarios/"+ usuario + "/red1")
                .document(red).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Error al eliminar",Toast.LENGTH_LONG).show();
            }
        });
    }
}
