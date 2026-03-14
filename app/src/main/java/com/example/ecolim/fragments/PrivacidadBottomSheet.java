package com.example.ecolim.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecolim.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PrivacidadBottomSheet extends BottomSheetDialogFragment {

    public static PrivacidadBottomSheet newInstance() {
        return new PrivacidadBottomSheet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacidad_bottomsheet,
                container, false);
        view.findViewById(R.id.btn_aceptar_privacidad)
                .setOnClickListener(v -> dismiss());
        return view;
    }
}