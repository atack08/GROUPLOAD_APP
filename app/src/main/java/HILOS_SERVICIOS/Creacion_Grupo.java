package HILOS_SERVICIOS;

import android.os.AsyncTask;

import com.example.atack08.groupload_app.CrearGrupo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import BEANS.Grupo;
import BEANS.Servidor;

/**
 * Created by atack08 on 7/5/17.
 */

public class Creacion_Grupo extends AsyncTask {

    private final int PUERTO_CREACION_GRUPO = 1608;
    private Socket conexion;
    private Servidor servidor;
    private Grupo nuevoGrupo;
    private File recurso;
    private CrearGrupo cg;
    private boolean resultCreacion;

    public Creacion_Grupo(Servidor servidor, Grupo nuevoGrupo, File recurso, CrearGrupo cg) {
        this.servidor = servidor;
        this.nuevoGrupo = nuevoGrupo;
        this.recurso = recurso;
        this.cg = cg;
    }

    @Override
    protected Object doInBackground(Object[] params) {


        try {
            //INICIAMOS LA CONEXIÓN
            conexion = new Socket(servidor.getIpServidor(), PUERTO_CREACION_GRUPO);

            //MANDAMOS EL GRUPO NUEVO AL SERVIDOR
            ObjectOutputStream outObject = new ObjectOutputStream(conexion.getOutputStream());
            outObject.writeObject(nuevoGrupo);

            System.out.println("GRUPO ENVIADO...");


            //RECIBIMOS CONFIRMACIÓN DE CREACION DE GRUPO
            DataInputStream inData = new DataInputStream(conexion.getInputStream());
            resultCreacion = inData.readBoolean();

            conexion.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if(resultCreacion) {
            cg.mostrarPanelInfo("El grupo " + nuevoGrupo.getAlias() + " se ha creado satisfactoriamente.");
            cg.getBotonSubirRecurso().setEnabled(true);
        }
        else {
            cg.mostrarPanelError("No se pudo crear el grupo, elija un nombre diferente.");
            cg.getBotonSubirRecurso().setEnabled(false);
        }
    }
}
