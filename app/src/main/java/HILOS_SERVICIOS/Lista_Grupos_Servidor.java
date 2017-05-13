package HILOS_SERVICIOS;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.example.atack08.groupload_app.OperacionesInternet;
import com.example.atack08.groupload_app.UnirseGrupo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;

/**
 * Created by atack08 on 1/5/17.
 */

public class Lista_Grupos_Servidor extends AsyncTask {

    private Usuario usuario;
    private Servidor servidor;
    private final static int PUERTO_GRUPOS_SERVIDOR = 1200;
    private ObjectInputStream in;
    private UnirseGrupo ug;
    private ArrayList<Grupo> listaG;


    public Lista_Grupos_Servidor(Usuario usuario, Servidor servidor, UnirseGrupo ug) {
        this.usuario = usuario;
        this.servidor = servidor;
        this.ug = ug;

    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {


            Socket socket = new Socket(servidor.getIpServidor(),PUERTO_GRUPOS_SERVIDOR);

            in = new ObjectInputStream(socket.getInputStream());

            listaG = (ArrayList<Grupo>) in.readObject();

            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //SI HUBIESE UN GRUPO SELECCIONADO ESTA TAREA INICIARÍA EL PROCESO DE UNIRSE A UN GRUPO
    //LLAMANDO A UN MÉTODO DE LA CLASE
    @Override
    protected void onPostExecute(Object o) {

        ug.configurarVistaLista(this.listaG);

        //SI HUBIESE GRUPO SELECCIONADO ACTUALIZARÍA DICHO GRUPO CON LA NUEVA INFORMACIÓN DEL SERVIDOR
        if(ug.getGrupoSeleccionado() != null){

            Grupo gS = ug.getGrupoSeleccionado();

            if(listaG.contains(gS)){
                Grupo grupoActualizado = listaG.get(listaG.indexOf(gS));

                if(gS.getParticipacion() != grupoActualizado.getParticipacion()) {
                    ug.setGrupoSeleccionado(grupoActualizado);
                    ug.actualizarSpinnerPorcentaje();
                    ug.mostrarPanelError("El grupo ha cambiado en el servidor, vuelva a seleccionar % de descarga.");
                }
                else
                    ug.conectarseAGrupo();

            }
            else{
                //SI EL GRUPO HA SIDO ELIMINADO DEL SERVIDOR
                ug.mostrarPanelError("El grupo ha sido eliminado del servidor.");
                ug.resetearSeleccion();
            }



        }
    }
}
