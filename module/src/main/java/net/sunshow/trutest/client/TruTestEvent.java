package net.sunshow.trutest.client;

public abstract class TruTestEvent {

    public final static String BlueToothDeviceDetected = "TruTest_BlueToothDeviceDetected";

    public final static String BlueToothDevicePairCompleted = "TruTest_BlueToothDevicePairCompleted";

    public final static String DeviceConnecting = "TruTest_DeviceConnecting";

    public final static String DeviceConnected = "TruTest_DeviceConnected";

    public final static String DeviceDisconnected = "TruTest_DeviceDisconnected";

    public final static String DeviceConnectionFailed = "TruTest_DeviceConnectionFailed";

    public final static String CommandExecutionCompleted = "TruTest_CommandExecutionCompleted";
}
