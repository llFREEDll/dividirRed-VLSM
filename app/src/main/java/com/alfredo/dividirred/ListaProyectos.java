package com.alfredo.dividirred;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alfredo.dividirred.Utility.FirebaseHelper;
import com.alfredo.dividirred.Utility.SharePreferencesHelper;
import com.alfredo.dividirred.interfaces.Proyectos;
import com.alfredo.dividirred.interfaces.subRedes;
import com.alfredo.dividirred.models.SubRed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaProyectos extends AppCompatActivity implements Proyectos {

    private ArrayList<String> proyectos = new ArrayList<>();
    private ArrayAdapter<String> stringArrayAdapter;
    private CardView cardView_addRed;
    private ListView listView_proyectos;
    private EditText editText_nombreProyecto;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private String red = " " ;
    private List<String> listaProyectos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_proyectos);

        setTitle("Proyectos");
        listView_proyectos = findViewById(R.id.ListView_proyectos);
        editText_nombreProyecto = findViewById(R.id.editText_nombreProyecto);
        cardView_addRed = findViewById(R.id.cardView_addRed);

        firebaseHelper.SetProyecto(this);
        firebaseHelper.GetProyect(this);

        cardView_addRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText_nombreProyecto.getText().toString().isEmpty()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(ListaProyectos.this);
                    builder.setTitle("Nuevo proyecto");
                    builder.setMessage("Crear " + editText_nombreProyecto.getText().toString() + "?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseHelper.addProyect(editText_nombreProyecto.getText().toString(),ListaProyectos.this);
                            listaProyectos.add(editText_nombreProyecto.getText().toString());
                            stringArrayAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }



            }
        });
        listView_proyectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ListaProyectos.this);
                red = listView_proyectos.getAdapter().getItem(i).toString();
                builder.setTitle(red);
                builder.setMessage("Que deseas hacer ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Abrir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ListaProyectos.this, nuevoProyecto.class);
                        SharePreferencesHelper sharePreferencesHelper = new SharePreferencesHelper(ListaProyectos.this,"red");
                        sharePreferencesHelper.write("red", red);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListaProyectos.this);
                        builder.setTitle("Eliminar");
                        builder.setMessage("Deseas elimianar?");
                        builder.setCancelable(false);
                        final int pos = (int)listView_proyectos.getAdapter().getItemId(i);
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String red = listView_proyectos.getAdapter().getItem(i).toString();
                                firebaseHelper.deleteProyect(red,ListaProyectos.this);
                                listaProyectos.remove(pos);
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
                builder.show();

            }
        });

    }


    @Override
    public void getAll(List<String> proyectosList) {
        listaProyectos = proyectosList;
        stringArrayAdapter = new ArrayAdapter<>(ListaProyectos.this,android.R.layout.simple_list_item_1,listaProyectos);
        listView_proyectos.setAdapter(stringArrayAdapter);

    }
}