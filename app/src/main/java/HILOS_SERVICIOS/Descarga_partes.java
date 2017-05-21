package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
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

/**
 * Created by atack08 on 13/5/17.
 */

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
            String nomFile = inSize.readUTF();

            System.out.println(nomFile);

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
                float porcProgreso = (1024f*100f) / sizeDescarga;
                float progreso = 0;

                //EMPEZAMOS A COPIAR DEL STREAM
                int len;
                while((len = inData.read(buffer)) > 0){
                    escritura.write(buffer,0,len);

                    progreso = progreso + porcProgreso;
                    this.publishProgress(progreso);
                }

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
        cg.mostrarPanelInfo("Se descargaron las partes seleccionadas en la carpeta DESCARGAS de su dispositivo.");

        //EJECUTAMOS TAREA PARA REPINTAR LA TABLA DE CLIENTES DEL GRUPO
        Actualizar_Grupos ag = new Actualizar_Grupos(servidor,ususario,cg);
        ag.execute();

    }

    @Override
    protected void onProgressUpdate(Object[] values) {

        int progreso = (int)((float)values[0]);
        pd.setProgress(progreso);

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
