package com.example.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.tabs.R; // Asegúrate de ajustar este import al paquete de tu proyecto.

public class Tab3 extends Fragment {

    private EditText entrada;
    private ImageView botonCalcular;
    private Button boton;
    private TextView salida;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3, container, false);

        entrada = rootView.findViewById(R.id.entrada);
        botonCalcular = rootView.findViewById(R.id.botonCalcular);
        boton = rootView.findViewById(R.id.boton);
        salida = rootView.findViewById(R.id.salida);

        botonCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sePulsa(botonCalcular);
            }
        });

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sePulsa0(v);
            }
        });

        return rootView;
    }


    public void sePulsa(View view){
        salida.setText(String.valueOf(
                Float.parseFloat(entrada.getText().toString())*2.0));
    }

    public void sePulsa0(View view) {
        entrada.setText(entrada.getText() + (String) view.getTag());
    }
}