package net.sunshow.trutest.client;

import android.util.Log;

import com.psp.bluetoothlibrary.BluetoothListener;
import com.psp.bluetoothlibrary.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TruTestProtocol {

    private final static String TAG = TruTestProtocol.class.getName();

    private final Connection connection;

    private LinkedBlockingQueue<TruTestCommand> commandQueue;

    private TruTestCommand command;

    private onCommandCompletedListener<String> listener;

    private StringBuilder received;

    public TruTestProtocol(Connection connection) {
        this.connection = connection;
    }

    // Receive listener
    public BluetoothListener.onReceiveListener receiveListener = new BluetoothListener.onReceiveListener() {
        @Override
        public void onReceived(String receivedData) {
            final TruTestCommand current = command;
            final boolean hasNext = !commandQueue.isEmpty();
            Log.e(TAG, "onReceived: " + receivedData);
            // received an ack
            if (TruTestError.OK.getCode().equals(receivedData)) {
                if (!hasNext && listener != null) {
                    listener.onCompleted(current, received.toString());
                }
                completeAndNext();
                return;
            }
            if (received != null) {
                received.append(receivedData);

                char c = received.charAt(received.length() - 1);
                if (c == ')') {
                    // error occurred
                    String code = received.toString();
                    for (TruTestError error : TruTestError.values()) {
                        if (error.getCode().equals(code)) {
                            if (!hasNext && listener != null) {
                                listener.onFailed(current, error);
                            }
                            completeAndNext();
                            return;
                        }
                    }
                    if (!hasNext && listener != null) {
                        listener.onFailed(current, TruTestError.Unknown);
                    }
                    completeAndNext();
                    return;
                }

                if (c == ']') {
                    // check stop delimiter
                    if (received.charAt(received.length() - 2) == '[') {
                        // read completed
                        if (!hasNext && listener != null) {
                            // remove last [] and return
                            listener.onCompleted(current, received.substring(0, received.length() - 2));
                        }
                        completeAndNext();
                        return;
                    }
                    // more data should request, execute current command again
                    if (!execute()) {
                        if (listener != null) {
                            listener.onFailed(current, TruTestError.CommandCouldNotBeSent);
                        }
                        reset();
                        return;
                    }
                } else {
                    // has not completed receiving current data, did nothing, more data will received later
                }
            }
        }
    };

    private synchronized void reset() {
        this.commandQueue = null;
        this.command = null;
        this.listener = null;
        this.received = null;
    }

    private synchronized boolean completeAndNext() {
        TruTestCommand next = this.commandQueue.poll();
        if (next == null) {
            reset();
            return false;
        } else {
            this.command = next;
            if (!execute()) {
                if (listener != null) {
                    listener.onFailed(command, TruTestError.CommandCouldNotBeSent);
                }
                reset();
                return false;
            }
            return true;
        }
    }

    private synchronized boolean execute() {
        return connection.send(command.getCode());
    }

    private synchronized boolean execute(List<TruTestCommand> commandList, onCommandCompletedListener<String> listener) {
        if (this.commandQueue != null) {
            Log.e(TAG, "Another command is executing or waiting response.");
            return false;
        }
        this.commandQueue = new LinkedBlockingQueue<>(commandList);
        this.listener = listener;
        this.received = new StringBuilder();

        this.command = this.commandQueue.poll();
        return execute();
    }

    private synchronized boolean execute(TruTestCommand command, onCommandCompletedListener<String> listener) {
        List<TruTestCommand> commandList = new ArrayList<>();
        commandList.add(command);
        return execute(commandList, listener);
    }

    public boolean setAcknowledgeOn(onCommandCompletedListener<String> listener) {
        return execute(TruTestCommand.SetAcknowledgeOn, listener);
    }

    public boolean clearAllSessionFiles(onCommandCompletedListener<String> listener) {
        return execute(TruTestCommand.ClearAllSessionFiles, listener);
    }

    public boolean resetCurrentSessionData(onCommandCompletedListener<String> listener) {
        return execute(TruTestCommand.ResetCurrentSessionData, listener);
    }

    public boolean downloadCurrentSessionData(onCommandCompletedListener<String> listener) {
        List<TruTestCommand> commandList = new ArrayList<>();
        commandList.add(TruTestCommand.OperateOnSessionData);
        commandList.add(TruTestCommand.GetSessionRecord);
        return execute(commandList, listener);
    }

    public interface onCommandCompletedListener<T> {

        void onCompleted(TruTestCommand command, T data);

        void onFailed(TruTestCommand command, TruTestError error);

    }

}
