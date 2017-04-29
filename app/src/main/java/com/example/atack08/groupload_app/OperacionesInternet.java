package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Button;

import BEANS.Servidor;
import HILOS_SERVICIOS.Estado_Servidor;


public class OperacionesInternet {

    private Estado_Servidor estadoServidor; //HILO QUE COMPRUEBA ESTADO DEL SERVIDOR
    private boolean isOnline;
    private Context context;
    private Button botonConectar;

    public OperacionesInternet(Context context, Button botonConectar){
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

    //MÉTODO PARA COMPROBAR SI UN SERVIDOR ESTÁ ONLINE
    public  synchronized void pedirConectividadServidor(Servidor servidor){

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
}
