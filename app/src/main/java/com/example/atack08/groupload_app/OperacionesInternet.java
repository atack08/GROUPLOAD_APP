package com.example.atack08.groupload_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;
import HILOS_SERVICIOS.Estado_Servidor;
import HILOS_SERVICIOS.Lista_Grupos_Servidor;
import HILOS_SERVICIOS.Login_Servidor;


public class  OperacionesInternet {

    private Estado_Servidor estadoServidor; //HILO QUE COMPRUEBA ESTADO DEL SERVIDOR
    private boolean isOnline;
    private Context context;
    private Button botonConectar;
    private ServerSelect activity;


    public OperacionesInternet(Context context, Button botonConectar,ServerSelect ss ){

        this.activity = ss;
        this.context = context;
        this.botonConectar = botonConectar;
    }

    //MÉTODO PARA COMPROBAR SI EL DISPOSITIVO TIENE HABILITADA ALGUNA RED
    public boolean pedirConectividad(){

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        if(actNetInfo == null)
            return false;

        return actNetInfo.isConnected();
    }

    //MÉTODO QUE REALIZA EL LOGEO EN UN SERVIDOR Y CAMBIA DE ACTIVIDAD
    public synchronized void logearEnServidor(Servidor server, Usuario user){

        Login_Servidor logeo = new Login_Servidor(server, this);
        logeo.execute(user);

    }

    //MÉTODO PARA COMPROBAR SI UN SERVIDOR ESTÁ ONLINE
    public synchronized void pedirConectividadServidor(Servidor servidor){

        Estado_Servidor es = new Estado_Servidor(this);
        es.execute(servidor);
    }

    

    //MÉTODO PARA MOSTRAR INFO
    public void mostrarPanelInfo(String msg){

        AlertDialog dialog = new AlertDialog.Builder(context).create();
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

    //MÉTODO PARA MOSTRAR LOS DIALOGS DE ERROR
    public void mostrarPanelError(String msg){

        AlertDialog dialog = new AlertDialog.Builder(context).create();
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

    public Button getBotonConectar() {
        return botonConectar;
    }

    public ServerSelect getActivity() {
        return activity;
    }


}
