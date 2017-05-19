package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.atack08.groupload_app.P2PServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Tarea_Server_P2P extends AsyncTask {

    private final int PUERTO_ESCUCHA_P2P = 7950;
    private final File CARPETA_PUBLICA_DESCARGAS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private P2PServer activity;
    private ServerSocket server;
    private Socket cliente;
    private String nomFile;
    private ProgressDialog pd;
    private long sizeDescarga;

    public Tarea_Server_P2P(P2PServer activity, ProgressDialog pd) {
        this.activity = activity;
        this.pd = pd;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            server = new ServerSocket(PUERTO_ESCUCHA_P2P);
            byte[] buffer =  new byte[2048];

            //PONEMOS EL SERVER A LA ESCUCHA
            cliente = server.accept();

            //ABRIMOS STREAMS
            DataInputStream inData = new DataInputStream(cliente.getInputStream());

            //RECATAMOS EL NOMBRE DEL FICHERO Y EL TAMAÑO
            nomFile = inData.readUTF();
            sizeDescarga = inData.readLong();

            //CALCULAMOS PORCENTAJE
            float porcentaje = (2048f * 100f) / sizeDescarga;
            float progreso = 0;

            //CREAMOS STREAMS PARA EL FICHERO
            FileOutputStream outFile = new FileOutputStream(new File(CARPETA_PUBLICA_DESCARGAS.getAbsolutePath() + "/" + nomFile));

            //CUANDO HAY UNA CONEXIÓN ABRIMOS EL DIALOGO DE PROGRESO
            publishProgress(-1);

            int len;
            //PASAMOS A LEER EL FICHERO
            while((len = inData.read(buffer)) > 0){
                outFile.write(buffer,0,len);

                progreso = progreso + porcentaje;
                publishProgress(progreso);
            }
            publishProgress(100);

            //CERRAMOS STREAMS
            outFile.close();
            inData.close();
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
            pd.setMessage(nomFile + ", " + Float.valueOf((sizeDescarga/1024)/1024) + " MB.");
            pd.show();
        }
        else{
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
        activity.mostrarPanelInfo("El servidor ya no está a la escucha");
        activity.getBotonIniciar().setEnabled(true);

    }
}
