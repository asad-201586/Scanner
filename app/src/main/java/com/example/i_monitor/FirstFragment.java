package com.example.i_monitor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class FirstFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FirstFragment() {

    }

    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    Button regester;
    TextInputLayout nameEdt, publicNameEdt, mobileNumberEdt, emailEdt, addressEdt, cityEdt, stateEdt;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        regester = view.findViewById(R.id.register_btn);
        nameEdt = view.findViewById(R.id.name_edt);
        publicNameEdt = view.findViewById(R.id.public_name_edt);
        mobileNumberEdt = view.findViewById(R.id.mobile_number_edt);
        emailEdt = view.findViewById(R.id.email_edt);
        addressEdt = view.findViewById(R.id.address_edt);
        cityEdt = view.findViewById(R.id.city_edt);
        stateEdt = view.findViewById(R.id.state_edt);

        regester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondModel secondModel = validateForm();
                if (secondModel != null)
                    gotoSecondFragment(secondModel);
            }
        });
    }

    private SecondModel validateForm() {
        String name, publicName, mobileNumber, email, address, city, state;

        if(nameEdt.getEditText().getText().toString().trim().isEmpty()){
            nameEdt.setError(getResources().getString(R.string.enter_name));
            return null;
        }
        else{
            name = nameEdt.getEditText().getText().toString();
            nameEdt.setErrorEnabled(false);
        }

        if(publicNameEdt.getEditText().getText().toString().trim().isEmpty()){
            publicName = "";
        }
        else{
            publicName = publicNameEdt.getEditText().getText().toString();
        }

        if(mobileNumberEdt.getEditText().getText().toString().trim().isEmpty()){
            mobileNumberEdt.setError(getResources().getString(R.string.enter_mobile_number));
            return null;
        }
        else{
            mobileNumber = mobileNumberEdt.getEditText().getText().toString();
            mobileNumberEdt.setErrorEnabled(false);
        }

        if(emailEdt.getEditText().getText().toString().trim().isEmpty()){
            email = "";
        }
        else{
            email = emailEdt.getEditText().getText().toString();
        }

        if(addressEdt.getEditText().getText().toString().trim().isEmpty()){
            address = "";
        }
        else{
            address = addressEdt.getEditText().getText().toString();
        }

        if(cityEdt.getEditText().getText().toString().trim().isEmpty()){
            city = "";
        }
        else{
            city = cityEdt.getEditText().getText().toString();
        }

        if(stateEdt.getEditText().getText().toString().trim().isEmpty()){
            state = "";
        }
        else{
            state = stateEdt.getEditText().getText().toString();
        }

        return new SecondModel(name, publicName, mobileNumber, email, address, city, state);
    }

    private void gotoSecondFragment(SecondModel secondModel) {
        String data = new Gson().toJson(secondModel);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SecondFragment fragment = SecondFragment.newInstance(data);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}