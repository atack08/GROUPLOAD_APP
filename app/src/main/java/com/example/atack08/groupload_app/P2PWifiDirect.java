package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

import HILOS_SERVICIOS.Tarea_Cliente_Enviar_P2P;
import HILOS_SERVICIOS.Tarea_Cliente_Recibir_P2P;
import HILOS_SERVICIOS.Tarea_Server_Enviar_P2P;
import HILOS_SERVICIOS.Tarea_Server_Recibir_P2P;
import HILOS_SERVICIOS.WifiDirectBroadcastReceiver;

public class P2PWifiDirect extends AppCompatActivity{

    private Spinner spinnerFicheros;
    private File ficheroSeleccionado;
    private ListView listViewDispositivos;

    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifichannel;
    private BroadcastReceiver wifiClientReceiver;

    private IntentFilter wifiClientReceiverIntentFilter;

    private WifiP2pDevice targetDevice;
    private WifiP2pInfo wifiInfo;

    private ProgressDialog pd;
    private ArrayList<WifiP2pDevice> listaDispositivos;

    private InetAddress serverIP;

    private final File CARPETA_DESCARGAS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    //BOTONES
    private Button botonConectar, botonEnviarFichero, botonRecibirFichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        wifichannel = wifiManager.initialize(this, getMainLooper(), null);
        wifiClientReceiver = new WifiDirectBroadcastReceiver(wifiManager, wifichannel, this);

        wifiClientReceiverIntentFilter = new IntentFilter();;
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        targetDevice = null;
        wifiInfo = null;

        registerReceiver(wifiClientReceiver, wifiClientReceiverIntentFilter);

        botonConectar = (Button)findViewById(R.id.botonConectarDispositivo);
        botonEnviarFichero = (Button)findViewById(R.id.botonEnviarFichero);
        botonRecibirFichero = (Button)findViewById(R.id.botonRecibirFichero);

        spinnerFicheros = (Spinner) findViewById(R.id.spinnerWifiFichero);
        rellenarFicheros();

        listViewDispositivos = (ListView)findViewById(R.id.listViewDispositivosWifi);
        listaDispositivos =  new ArrayList<>();

