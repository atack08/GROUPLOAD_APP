package HILOS_SERVICIOS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.atack08.groupload_app.P2PWifiDirect;

import java.util.ArrayList;

/**
 * Created by atack08 on 19/5/17.
 */

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private P2PWifiDirect activity;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, P2PWifiDirect activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;

        activity.mostrarPanelInfo("Client Broadcast receiver creado.");

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.mostrarPanelInfo("Wifi Direct está disponible.");
            }
            else {
                activity.mostrarPanelError("Wifi Direct no está disponible.");
            }

        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            //This broadcast is sent when status of in range peers changes. Attempt to get current list of peers.

            activity.mostrarPanelInfo("DISPOSITIVOS ENCONTRADOS...");

            manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {

                public void onPeersAvailable(WifiP2pDeviceList peers) {

                    ArrayList<WifiP2pDevice> listaD = new ArrayList<>();

                    for(WifiP2pDevice device: peers.getDeviceList()){
                        listaD.add(device);
                    }

                    activity.displayPeers(listaD);

                }
            });

        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pInfo wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

            if(networkState.isConnected()) {

                activity.setNetworkToReadyState(wifiInfo, device);
                activity.mostrarPanelInfo("Estado de la conexión: CONECTADO");
                activity.activarBotonoes();

            }
            else {

                activity.mostrarPanelInfo("Estado de la conexión: DESCONECTADO");
                manager.cancelConnect(channel, null);
                activity.desactivarBotones();

            }

        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
