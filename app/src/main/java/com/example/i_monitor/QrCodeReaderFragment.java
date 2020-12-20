package com.example.i_monitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QrCodeReaderFragment extends Fragment {

    DecoratedBarcodeView qrCodeView;
    BarcodeCallback barcodeCallback;

    public static QrCodeReaderFragment newInstance(BarcodeCallback callback) {
        QrCodeReaderFragment qrCodeReaderFragment = new QrCodeReaderFragment();
        qrCodeReaderFragment.barcodeCallback = callback;
        return qrCodeReaderFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qr_code_reader, container, false);
        qrCodeView = rootView.findViewById(R.id.qr_code_view);
        if (barcodeCallback != null) {
            qrCodeView.decodeSingle(barcodeCallback);
        }
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (qrCodeView != null) {
            if (isVisibleToUser) {
                qrCodeView.resume();
            } else {
                qrCodeView.pauseAndWait();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        qrCodeView.resume();
    }
}