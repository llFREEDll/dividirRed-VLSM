package com.alfredo.dividirred;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alfredo.dividirred.Utility.FirebaseHelper;
import com.alfredo.dividirred.Utility.SharePreferencesHelper;
import com.alfredo.dividirred.interfaces.subRedes;

import java.util.List;

public class nuevoProyecto extends AppCompatActivity implements subRedes {

    private EditText editText_ip,editText_mascara,editText_nodos,
    editText_descripcion;
    private CardView cardView_add;
    private ListView listView_nodos;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private ArrayAdapter<String> stringArrayAdapter;
    private List<String> proyectosList;
    private boolean datosIniciales = true;
    private String mascaraInicia = "24", primerIP = "192.168.10.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_proyecto);

        editText_ip = findViewById(R.id.editText_ip);
        editText_mascara = findViewById(R.id.editTextNumber_mascara);
        editText_nodos = findViewById(R.id.editTextNumber_nodos);
        editText_descripcion = findViewById(R.id.editTextText_Descripcion);
        cardView_add = findViewById(R.id.cardView_nodo);
        listView_nodos =  findViewById(R.id.ListView_nodos);
        final EvaluarRed evaluarRed = new EvaluarRed();

        firebaseHelper.SetSubRed(this);
        firebaseHelper.GetSubRed(this);

        SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(nuevoProyecto.this, "red");
        String red = sharePreferencesHelper.read("red");
        setTitle(red);

        evaluarRed.setSubredes(nuevoProyecto.this);

        cardView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (datosIniciales){
                    primerIP = editText_ip.getText().toString();
                    mascaraInicia = editText_mascara.getText().toString();
                    datosIniciales = false;
                }

                evaluarRed.setNodosAnteriores(proyectosList);
                evaluarRed.DividirIP(nuevoProyecto.this,
                        editText_ip.getText().toString(),
                        editText_mascara.getText().toString(),
                        editText_nodos.getText().toString(),
                        editText_descripcion.getText().toString());
                evaluarRed.NewNodo();
                stringArrayAdapter.notifyDataSetChanged();


            }
        });

        listView_nodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {

                final String nodo = listView_nodos.getAdapter().getItem(i).toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(nuevoProyecto.this);
                builder.setTitle("Eliminar");
                builder.setMessage("Deseas eliminar?");
                builder.setCancelable(false);
                final int pos = (int)listView_nodos.getAdapter().getItemId(i);
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String[] aux = nodo.split("\n");
                        String id = aux[4].substring(13);

                        firebaseHelper.DeleteNode(nuevoProyecto.this,id);
                        proyectosList.remove(pos);
                        if (proyectosList.size() > 0){

                            aux = proyectosList.get(0).split("\n");

                            //relleno los campos para evitar un error que se da en la elimiancion
                            //esto lo hago para ahorrar codigo al hacer un metodo especial para la eliminacion

                            editText_ip.setText(aux[0].substring(12));
                            editText_mascara.setText(aux[2].substring(9));
                            editText_nodos.setText(aux[3].substring(7));
                            editText_descripcion.setText(aux[4].substring(13));

                            evaluarRed.setNodosAnteriores(proyectosList);
                            evaluarRed.DividirIP(nuevoProyecto.this,
                                    //ipinicial:
                                    primerIP,
                                    //mascara:
                                    mascaraInicia,
//                                    aux[2].substring(9),
                                    //nodos:
                                    aux[3].substring(7),
                                    //descripcion:
                                    aux[4].substring(13));
                            evaluarRed.NewNodo();
                        }

                        stringArrayAdapter.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();



            }
        });


    }

    @Override
    public void getAll(List<String> proyectosListList) {
        proyectosList = proyectosListList;
        stringArrayAdapter = new ArrayAdapter<>(nuevoProyecto.this,android.R.layout.simple_list_item_1,proyectosList);
        listView_nodos.setAdapter(stringArrayAdapter);

    }
}