package HILOS_SERVICIOS;

import java.io.IOException;
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
            Socket s = new Socket("89.131.153.38",1302);
            s.close();
        } catch (SocketException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
