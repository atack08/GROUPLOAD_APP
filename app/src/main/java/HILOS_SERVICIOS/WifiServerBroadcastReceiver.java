package HILOS_SERVICIOS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.atack08.groupload_app.P2PServer;

/**
 * Created by atack08 on 19/5/17.
 */

public class WifiServerBroadcastReceiver extends BroadcastReceiver{

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private P2PServer p2PServer;

    public WifiServerBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, P2PServer activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.p2PServer = activity;

        activity.mostrarPanelInfo("Server Broadcast Receiver created");

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                p2PServer.mostrarPanelInfo("Wifi Direct is enabled");
            } else {
                p2PServer.mostrarPanelError("Wifi Direct is not enabled");
            }

        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkState.isConnected()) {
                p2PServer.mostrarPanelInfo("Connection Status: Connected");
            }
            else {
                p2PServer.mostrarPanelInfo("Connection Status: Disconnected");
                manager.cancelConnect(channel, null);

            }

        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
