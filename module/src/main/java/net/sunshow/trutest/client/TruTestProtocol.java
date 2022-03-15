package net.sunshow.trutest.client;

import android.util.Log;

import com.psp.bluetoothlibrary.BluetoothListener;
import com.psp.bluetoothlibrary.Connection;

public class TruTestProtocol {

    private final static String TAG = TruTestProtocol.class.getName();

    // Clears All Session Files. (Empty default session file is created)
    private final static String CMD_CLEAR_ALL_SESSION_FILES = "{CL}";

    // Clears life data or currently selected session.
    private final static String CMD_CLEAR_CURRENT_SESSION_DATA = "{FC}";

    private final Connection connection;

    public TruTestProtocol(Connection connection) {
        this.connection = connection;
    }

    // Receive listener
    public final static BluetoothListener.onReceiveListener receiveListener = new BluetoothListener.onReceiveListener() {
        @Override
        public void onReceived(String receivedData) {
            Log.e(TAG, receivedData);
        }
    };
}
