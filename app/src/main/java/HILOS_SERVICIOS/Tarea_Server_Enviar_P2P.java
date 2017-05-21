package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.atack08.groupload_app.P2PWifiDirect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by atack08 on 21/5/17.
 */

public class Tarea_Server_Enviar_P2P extends AsyncTask{

    private final int PUERTO_ESCUCHA_P2P = 8988;
    private File fichero;
    private P2PWifiDirect activity;
    private ServerSocket server;
    private Socket cliente;
    private String nomFile;
    private ProgressDialog pd;
    private long sizeDescarga;


    public Tarea_Server_Enviar_P2P(File fichero,P2PWifiDirect activity, ProgressDialog pd) {
        this.activity = activity;
        this.pd = pd;
        this.fichero = fichero;
        this.nomFile = fichero.getName();
        this.sizeDescarga = fichero.length();

    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            server = new ServerSocket(PUERTO_ESCUCHA_P2P);

            System.out.println("SERVIDOR ESCUCHANDO...");
            byte[] buffer =  new byte[1024];

            //PONEMOS EL SERVER A LA ESCUCHA
            cliente = server.accept();

            //ABRIMOS STREAMS
            DataOutputStream outData = new DataOutputStream(cliente.getOutputStream());

            //ENVIAMOS NOMBRE DE FICHERO Y TAMAÑO
            outData.writeUTF(nomFile);
            outData.writeLong(sizeDescarga);

            //CALCULAMOS PORCENTAJE
            float porcentaje = (1024f * 100f) / sizeDescarga;
            float progreso = 0;

            //CREAMOS STREAMS PARA EL FICHERO
            FileInputStream inFile = new FileInputStream(fichero);

            //CUANDO HAY UNA CONEXIÓN ABRIMOS EL DIALOGO DE PROGRESO
            publishProgress(-1f);

            //PASAMOS A LEER EL FICHERO
            int len;
            while((len = inFile.read(buffer)) > 0){

                outData.write(buffer,0,len);

                progreso = progreso + porcentaje;
                publishProgress(progreso);
            }

            //CERRAMOS STREAMS
            inFile.close();
            outData.close();
            cliente.close();

            server.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        activity.mostrarPanelInfo("Servidor a la escucha...");

        //CONFIGURAMOS EL PROGRESS DIALOG
        pd =  new ProgressDialog(activity);
        pd.setCancelable(false);
        pd.setTitle("Transferencia de fichero: ");
        pd.setMessage("Recibiendo...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        pd.setProgress(0);

    }

    @Override
    protected void onProgressUpdate(Object[] values) {

        int progreso = (int)((float)values[0]);

        if(progreso == -1){
            pd.setMessage("Enviando: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024)) + " MB.");
            pd.show();
        }
        else{
            pd.setMessage("Enviando: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024)) + " MB.");
            pd.setProgress(progreso);

            if(progreso == 100){
                pd.cancel();
                activity.mostrarPanelInfo("Se completó la tranferencia de ficheros.");
            }
        }

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        pd.cancel();
        activity.mostrarPanelInfo("Se completó la tranferencia de ficheros.");
    }

}



