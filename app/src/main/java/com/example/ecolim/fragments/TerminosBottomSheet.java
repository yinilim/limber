package com.example.ecolim.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ecolim.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TerminosBottomSheet extends BottomSheetDialogFragment {

    public static TerminosBottomSheet newInstance() {
        return new TerminosBottomSheet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminos_bottomsheet,
                container, false);
        view.findViewById(R.id.btn_aceptar_terminos)
                .setOnClickListener(v -> dismiss());
        return view;
    }
}