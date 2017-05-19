package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import HILOS_SERVICIOS.Tarea_Server_P2P;
import HILOS_SERVICIOS.WifiServerBroadcastReceiver;

public class P2PServer extends AppCompatActivity {

    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifichannel;
    private WifiServerBroadcastReceiver wifiServerReceiver;

    private IntentFilter wifiServerReceiverIntentFilter;
    private Intent serverServiceIntent;
    private ProgressDialog pd;
    private Button botonIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2_pserver);


        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifichannel = wifiManager.initialize(this, getMainLooper(), null);
        wifiServerReceiver = new WifiServerBroadcastReceiver(wifiManager, wifichannel, this);

        botonIniciar = (Button) findViewById(R.id.botonIniciar);

        wifiServerReceiverIntentFilter = new IntentFilter();;
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(wifiServerReceiver,wifiServerReceiverIntentFilter);

    }

    //MÉTODO QUE INICIA LA TAREA QUE PONE EL SERVIDOR A LA ESCUCHA
    public void iniciarServidor(View v){

        Tarea_Server_P2P tarea_server = new Tarea_Server_P2P(this, pd);
        tarea_server.execute();

        botonIniciar.setEnabled(false);
    }


    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopServer(null);
        //unregisterReceiver(wifiServerReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(wifiServerReceiver);
        } catch (IllegalArgumentException e) {

        }
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

    public Button getBotonIniciar() {
        return botonIniciar;
    }

    public void setBotonIniciar(Button botonIniciar) {
        this.botonIniciar = botonIniciar;
    }
}
