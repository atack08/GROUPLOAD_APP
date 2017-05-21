package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.example.atack08.groupload_app.P2PWifiDirect;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by atack08 on 19/5/17.
 */

public class Tarea_Cliente_Enviar_P2P extends AsyncTask {

    private File fichero;
    private final int PUERTO_CONEXION_WIFI_P2P = 8988;
    private P2PWifiDirect p2PWifiDirect;
    private String nomFile;
    private ProgressDialog pd;
    private long sizeDescarga;
    private InetAddress serverIP;

    private float tasaTransfer;


    public Tarea_Cliente_Enviar_P2P(File fichero, P2PWifiDirect p2PWifiDirect, ProgressDialog pd, InetAddress serverIP) {
        this.fichero = fichero;
        this.p2PWifiDirect = p2PWifiDirect;
        this.pd = pd;
        this.nomFile = fichero.getName();
        this.sizeDescarga = fichero.length();
        this.serverIP = serverIP;
        this.tasaTransfer = 0;

        System.out.println("LLEGA Y EJECUTA BIEN EL CONSTRUCTOR");


    }

    @Override
    protected Object doInBackground(Object[] params) {

            try {
                System.out.println("ENTRA AL FILE TRANSFER");

                Socket conexion = new Socket();
                conexion.bind(null);

                conexion.connect((new InetSocketAddress(serverIP,PUERTO_CONEXION_WIFI_P2P)),500);

                System.out.println("ESTABLECE LA CONEXION");

                byte[] buffer =  new byte[1024];

                //ABRIMOS STREAMS
                DataOutputStream outData = new DataOutputStream(conexion.getOutputStream());

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
                long timeI;
                long timeF;
                int len = inFile.read(buffer);

                while(len > 0){
                    timeI = System.currentTimeMillis();
                    outData.write(buffer,0,len);


                    len = inFile.read(buffer);
                    timeF = System.currentTimeMillis();

                    tasaTransfer = ((1024f/(timeF - timeI))*1000f)/1024f; //KB por segundo

                    progreso = progreso + porcentaje;
                    publishProgress(progreso);
                    Thread.sleep(5);
                }

                //CERRAMOS STREAMS
                inFile.close();
                outData.close();
                conexion.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //CONFIGURAMOS EL PROGRESS DIALOG
        pd =  new ProgressDialog(p2PWifiDirect);
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
            pd.setMessage("Enviando: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024)) + " MB.");
            pd.show();
        }
        else{
            pd.setMessage("Enviando: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024))
                    + " MB. Velocidad: " + String.valueOf(tasaTransfer) + " KB/s");
            pd.setProgress(progreso);

        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        pd.cancel();
        p2PWifiDirect.mostrarPanelInfo("Se completó la tranferencia de ficheros.");
    }

}
