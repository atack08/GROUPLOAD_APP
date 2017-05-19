package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.net.wifi.WifiInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;

import com.example.atack08.groupload_app.P2PClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by atack08 on 19/5/17.
 */

public class Tarea_Cliente_P2P extends AsyncTask {

    private File fichero;
    private final int PUERTO_CONEXION_WIFI_P2P = 7950;
    private WifiP2pDevice targetDevice;
    private WifiP2pInfo wifiInfo;
    private P2PClient p2pClient;
    private String nomFile;
    private ProgressDialog pd;
    private long sizeDescarga;

    private String msgFinish;

    public Tarea_Cliente_P2P(File fichero, WifiP2pDevice targetDevice, WifiP2pInfo wifiInfo, P2PClient p2pClient, ProgressDialog pd) {
        this.fichero = fichero;
        this.targetDevice = targetDevice;
        this.wifiInfo = wifiInfo;
        this.p2pClient = p2pClient;
        this.pd = pd;
        this.nomFile = fichero.getName();
        this.sizeDescarga = fichero.length();
    }

    @Override
    protected Object doInBackground(Object[] params) {

        if(!wifiInfo.isGroupOwner){
            //SACAMOS IP
            InetAddress targetIP = wifiInfo.groupOwnerAddress;

            try {
                Socket conexion = new Socket(targetIP,PUERTO_CONEXION_WIFI_P2P);
                byte[] buffer =  new byte[2048];

                //ABRIMOS STREAMS
                DataOutputStream outData = new DataOutputStream(conexion.getOutputStream());

                //ENVIAMOS NOMBRE DE FICHERO Y TAMAÑO
                outData.writeUTF(nomFile);
                outData.writeLong(sizeDescarga);

                //CALCULAMOS PORCENTAJE
                float porcentaje = (2048f * 100f) / sizeDescarga;
                float progreso = 0;

                //CREAMOS STREAMS PARA EL FICHERO
                FileInputStream inFile = new FileInputStream(fichero);

                //CUANDO HAY UNA CONEXIÓN ABRIMOS EL DIALOGO DE PROGRESO
                publishProgress(-1);

                int len;
                //PASAMOS A LEER EL FICHERO
                while((len = inFile.read(buffer)) > 0){
                    outData.write(buffer,0,len);

                    progreso = progreso + porcentaje;
                    publishProgress(progreso);
                }
                publishProgress(100);

                //CERRAMOS STREAMS
                inFile.close();
                outData.close();
                conexion.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
            msgFinish = "El dispositivo es 'GROUP OWNER' y no se pudo conocer su IP.";


        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //CONFIGURAMOS EL PROGRESS DIALOG
        pd =  new ProgressDialog(p2pClient);
        pd.setCancelable(false);
        pd.setTitle("Transferencia de fichero: ");
        pd.setMessage("Enviando...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        pd.setProgress(0);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);

        int progreso = (int)((float)values[0]);

        if(progreso == -1){
            pd.setMessage(nomFile + ", " + Float.valueOf((sizeDescarga/1024)/1024) + " MB.");
            pd.show();
        }
        else{
            pd.setProgress(progreso);

            if(progreso == 100){
                pd.cancel();
                p2pClient.mostrarPanelInfo("Se completó la tranferencia de ficheros.");
            }
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        p2pClient.mostrarPanelInfo(msgFinish);
    }
}
