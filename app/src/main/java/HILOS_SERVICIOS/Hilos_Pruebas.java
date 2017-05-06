package HILOS_SERVICIOS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by atack08 on 29/4/17.
 */

public class Hilos_Pruebas extends Thread{


    @Override
    public void run() {

        try {
            ServerSocket server = new ServerSocket(10556,1000,InetAddress.getByName("10.88.213.198"));

            System.out.println("SERVIDOR ESCUCHANDO EN : " + server.getLocalSocketAddress().toString());

            Socket cliente = server.accept();

            System.out.println("CLIENTE CONECTADO, IP DATOS: " + cliente.getLocalAddress().toString());

            cliente.close();
            server.close();

        } catch (SocketException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
