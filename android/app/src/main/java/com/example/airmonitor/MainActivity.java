package com.example.airmonitor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo = null;

    private TextView logTextView;
    private StringBuilder logBuilder = new StringBuilder();
    private TextView textViewCO2;
    private TextView textViewTemperatura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logTextView = findViewById(R.id.log_text_view);
        textViewCO2 = findViewById(R.id.ValorCO2);
        textViewTemperatura = findViewById(R.id.ValorTemp);

        textViewCO2.setText("CO2: -");
        textViewTemperatura.setText("Cº: -");

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");
        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, " onCreate(): termina ");
    }

    private static final int MAX_LINEAS_LOG = 10;

    private void agregarAlLog(String mensaje) {
        logBuilder.append(mensaje).append("\n");
        String[] lineas = logBuilder.toString().split("\n");

        if (lineas.length > MAX_LINEAS_LOG) {
            logBuilder.delete(0, logBuilder.length());
            for (int i = lineas.length - MAX_LINEAS_LOG; i < lineas.length; i++) {
                logBuilder.append(lineas[i]).append("\n");
            }
        }
        runOnUiThread(() -> logTextView.setText(logBuilder.toString()));
    }

    private void buscarTodosLosDispositivosBTLE() {
        detenerBusquedaDispositivosBTLE();
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTLE(): empieza ");
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                mostrarInformacionDispositivoBTLE(resultado);
                agregarAlLog("Dispositivo detectado:\n" + resultado.getDevice().getAddress());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTLE(): onBatchScanResults() ");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTLE(): onScanFailed() ");
            }
        };
        this.elEscanner.startScan(this.callbackDelEscaneo);
    }

    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);
        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);
        String uuidDispositivo = Utilidades.bytesToHexString(tib.getUUID());
        Log.d(ETIQUETA_LOG, " uuid  = " + uuidDispositivo);

        UUID uuidObjetivo = Utilidades.stringToUUID("EPSG-GTI-PROY-3A");
        if (uuidDispositivo.equals(Utilidades.uuidToHexString(uuidObjetivo))) {
            Log.d(ETIQUETA_LOG, "Te has conectado a la SparkFun: " + bluetoothDevice.getName());
            agregarAlLog("Te has conectado a la SparkFun: " + bluetoothDevice.getName());
            mostrarValores(bytes);
        }
    }

    private void mostrarValores(byte[] bytes) {
        int valorCO2 = (bytes[11] & 0xFF) << 8 | (bytes[12] & 0xFF);
        int valorTemperatura = (bytes[10] & 0xFF);

        Log.d(ETIQUETA_LOG, "CO2 = " + valorCO2 + " ppm");
        Log.d(ETIQUETA_LOG, "Temperatura = " + valorTemperatura + " Cº");

        runOnUiThread(() -> {
            textViewCO2.setText("CO2: " + valorCO2);
            textViewTemperatura.setText("Cº: " + valorTemperatura);
        });
    }

    private void buscarEsteDispositivoBTLE(final UUID dispositivoBuscado) {
        detenerBusquedaDispositivosBTLE();

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                BluetoothDevice dispositivo = resultado.getDevice();
                byte[] bytes = resultado.getScanRecord().getBytes();
                TramaIBeacon tib = new TramaIBeacon(bytes);
                UUID uuid = Utilidades.bytesToUUID(tib.getUUID());

                if (uuid.equals(dispositivoBuscado)) {
                    mostrarInformacionDispositivoBTLE(resultado);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onBatchScanResults()");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onScanFailed() - Código de error: " + errorCode);
            }
        };

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
    }

    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        buscarEsteDispositivoBTLE(Utilidades.stringToUUID("EPSG-GTI-PROY-3A"));
    }

    private void detenerBusquedaDispositivosBTLE() {
        agregarAlLog("Se ha detenido la búsqueda.");
        if (this.callbackDelEscaneo != null) {
            this.elEscanner.stopScan(this.callbackDelEscaneo);
            this.callbackDelEscaneo = null;
        }
    }

    public void botonBuscarDispositivosBTLEPulsado(View v) {
        this.buscarTodosLosDispositivosBTLE();
    }

    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        detenerBusquedaDispositivosBTLE();
    }

    private void inicializarBlueTooth() {
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if (bta == null) {
            Log.d(ETIQUETA_LOG, "Bluetooth no está soportado.");
            return;
        }
        if (!bta.isEnabled()) {
            Log.d(ETIQUETA_LOG, "Bluetooth no está habilitado.");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODIGO_PETICION_PERMISOS);
            }
        }
        elEscanner = bta.getBluetoothLeScanner();
    }
}
