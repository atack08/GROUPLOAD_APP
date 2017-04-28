package com.example.atack08.groupload_app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


import BEANS.Servidor;

public class ServerSelect extends AppCompatActivity {

    //CONSTANTES PARA PERMISOS
    private final int PERMISO_WRITE_READ_EXTERNAL_STORAGE = 0 ;

    private Spinner comboServidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_select);

        //RECUPERAMOS EL SPINNER DE SERVIDORES
        comboServidor = (Spinner)findViewById(R.id.comboServidores);

        //CARGAMOS LOS SERVIDORES EN LA EL COMBO
        //NOTA - SI HUBIESE MÁS SERVIDORES SE PODRÍA CREAR UN FICHERO XML PARA ALMACENARLOS
        Servidor s1 = new Servidor("HALL-10000","10.10.120.155","España");
        Servidor s2 = new Servidor("RedIris","10.10.10.101","España");
        ArrayList<Servidor> listaS = new ArrayList<>();
        listaS.add(s1);listaS.add(s2);
        ArrayAdapter<Servidor> adapter = new ArrayAdapter<>(this,R.layout.layout_spinner_servidor,R.id.descripcionServidor,listaS);

        comboServidor.setAdapter(adapter);

        //COMPROBAMOS PERMISOS
        comprobarPermisos();

    }


    //MÉTODO PARA CONTROLAR EL BOTÓN CONECTAR
    //COMPRUEBA EL FORMULARIO Y REALIZA LA CONEXIÓN AL SERVIDOR
    public void conectar(View v){

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
    public void mostrarPanelPermisos(String msg){

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

    //MÉTODO PARA COMPROBAR LOS PERMISOS DEL SISTEMA
    public void comprobarPermisos(){
        
        //COMPROBAMOS PERMISOS
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISO_WRITE_READ_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISO_WRITE_READ_EXTERNAL_STORAGE);
        }



        


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISO_WRITE_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "PERMISO SD CONCEDIDO", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "PERMISO SD DENEGADO", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}