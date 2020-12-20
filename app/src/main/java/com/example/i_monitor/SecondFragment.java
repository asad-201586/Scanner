package com.example.i_monitor;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.ResultPoint;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SecondFragment extends Fragment implements View.OnClickListener {

    private static final String SECOND_MODEL = "MODEL";

    //contain data from registration page;
    private String secondModel = "";

    public SecondFragment() {

    }

    public static SecondFragment newInstance(String data) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(SECOND_MODEL, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            secondModel = getArguments().getString(SECOND_MODEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    Button readUsbBtn, scanQrBtn, submitBtn;
    TextView usbDataTv, scanDataTv;
    FrameLayout cameraContainer;
    QrCodeReaderFragment qrCodeReaderFragment;
    String qrCodeScanData ="", usbScanData ="";
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //firebase setup
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("registration");

        readUsbBtn = view.findViewById(R.id.read_usb_btn);
        scanQrBtn = view.findViewById(R.id.scan_qr_btn);
        submitBtn = view.findViewById(R.id.submit_btn);
        usbDataTv = view.findViewById(R.id.usb_data_tv);
        scanDataTv = view.findViewById(R.id.scan_data_tv);
        cameraContainer = view.findViewById(R.id.camera_container);

        readUsbBtn.setOnClickListener(this);
        scanQrBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        qrCodeReaderFragment = QrCodeReaderFragment.newInstance(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                qrCodeScanData = result.getText();
                scanDataTv.setText(result.getText());
                closeQrCodeReaderFragment();
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_usb_btn) {
            //read usb data
            try {
                readDataFromUsbDevice();
            } catch (IOException e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } else if (v.getId() == R.id.scan_qr_btn) {
            //scan qr code
            if (scanQrBtn.getText().equals(getResources().getString(R.string.scan_qr_code))) {
                openQrCodeReaderFragment();
            } else {
                closeQrCodeReaderFragment();
            }


        } else if (v.getId() == R.id.submit_btn) {
            //submit the qrCodeScanData and usbScanData to firebase
            if(qrCodeScanData.isEmpty()){
                Toast.makeText(getContext(), getResources().getString(R.string.not_data_found), Toast.LENGTH_SHORT)
                        .show();
            } else {
                uploadDataToFirebase(qrCodeScanData, usbScanData);
            }

        } else {

        }
    }

    private void openQrCodeReaderFragment() {
        scanQrBtn.setText(getResources().getString(R.string.close_qr_code));
        cameraContainer.setVisibility(View.VISIBLE);
        scanDataTv.setVisibility(View.GONE);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.camera_container, qrCodeReaderFragment);
        fragmentTransaction.commit();
    }

    private void closeQrCodeReaderFragment() {
        scanQrBtn.setText(getResources().getString(R.string.scan_qr_code));
        cameraContainer.setVisibility(View.GONE);
        scanDataTv.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(qrCodeReaderFragment);
        fragmentTransaction.commit();
    }

    private void readDataFromUsbDevice() throws IOException {
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            Toast.makeText(getContext(),"No Driver Found", Toast.LENGTH_SHORT).show();
            Log.i("USB_DATA","No Driver Found");
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            Toast.makeText(getContext(),"Connection Null", Toast.LENGTH_SHORT).show();
            Log.i("USB_DATA","Connection Null");
            return;
        }

        // Most devices have just one port (port 0)
        try (UsbSerialPort port = driver.getPorts().get(0)) {
            port.open(connection);
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            byte[] buffer = new byte[10000];
            long len = port.read(buffer, 1000);

            Toast.makeText(getContext(), "Buffer Read Length" + len, Toast.LENGTH_SHORT).show();

            usbScanData = new String(buffer, StandardCharsets.UTF_8);
            usbDataTv.setText(usbScanData);
            Log.i("USB_DATA", "data read: " + usbScanData);
        } catch (IOException e) {
            // Deal with error.
            Toast.makeText(getContext(), "Error on data read " + e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            Toast.makeText(getContext(), "Connection Closed", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDataToFirebase(String qrCodeData, String usbData) {
        FirstModel firstModel = new Gson().fromJson(qrCodeData, FirstModel.class);
        Data data = new Data(firstModel, usbData);
        myRef.push().setValue( data, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null) {
                    Toast.makeText(getContext(), getResources().getString(R.string.data_upload_successful), Toast.LENGTH_SHORT)
                            .show();
                    resetData();
                }
                else {
                    Toast.makeText(getContext(), getResources().getString(R.string.try_again_to_submit), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void resetData() {
        qrCodeScanData = "";
        usbScanData = "";
        usbDataTv.setText(getResources().getString(R.string.connect_usb));
        scanDataTv.setText(getResources().getString(R.string.scan_qr_code_using_camera));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.reset, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reset) {
            Toast.makeText(getContext(), getResources().getString(R.string.reset), Toast.LENGTH_SHORT)
                    .show();
            resetData();
        }
        return super.onOptionsItemSelected(item);
    }

}