        //APLICAMOS LISTENER A LA LISTA
        listViewDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int arg2,long arg3) {

                String dispSeleccionado = ((TextView)view).getText().toString();

                for(WifiP2pDevice device: listaDispositivos){
                    if(dispSeleccionado.equalsIgnoreCase(device.deviceName)){
                        targetDevice = device;
                        break;
                    }
                }

                if(targetDevice != null)
                    botonConectar.setEnabled(true);

            }
        });


        //APLICAMOS LISTENER A SPINNER
        spinnerFicheros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ficheroSeleccionado = new File(CARPETA_DESCARGAS.getAbsolutePath() + "/" + parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //MÉTODO QUE PREPARA EL CLIENTE
    public void setNetworkToReadyState(WifiP2pInfo info, WifiP2pDevice device) {
        wifiInfo = info;
        targetDevice = device;

        if(info != null){
            if(wifiInfo.isGroupOwner){
                Toast.makeText(this, "ES GROUPOWNER: IP: " + wifiInfo.groupOwnerAddress.toString() , Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(this, "NO ES GROUPOWNER: IP: " + wifiInfo.groupOwnerAddress.toString(), Toast.LENGTH_LONG).show();
        }

        else
            System.out.println("INFO SUELTA NULL" );

        serverIP = wifiInfo.groupOwnerAddress;

    }

    public void activarBotonoes(){
        botonEnviarFichero.setEnabled(true);
        botonRecibirFichero.setEnabled(true);
    }

    public void desactivarBotones(){
        botonEnviarFichero.setEnabled(false);
        botonRecibirFichero.setEnabled(false);
    }

    private void stopClientReceiver()
    {
        try {
            unregisterReceiver(wifiClientReceiver);
        }
        catch(IllegalArgumentException e)
        {
            //This will happen if the server was never running and the stop button was pressed.
            //Do nothing in this case.
        }
    }


    //MÉTODO QUE LISTA EN EL SPINNER TODOS LOS FICHEROS DE LA CARPETA DESCARGAS
    public void rellenarFicheros(){

        //COMPROBAMOS EL ESTADO DE LA MEMORIA EXTERNA - SD
        String estado = Environment.getExternalStorageState();

        if(estado.equals(Environment.MEDIA_MOUNTED) || estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {

            File[] ficheros = CARPETA_DESCARGAS.listFiles();

            ArrayList<String> rutasFicheros = new ArrayList<>();

            for(File file: ficheros){
                rutasFicheros.add(file.getName());
            }

            ArrayAdapter<String> adaptador1 = new ArrayAdapter<String>(this,R.layout.spinner_ficheros_item,rutasFicheros);
            spinnerFicheros.setAdapter(adaptador1);

        }
        else
            mostrarPanelError("No se puede leer en la tarjeta SD");
    }

    //MÉTODO PARA BUSCAR DISPOSITIVOS
    public void buscarDispositivos(View view) {

        wifiManager.discoverPeers(wifichannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mostrarPanelInfo("BUSQUEDA REALIZADA CORRECTAMENTE.");

            }

            @Override
            public void onFailure(int reason) {

            }
        });

    }

    //MÉTODO QUE CONECTA CON UN DISPOSITIVO
    public void conectarConDispositivo(View v){

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = targetDevice.deviceAddress;

        wifiManager.connect(wifichannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mostrarPanelInfo("Conexión con " + targetDevice.deviceName + " establecida correctamente.");
                botonRecibirFichero.setEnabled(true);
                botonEnviarFichero.setEnabled(true);
            }

            @Override
            public void onFailure(int reason) {
                mostrarPanelError("No se pudo establecer la conexión.");
            }
        });
    }

    //MÉTODO PARA CONFECCIONAR LISTA DE DISPOSITIVOS ENCONTRADOS
    public void displayPeers(ArrayList<WifiP2pDevice> listaD)
    {
        this.listaDispositivos = listaD;
        final ArrayList<String> peersStringArrayList = new ArrayList<String>();

        //RELLENAMOS LA LISTA CON LOS NOMBRES DE LOS DISPOSITIVOS ENCONTRADOS
        for(WifiP2pDevice wd : listaDispositivos) {
            peersStringArrayList.add(wd.deviceName);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, peersStringArrayList.toArray());
        listViewDispositivos.setAdapter(arrayAdapter);

    }


    //MÉTODO WUE INICIA LA TAREA DE ENVIAR FICHERO
    public void enviarFichero(View v){

        if(ficheroSeleccionado != null && ficheroSeleccionado.exists()){

            Tarea_Server_Enviar_P2P tareaTransferServer;
            Tarea_Cliente_Enviar_P2P tareaTrasferCliente;

            if(wifiInfo.isGroupOwner) {
                tareaTransferServer = new Tarea_Server_Enviar_P2P(ficheroSeleccionado, this, pd);
                tareaTransferServer.execute();
            }
            else {
                tareaTrasferCliente = new Tarea_Cliente_Enviar_P2P(ficheroSeleccionado, this, pd, serverIP);
                tareaTrasferCliente.execute();
            }

        }
        else
            mostrarPanelError("No hay fichero seleccionado o este no es correcto.");

    }

    //MÉTODO WUE INICIA LA TAREA DE RECIBIR FICHERO
    public void recibirFichero(View v){

        Tarea_Server_Recibir_P2P tareaTransferServer;
        Tarea_Cliente_Recibir_P2P tareaTrasferCliente;

        if(wifiInfo.isGroupOwner) {
            tareaTransferServer = new Tarea_Server_Recibir_P2P(this,pd);
            tareaTransferServer.execute();
        }
        else {
            tareaTrasferCliente = new Tarea_Cliente_Recibir_P2P(this,pd,serverIP);
            tareaTrasferCliente.execute();
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

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Continue to listen for wifi related system broadcasts even when paused
        //stopClientReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Kill thread that is transferring data

        //Unregister broadcast receiver
        stopClientReceiver();
    }

    public void volver(View v){

        //Unregister broadcast receiver
        wifiManager.removeGroup(wifichannel,null);
        finish();

    }
}
