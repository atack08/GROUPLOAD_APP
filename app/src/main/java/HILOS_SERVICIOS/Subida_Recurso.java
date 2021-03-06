package HILOS_SERVICIOS;

import android.os.AsyncTask;

import com.example.atack08.groupload_app.CrearGrupo;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import BEANS.Grupo;
import BEANS.Servidor;


/**
 * Created by atack08 on 7/5/17.
 */

public class Subida_Recurso extends AsyncTask {

    private Servidor servidor;
    private Grupo grupo;
    private File torrentSeleccionado;
    private Socket conexion;
    private final int PUERTO_SUBIDA_RECURSO = 1453;
    private CrearGrupo cg;

    public Subida_Recurso(Servidor servidor, Grupo grupo, File torrentSeleccionado, CrearGrupo cg) {
        this.servidor = servidor;
        this.grupo = grupo;
        this.torrentSeleccionado = torrentSeleccionado;
        this.cg = cg;
    }

    @Override
    protected Object doInBackground(Object[] params) {


        try {
            //INICIAMOS LA CONEXIÓN
            conexion = new Socket(servidor.getIpServidor(), PUERTO_SUBIDA_RECURSO);

            //MANDAMOS EL NOMBRE DEL GRUPO Y EL RECURSO SELECCIONADO AL SERVIDOR
            DataOutputStream outData = new DataOutputStream(conexion.getOutputStream());
            outData.writeUTF(grupo.getAlias());
            outData.writeUTF(torrentSeleccionado.getName());

            //CREAMOS BUFFER PARA LEER STREAM DEL FICHERO
            byte[] buffer = new byte[1024];
            FileInputStream lectura = new FileInputStream(torrentSeleccionado);
            int len;

            while((len = lectura.read(buffer)) > 0){
                outData.write(buffer,0,len);
            }

            //CERRAMOS STREAM FICHERO
            lectura.close();
            outData.close();
            conexion.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        cg.mostrarPanelInfo("Se subió correctamente el fichero " + torrentSeleccionado.getName() + " al servidor.");

    }
}

