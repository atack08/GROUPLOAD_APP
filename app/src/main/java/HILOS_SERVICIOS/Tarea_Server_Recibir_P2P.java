package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import com.example.atack08.groupload_app.P2PWifiDirect;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Tarea_Server_Recibir_P2P extends AsyncTask {

    private final int PUERTO_ESCUCHA_P2P = 8988;
    private final File CARPETA_PUBLICA_DESCARGAS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private P2PWifiDirect activity;
    private ServerSocket server;
    private Socket cliente;
    private String nomFile;
    private ProgressDialog pd;
    private long sizeDescarga;


    public Tarea_Server_Recibir_P2P(P2PWifiDirect activity, ProgressDialog pd) {
        this.activity = activity;
        this.pd = pd;

    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            server = new ServerSocket(PUERTO_ESCUCHA_P2P);

            byte[] buffer =  new byte[1024];

            //PONEMOS EL SERVER A LA ESCUCHA
            cliente = server.accept();

            //ABRIMOS STREAMS
            DataInputStream inData = new DataInputStream(cliente.getInputStream());

            //RECATAMOS EL NOMBRE DEL FICHERO Y EL TAMAÑO
            nomFile = inData.readUTF();
            sizeDescarga = inData.readLong();

            //CALCULAMOS PORCENTAJE
            float porcentaje = (1024f * 100f) / sizeDescarga;
            float progreso = 0;
            float progresoAnterior = 0;

            //CREAMOS STREAMS PARA EL FICHERO
            FileOutputStream outFile = new FileOutputStream(new File(CARPETA_PUBLICA_DESCARGAS.getAbsolutePath() + "/" + nomFile));

            //CUANDO HAY UNA CONEXIÓN ABRIMOS EL DIALOGO DE PROGRESO
            publishProgress(-1f);


            //PASAMOS A LEER EL FICHERO
            int len ;

            while((len = inData.read(buffer)) > 0){
                outFile.write(buffer,0,len);

                progreso = progreso + porcentaje;

                if((int)progreso != (int)progresoAnterior)
                    publishProgress(progreso);

                progresoAnterior = progreso;

            }

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
            pd.setMessage("Recibiendo: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024)) + " MB.");
            pd.show();
        }
        else{
            pd.setMessage("Recibiendo: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024)) + " MB.");
            pd.setProgress(progreso);

        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        pd.cancel();
        activity.mostrarPanelInfo("Se completó la tranferencia de ficheros.");
    }

}
