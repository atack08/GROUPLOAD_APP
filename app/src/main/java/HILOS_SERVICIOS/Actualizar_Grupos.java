package HILOS_SERVICIOS;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.atack08.groupload_app.ConectarGrupo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;

/**
 * Created by atack08 on 14/5/17.
 */

public class Actualizar_Grupos extends AsyncTask{

    private Servidor servidor;
    private Usuario usuario;
    private ConectarGrupo cg;
    private ArrayList<Grupo> listaG;

    private final int PUERTO_ACTUALIZACION_GRUPOS = 1200;

    public Actualizar_Grupos(Servidor servidor, Usuario usuario, ConectarGrupo cg) {
        this.servidor = servidor;
        this.usuario = usuario;
        this.cg = cg;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            Socket conexion = new Socket(servidor.getIpServidor(), PUERTO_ACTUALIZACION_GRUPOS);

            ObjectInputStream  inObject = new ObjectInputStream(conexion.getInputStream());

            listaG = (ArrayList<Grupo>) inObject.readObject();

            inObject.close();
            conexion.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        //RESCATAMOS DE LA LISTA EL GRUPO SELECCIONADO
        if(listaG.contains(cg.getGrupoConectado())){
            cg.setGrupoConectado(listaG.get(listaG.indexOf(cg.getGrupoConectado())));
        }

        //REPINTAMOS LA TABLA
        cg.construirTablaGrupo();
    }
}
