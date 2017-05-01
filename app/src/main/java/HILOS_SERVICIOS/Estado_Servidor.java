package HILOS_SERVICIOS;

import android.os.AsyncTask;
import android.widget.Button;

import com.example.atack08.groupload_app.OperacionesInternet;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import BEANS.Servidor;



public class Estado_Servidor extends AsyncTask<Servidor, Integer,Boolean>{

    private final int PUERTO_ESTADO_SERVIDOR = 1302;
    private OperacionesInternet oi;

    public Estado_Servidor(OperacionesInternet oi) {
        this.oi = oi;
    }

    @Override
    protected Boolean doInBackground(Servidor... servidores) {

        Servidor s = servidores[0];

        Boolean isOnline = false;

        try {
            Socket socket = new Socket(s.getIpServidor(), PUERTO_ESTADO_SERVIDOR);
            isOnline = socket.isConnected();
            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return isOnline;
    }

    @Override
    protected void onPostExecute(Boolean isOnline) {

        if(isOnline) {
            oi.mostrarPanelInfo("El servidor está ON-LINE.");
            //ACTIVAMOS BOTÓN CONECTAR
            oi.getBotonConectar().setEnabled(true);

        }
        else
            oi.mostrarPanelError("El servidor NO está disponible o no tienes conexión a internet");



    }
}
