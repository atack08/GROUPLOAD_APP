package HILOS_SERVICIOS;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

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

    @Override
    protected void onPostExecute(Object o) {

        ug.configurarVistaLista(this.listaG);
    }
}
