package net.sunshow.trutest.client;

import android.util.Log;

import com.psp.bluetoothlibrary.BluetoothListener;
import com.psp.bluetoothlibrary.Connection;

public class TruTestProtocol {

    private final static String TAG = TruTestProtocol.class.getName();

    private final Connection connection;

    private TruTestCommand command;

    private onCommandCompletedListener listener;

    private StringBuilder received;

    public TruTestProtocol(Connection connection) {
        this.connection = connection;
    }

    // Receive listener
    public BluetoothListener.onReceiveListener receiveListener = new BluetoothListener.onReceiveListener() {
        @Override
        public void onReceived(String receivedData) {
            Log.e(TAG, "onReceived: " + receivedData);
            // received an ack
            if (TruTestError.OK.getCode().equals(receivedData)) {
                if (listener != null) {
                    listener.onCompleted(command, received.toString());
                }
                complete();
            }
            if (received != null) {
                received.append(receivedData);

                char c = received.charAt(received.length() - 1);
                if (c == ')') {
                    // error occurred
                    String code = received.toString();
                    for (TruTestError error : TruTestError.values()) {
                        if (error.getCode().equals(code)) {
                            if (listener != null) {
                                listener.onFailed(command, error);
                            }
                            complete();
                            return;
                        }
                    }
                    if (listener != null) {
                        listener.onFailed(command, TruTestError.Unknown);
                    }
                    complete();
                    return;
                }

                if (c == ']') {
                    // check stop delimiter
                    if (received.charAt(received.length() - 2) == '[') {
                        // read completed
                        if (listener != null) {
                            listener.onCompleted(command, received.toString());
                        }
                        complete();
                        return;
                    }
                    // more data should request, execute command again
                    execute();
                } else {
                    // has not completed receiving current data, did nothing, more data will received later
                }
            }
        }
    };

    private synchronized void complete() {
        this.command = null;
        this.listener = null;
        this.received = null;
    }

    private synchronized boolean execute() {
        return connection.send(command.getCode());
    }

    private synchronized boolean execute(TruTestCommand command, onCommandCompletedListener listener) {
        if (this.command != null) {
            Log.e(TAG, "Another command is executing or waiting response: " + this.command);
            return false;
        }
        this.command = command;
        this.listener = listener;
        this.received = new StringBuilder();
        return connection.send(command.getCode());
    }

    public boolean setAcknowledgeOn(onCommandCompletedListener listener) {
        return execute(TruTestCommand.SetAcknowledgeOn, listener);
    }

    public boolean clearAllSessionFiles(onCommandCompletedListener listener) {
        return execute(TruTestCommand.ClearAllSessionFiles, listener);
    }

    public boolean resetCurrentSessionData(onCommandCompletedListener listener) {
        return execute(TruTestCommand.ResetCurrentSessionData, listener);
    }

    public interface onCommandCompletedListener {

        void onCompleted(TruTestCommand command, Object data);

        void onFailed(TruTestCommand command, TruTestError error);

    }

}
