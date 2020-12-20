package com.example.i_monitor;

public class Data {
    private FirstModel firstModel;
    private String usbScanData;

    public Data() {

    }

    public Data(FirstModel firstModel, String usbScanData) {
        this.firstModel = firstModel;
        this.usbScanData = usbScanData;
    }

    public FirstModel getFirstModel() {
        return firstModel;
    }

    public void setFirstModel(FirstModel firstModel) {
        this.firstModel = firstModel;
    }

    public String getUsbScanData() {
        return usbScanData;
    }

    public void setUsbScanData(String usbScanData) {
        this.usbScanData = usbScanData;
    }

}
