package HILOS_SERVICIOS;

import android.os.AsyncTask;

import com.example.atack08.groupload_app.ConectarGrupo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import BEANS.Cliente;
import BEANS.Grupo;
import BEANS.Usuario;

/**
 * Created by atack08 on 6/5/17.
 */

public class Conexion_Cliente_Grupo extends AsyncTask<Object,Grupo, Object> {

    private Socket conexion;
    private final int PUERTO_CONEXION_CLIENTE = 1507;
    private Cliente cliente;
    private ConectarGrupo cg;

    private ObjectInputStream inObject;
    private ObjectOutputStream outObject;


    public Conexion_Cliente_Grupo(ConectarGrupo cg) {

        this.cg = cg;

    }

    @Override
    protected Object doInBackground(Object[] params) {

        //CREAMOS LA CONEXIÓN AL SERVIDOR
        try {
            conexion = new Socket(cg.getServidor().getIpServidor(), PUERTO_CONEXION_CLIENTE);

            //CREAMOS EL CLIENTE Y LO UNIMOS AL GRUPO QUE LE PASAMOS AL SERVIDOR
            cliente = new Cliente(cg.getUsuario(),conexion.getInetAddress().toString(), cg.getPorcentajeDescarga());

            //INSTANCIAMOS LOS STREAMS
            outObject = new ObjectOutputStream(conexion.getOutputStream());


            //PASAMOS AL SERVIDOR EL GRUPO Y EL CLIENTE A UNIR
            outObject.writeObject(cg.getGrupoConectado());
            outObject.writeObject(cliente);


            //outObject.writeObject(cliente);
            outObject.close();

            inObject = new ObjectInputStream(conexion.getInputStream());

            //MIENTRAS DESCONECTAR SEA FALSE
            while(!cg.isDesconectar()){

                //ACTUALIZAMOS EL GRUPO DESDE EL SERVIDOR
                try {
                    Grupo grupoActualizado = (Grupo) inObject.readObject();


                    publishProgress(grupoActualizado);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }



        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Grupo... values) {

        //ACTUALIZAMOS EL GRUPO
        Grupo grupoActualizado = values[0];
        cg.setGrupoConectado(grupoActualizado);

        //PINTAMOS EN LA ACTIVITY LA TABLA
        cg.construirTablaGrupo();
    }
}
