package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;

import com.example.atack08.groupload_app.ConectarGrupo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;


public class Descarga_partes extends AsyncTask{

    private Usuario ususario;
    private Servidor servidor;
    private Grupo grupo;
    private int porcentaje;
    private long sizeDescarga;
    private ConectarGrupo cg;
    private Socket conexion;
    private File filePartes;
    private ProgressDialog pd;
    private String nomFile;
    private long timeDescarga;

    private final int PUERTO_DESCARGA_PARTES = 1507;
    private final File CARPETA_DESCARGAS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public Descarga_partes(Usuario ususario, Servidor servidor, Grupo grupo, int p, ConectarGrupo cg, ProgressDialog pd) {
        this.ususario = ususario;
        this.servidor = servidor;
        this.grupo = grupo;
        this.porcentaje = p;
        this.cg = cg;
        this.pd = pd;
    }

    @Override
    protected Object doInBackground(Object[] params) {


        //CONECTAMOS CON SERVIDOR
        try {
            conexion = new Socket(servidor.getIpServidor(),PUERTO_DESCARGA_PARTES);

            //ENVIAMOS EL GRUPO Y EL USUARIO SELECCIONADO
            ObjectOutputStream outObject = new ObjectOutputStream(conexion.getOutputStream());
            outObject.writeObject(grupo);
            outObject.writeObject(ususario);

            //ENVIAMOS EL PORCENTAJE SELECCIONADO
            DataOutputStream onData = new DataOutputStream(conexion.getOutputStream());
            onData.writeInt(porcentaje);

            //COGEMOS DEL STREAM EL TAMAÑO DE LAS DESCARGA
            DataInputStream inSize = new DataInputStream(conexion.getInputStream());
            sizeDescarga =  inSize.readLong();
            //LEEMOS EL NOMBRE DEL FICHERO
            nomFile = inSize.readUTF();

            //REALIZAMOS DESCARGA
            realizarDescarga(nomFile);

            outObject.close();
            onData.close();
            conexion.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    //MÉTODO QUE REALIZA LA DESCARGA DE LAS PARTES
    private void realizarDescarga(String nomFile){

        //COMPROBAMOS EL ESTADO DE LA MEMORIA EXTERNA - SD
        String estado = Environment.getExternalStorageState();

        if(estado.equals(Environment.MEDIA_MOUNTED)){

            DataInputStream inData;
            FileOutputStream escritura;
            byte[] buffer;

            //INICIAMOS STREAM
            try {
                inData = new DataInputStream(conexion.getInputStream());
                buffer = new byte[1024];

                filePartes = new File(CARPETA_DESCARGAS.getAbsolutePath() + "/" + nomFile);

                //CREAMOS STREAM
                escritura =  new FileOutputStream(filePartes);

                //CALCULAMOS PORCENTAJE BARRA DE PROGRESO
                float progreso = 0;
                float progresoAnterior = 0;
                float progresoParcial;

                //EMPEZAMOS A COPIAR DEL STREAM
                //CALCULAMOS EL TIEMPO
                long timeI = System.currentTimeMillis();
                int len;

                while((len = inData.read(buffer)) > 0){
                    escritura.write(buffer,0,len);

                    progresoParcial = (len*100f)/sizeDescarga;
                    progreso = progreso + progresoParcial;

                    if((int)progreso != (int)progresoAnterior)
                        publishProgress(progreso);

                    progresoAnterior = progreso;
                }
                long timeF = System.currentTimeMillis();
                timeDescarga = timeF - timeI;
                escritura.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            cg.mostrarPanelError("No se puede leer en la tarjeta SD");

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        pd.cancel();

        float sizeMegas = (sizeDescarga/1024f)/1024f;
        float timeSeconds = (timeDescarga/1000f);
        float sizeMegabits = sizeMegas*8;

        cg.mostrarPanelInfo("Se descargaron las partes seleccionadas en la carpeta DESCARGAS de su dispositivo.\n" +
                "\n\nVelocidad de descarga: " +  String.format("%.2f",(sizeMegabits/timeSeconds)) + " mb/s");

        //EJECUTAMOS TAREA PARA REPINTAR LA TABLA DE CLIENTES DEL GRUPO
        Actualizar_Grupos ag = new Actualizar_Grupos(servidor,ususario,cg);
        ag.execute();

    }

    @Override
    protected void onProgressUpdate(Object[] values) {

        float progreso = (float)values[0];
        int p = (int)progreso;
        float sizeMegas = (sizeDescarga/1024f)/1024f;

        if(p == -1){
            pd.setMessage("Recibiendo: " + nomFile + ", \nTamaño: " + String.format("%.2f",sizeMegas) + " MB");
            pd.show();
        }
        else{
            pd.setMessage("Recibiendo: " + nomFile + ", \nTamaño: " + String.format("%.2f",sizeMegas) + " MB");
            pd.setProgress(p);

        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //CONFIGURAMOS EL PROGRESS DIALOG
        pd =  new ProgressDialog(cg);
        pd.setCancelable(false);
        pd.setTitle("Descargando ficheros...");
        pd.setMessage("Descargando");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        pd.setProgress(0);
        pd.show();
    }
}
