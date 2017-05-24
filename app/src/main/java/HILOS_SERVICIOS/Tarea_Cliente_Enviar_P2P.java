package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.example.atack08.groupload_app.P2PWifiDirect;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Tarea_Cliente_Enviar_P2P extends AsyncTask {

    private File fichero;
    private final int PUERTO_CONEXION_WIFI_P2P = 8988;
    private P2PWifiDirect p2PWifiDirect;
    private String nomFile;
    private ProgressDialog pd;
    private long sizeDescarga;
    private InetAddress serverIP;
    private long timeDescarga;


    public Tarea_Cliente_Enviar_P2P(File fichero, P2PWifiDirect p2PWifiDirect, ProgressDialog pd, InetAddress serverIP) {
        this.fichero = fichero;
        this.p2PWifiDirect = p2PWifiDirect;
        this.pd = pd;
        this.nomFile = fichero.getName();
        this.sizeDescarga = fichero.length();
        this.serverIP = serverIP;
    }

    @Override
    protected Object doInBackground(Object[] params) {

            try {

                Socket conexion = new Socket();
                conexion.bind(null);

                conexion.connect((new InetSocketAddress(serverIP,PUERTO_CONEXION_WIFI_P2P)),500);

                byte[] buffer =  new byte[1024];

                //ABRIMOS STREAMS
                DataOutputStream outData = new DataOutputStream(conexion.getOutputStream());

                //ENVIAMOS NOMBRE DE FICHERO Y TAMAÑO
                outData.writeUTF(nomFile);
                outData.writeLong(sizeDescarga);

                //CALCULAMOS PORCENTAJE BARRA DE PROGRESO
                float progreso = 0;
                float progresoAnterior = 0;
                float progresoParcial;

                //CREAMOS STREAMS PARA EL FICHERO
                FileInputStream inFile = new FileInputStream(fichero);

                //CUANDO HAY UNA CONEXIÓN ABRIMOS EL DIALOGO DE PROGRESO
                publishProgress(-1f);

                //EMPEZAMOS A COPIAR DEL STREAM
                //CALCULAMOS EL TIEMPO
                long timeI = System.currentTimeMillis();
                int len;

                while((len = inFile.read(buffer)) > 0){
                    outData.write(buffer,0,len);

                    progresoParcial = (len*100f)/sizeDescarga;
                    progreso = progreso + progresoParcial;

                    if((int)progreso != (int)progresoAnterior)
                        publishProgress(progreso);

                    progresoAnterior = progreso;
                }
                long timeF = System.currentTimeMillis();
                timeDescarga = timeF - timeI;

                //CERRAMOS STREAMS
                inFile.close();
                outData.close();
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
            pd.setMessage("Enviando: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024)) + " MB.");
            pd.show();
        }
        else{
            pd.setMessage("Enviando: " + nomFile + ", " + String.valueOf(Float.valueOf((sizeDescarga/1024)/1024)) + " MB.");
            pd.setProgress(progreso);

        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        pd.cancel();

        float sizeMegas = (sizeDescarga/1024f)/1024f;
        float timeSeconds = (timeDescarga/1000f);
        float sizeMegabits = sizeMegas*8;


        p2PWifiDirect.mostrarPanelInfo("Se completó la transferencia de ficheros.\n" +
                "\n\nVelocidad de descarga: " +  String.format("%.2f",(sizeMegabits/timeSeconds)) + " mb/s");
    }

}
