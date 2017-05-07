package HILOS_SERVICIOS;

import android.os.AsyncTask;

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

    public Subida_Recurso(Servidor servidor, Grupo grupo, File torrentSeleccionado) {
        this.servidor = servidor;
        this.grupo = grupo;
        this.torrentSeleccionado = torrentSeleccionado;
    }

    @Override
    protected Object doInBackground(Object[] params) {


        try {
            //INICIAMOS LA CONEXIÓN
            conexion = new Socket(servidor.getIpServidor(), PUERTO_SUBIDA_RECURSO);

            //MANDAMOS EL NOMBRE DEL GRUPO  AL SERVIDOR
            DataOutputStream outObject = new DataOutputStream(conexion.getOutputStream());
            outObject.writeUTF(grupo.getAlias());

            //CREAMOS BUFFER PARA LEER STREAM DEL FICHERO
            byte[] buffer = new byte[1024];
            FileInputStream lectura = new FileInputStream(torrentSeleccionado);

            //ABRIMOS STREAM RED
            DataOutputStream out = new DataOutputStream(conexion.getOutputStream());

            int len;
            while((len = lectura.read(buffer)) > 0){
                out.write(buffer,0,len);
            }

            //CERRAMOS STREAM FICHERO
            lectura.close();

            conexion.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);


    }
}

