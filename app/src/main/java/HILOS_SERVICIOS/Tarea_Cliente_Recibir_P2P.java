package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.net.wifi.p2p.WifiP2pDevice;
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by atack08 on 21/5/17.
 */

public class Tarea_Cliente_Recibir_P2P extends AsyncTask {
    private final int PUERTO_CONEXION_WIFI_P2P = 8988;
    private P2PWifiDirect p2PWifiDirect;
    private String nomFile;
    private ProgressDialog pd;
    private long sizeDescarga;
    private InetAddress serverIP;
    private final File CARPETA_PUBLICA_DESCARGAS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private float tasaTransfer;


    public Tarea_Cliente_Recibir_P2P(P2PWifiDirect p2PWifiDirect, ProgressDialog pd, InetAddress serverIP) {

        this.p2PWifiDirect = p2PWifiDirect;
        this.pd = pd;
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
            DataInputStream inData = new DataInputStream(conexion.getInputStream());

            //RECATAMOS EL NOMBRE DEL FICHERO Y EL TAMAÑO
            nomFile = inData.readUTF();
            sizeDescarga = inData.readLong();

            //CALCULAMOS PORCENTAJE
            float porcentaje = (1024f * 100f) / sizeDescarga;
            float progreso = 0;

            //CREAMOS STREAMS PARA EL FICHERO
            FileOutputStream outFile = new FileOutputStream(new File(CARPETA_PUBLICA_DESCARGAS.getAbsolutePath() + "/" + nomFile));

            //CUANDO HAY UNA CONEXIÓN ABRIMOS EL DIALOGO DE PROGRESO
            publishProgress(-1f);

            //PASAMOS A LEER EL FICHERO
            long timeI;
            long timeF;
            int len = inData.read(buffer);

            while(len > 0){
                timeI = System.currentTimeMillis();
                outFile.write(buffer,0,len);


                len = inData.read(buffer);
                timeF = System.currentTimeMillis();

                tasaTransfer = ((1024f/(timeF - timeI))*1000)/1024f; //KB por segundo

                progreso = progreso + porcentaje;
                publishProgress(progreso);
            }
            publishProgress(100);

            //CERRAMOS STREAMS
            outFile.close();
            inData.close();
            conexion.close();

        } catch (IOException e) {
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
            pd.setMessage("Recibiendo: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024)) + " MB.");
            pd.show();
        }
        else{
            pd.setMessage("Recibiendo: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024))
                    + " MB. Velocidad: " + String.valueOf(tasaTransfer) + " KB/s");
            pd.setProgress(progreso);

            if(progreso == 100){
                pd.cancel();
                p2PWifiDirect.mostrarPanelInfo("Se completó la tranferencia de ficheros.");
            }
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

    }
}

