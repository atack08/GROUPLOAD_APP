package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;

public class RolSelection extends AppCompatActivity {

    //VARIABLES DE SESIÓN
    private Servidor servidor;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rol_selection);

        //RECUPERAMOS DEL INTENT EL USUARIO Y EL SERVIDOR LOGEADOS
        Intent intent = getIntent();
        servidor = (Servidor)intent.getExtras().getSerializable("servidor");
        usuario = (Usuario)intent.getExtras().getSerializable("usuario");

        mostrarPanelInfo(usuario.getNombre() + " conectado correctamente al servidor "
                + servidor.getNombreServidor());

        //CONFIGURAMOS EL LABEL TITULO CONE L NOMBRE DEL SERVIDOR
        String titulo = "Conectado con: " + servidor.getNombreServidor();
        ((TextView)findViewById(R.id.labelNombreServidor)).setText(titulo);



    }


    //MÉTODO PARA MOSTRAR LOS DIALOGS DE ERROR
    public void mostrarPanelError(String msg){

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(msg);
        dialog.setTitle("ERROR");

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        dialog.show();

    }

    //MÉTODO PARA MOSTRAR INFO
    public void mostrarPanelInfo(String msg){

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(msg);
        dialog.setTitle("INFO");

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        dialog.show();

    }

    //MÉTODO QUE CAMBIA DE ACTIVIDAD CUANDO SE ELIGE UNIRSE A GRUPO
    public void unirseAGrupo(View v){

        Intent intent = new Intent(this, UnirseGrupo.class);

        intent.putExtra("servidor", servidor);
        intent.putExtra("usuario", usuario);

        startActivity(intent);
    }

    //MÉTODO QUE CAMBIA DE ACTIVIDAD CUANDO SE ELIGE CREAR GRUPO
    public void crearGrupo(View v){

        Intent intent = new Intent(this, CrearGrupo.class);

        intent.putExtra("servidor", servidor);
        intent.putExtra("usuario", usuario);

        startActivity(intent);
    }

    public void volver(View v){
        finish();

    }



}
