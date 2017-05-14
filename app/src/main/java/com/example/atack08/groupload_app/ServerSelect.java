package com.example.atack08.groupload_app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;


import BEANS.Servidor;
import BEANS.Usuario;
import HILOS_SERVICIOS.Hilos_Pruebas;

public class ServerSelect extends AppCompatActivity {

    //CONSTANTES PARA PERMISOS
    private final int PERMISO_WRITE_READ_EXTERNAL_STORAGE = 0 ;

    //CLASE QUE CONTROLA LAS OPERACIONES DE RED WAN
    private OperacionesInternet redWAN;

    private Spinner comboServidor;
    private Button botonConectar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_select);

        //RECUPERAMOS EL SPINNER DE SERVIDORES
        comboServidor = (Spinner)findViewById(R.id.comboServidores);

        //CARGAMOS LOS SERVIDORES EN LA EL COMBO
        //NOTA - SI HUBIESE MÁS SERVIDORES SE PODRÍA CREAR UN FICHERO XML PARA ALMACENARLOS
        Servidor s1 = new Servidor("HALL-10000","89.131.153.38","España");
        ArrayList<Servidor> listaS = new ArrayList<>();
        listaS.add(s1);
        ArrayAdapter<Servidor> adapter = new ArrayAdapter<>(this,R.layout.layout_spinner_servidor,R.id.descripcionServidor,listaS);

        comboServidor.setAdapter(adapter);

        //RECUPERAMOS COMPONENTES DE LA INTERFACE GRÁFICA
        botonConectar = (Button)findViewById(R.id.botonConectar);

        //COMPROBAMOS PERMISOS
        comprobarPermisos();

        //UNA VEZ CONCEDIDOS PERMISOS INICIALIZAMOS LA CLASE QUE CONTROLA LA RED WAN
        redWAN = new OperacionesInternet(this, botonConectar, this);

    }


    //MÉTODO PARA CONTROLAR EL BOTÓN CONECTAR
    //COMPRUEBA EL FORMULARIO Y REALIZA LA CONEXIÓN AL SERVIDOR
    public void conectar(View v){

        //RESCATAMOS LOS DATOS DEL FORMULARIO
        String nick = ((EditText)findViewById(R.id.editUser)).getText().toString();
        String pass = ((EditText)findViewById(R.id.editPassword)).getText().toString();

        if(nick.equals("") || pass.equals(""))
            mostrarPanelError("Introduzca usuario y contraseña para conectarse");
        else{

            //RECATAMOS SERVIDOR ELEGIDO
            Servidor servidor = (Servidor) comboServidor.getSelectedItem();
            //CONSTRUIMOS EL USUARIO
            Usuario user = new Usuario(nick,pass,"");

            //REALIZAMOS EL LOGEO
            redWAN.logearEnServidor(servidor,user);

        }

    }


    //MÉTODO QUE COMPRUEBA EL ESTADO DE UN SERVIDOR
    public void comprobarServidor(View v){

        //RECATAMOS SERVIDOR ELEGIDO
        Servidor servidor = (Servidor) comboServidor.getSelectedItem();

        //COMPROBAMOS SI TENEMOS CONECTIVIDAD DE RED Y POSTERIORMENTE SI EL SERVIDOR ESTÁ ONLINE
        if(redWAN.pedirConectividad()) {
            redWAN.pedirConectividadServidor(servidor);
        }
        else
            mostrarPanelError("No tienes dispositivos de red habilitados.");

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

    //MÉTODO PARA CAMBIAR DE VENTANA DENTRO DE LA APLICACIÓN
    public void cambiarActividadLogin(Servidor server, Usuario user){

        Intent intent = new Intent(this, RolSelection.class);

        intent.putExtra("servidor", server);
        intent.putExtra("usuario", user);


        startActivity(intent);

    }

    public void salir(View v){
        this.finish();
    }


}