package HILOS_SERVICIOS;

import android.os.AsyncTask;

import com.example.atack08.groupload_app.OperacionesInternet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import BEANS.Servidor;
import BEANS.Usuario;


public class Login_Servidor extends AsyncTask<Usuario,Integer,Boolean> {

    private final static int PUERTO_LOGIN_SERVIDOR = 1063;
    private Servidor server;
    private ObjectOutputStream out;
    private DataInputStream in;
    private OperacionesInternet oi;
    private Usuario user;

    public Login_Servidor(Servidor server, OperacionesInternet oi) {
        this.server = server;
        this.oi = oi;
    }

    @Override
    protected Boolean doInBackground(Usuario... params) {

        user = params[0];
        boolean login = false;

        try {
            Socket socket = new Socket(server.getIpServidor(), PUERTO_LOGIN_SERVIDOR);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            out.writeObject(user);

            login = in.readBoolean();

            out.close();
            in.close();
            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return login;
    }


    @Override
    protected void onPostExecute(Boolean login) {

        if(login)
            oi.getActivity().cambiarActividadLogin(server,user);
        else
            oi.mostrarPanelError("Usuario y/o Contraseña incorrectos.");

    }
}
