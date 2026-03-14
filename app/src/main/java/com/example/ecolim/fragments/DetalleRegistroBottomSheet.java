package com.example.ecolim.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.ecolim.R;
import com.example.ecolim.models.Registro;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

public class DetalleRegistroBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_REGISTRO_ID = "registro_id";
    private Registro registro;

    public static DetalleRegistroBottomSheet newInstance(Registro r) {
        DetalleRegistroBottomSheet sheet = new DetalleRegistroBottomSheet();
        Bundle args = new Bundle();
        // Pasamos todos los datos por Bundle
        args.putLong("id",            r.getId());
        args.putString("tipo",        r.getTipoResiduo());
        args.putString("categoria",   r.getCategoriaResiduo());
        args.putDouble("cantidad",    r.getCantidad());
        args.putString("unidad",      r.getUnidad());
        args.putString("ubicacion",   r.getUbicacion());
        args.putString("fecha",       r.getFecha());
        args.putString("hora",        r.getHora());
        args.putString("usuario",     r.getNombreUsuario());
        args.putString("estado",      r.getEstado());
        args.putString("obs",         r.getObservaciones());
        args.putString("foto", r.getFotoUri());
        args.putBoolean("sync",       r.isSincronizado());
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_bottomsheet,
                container, false);

        Bundle args = getArguments();
        if (args == null) return view;

        // Vistas
        View      colorIndicator    = view.findViewById(R.id.view_color_indicator);
        TextView  txtTipo           = view.findViewById(R.id.txt_detalle_tipo);
        TextView  txtCategoria      = view.findViewById(R.id.txt_detalle_categoria);
        TextView  txtId             = view.findViewById(R.id.txt_detalle_id);
        TextView  txtCantidad       = view.findViewById(R.id.txt_detalle_cantidad);
        TextView  txtEstado         = view.findViewById(R.id.txt_detalle_estado);
        TextView  txtUbicacion      = view.findViewById(R.id.txt_detalle_ubicacion);
        TextView  txtFecha          = view.findViewById(R.id.txt_detalle_fecha);
        TextView  txtHora           = view.findViewById(R.id.txt_detalle_hora);
        TextView  txtUsuario        = view.findViewById(R.id.txt_detalle_usuario);
        TextView  txtSync           = view.findViewById(R.id.txt_detalle_sync);
        TextView  txtObs            = view.findViewById(R.id.txt_detalle_observaciones);
        CardView  cardFoto          = view.findViewById(R.id.card_foto);
        ImageView imgFoto           = view.findViewById(R.id.img_detalle_foto);

        // Datos
        String categoria = args.getString("categoria", "");
        String obs       = args.getString("obs", "");
        String fotoPath  = args.getString("foto", "");
        boolean sync     = args.getBoolean("sync", false);

        txtTipo.setText(args.getString("tipo", ""));
        txtCategoria.setText(categoria);
        txtId.setText("#" + args.getLong("id"));
        txtCantidad.setText(args.getDouble("cantidad") + " " + args.getString("unidad", ""));
        txtEstado.setText(args.getString("estado", ""));
        txtUbicacion.setText(args.getString("ubicacion", ""));
        txtFecha.setText(args.getString("fecha", ""));
        txtHora.setText(args.getString("hora", ""));
        txtUsuario.setText(args.getString("usuario", ""));
        txtObs.setText(obs.isEmpty() ? "Sin observaciones" : obs);

        // Sync
        txtSync.setText(sync ? "✅ Sincronizado" : "⏳ Pendiente");
        txtSync.setTextColor(sync ? 0xFF2E7D32 : 0xFFE65100);

        // Color barra según categoría
        int color;
        switch (categoria) {
            case "Peligroso": color = 0xFFF44336; break;
            case "Especial":  color = 0xFF9C27B0; break;
            default:          color = 0xFF4CAF50; break;
        }
        colorIndicator.setBackgroundColor(color);

        // Foto
        if (fotoPath != null && !fotoPath.isEmpty()) {
            File file = new File(fotoPath);
            if (file.exists()) {
                cardFoto.setVisibility(View.VISIBLE);
                imgFoto.setImageURI(Uri.fromFile(file));
            } else {
                // Si es Uri de galería
                try {
                    cardFoto.setVisibility(View.VISIBLE);
                    imgFoto.setImageURI(Uri.parse(fotoPath));
                } catch (Exception e) {
                    cardFoto.setVisibility(View.GONE);
                }
            }
        }

        return view;
    }
}