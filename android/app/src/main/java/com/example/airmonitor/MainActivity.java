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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PETICION_PERMISOS = 11223344;
    private static final int MAX_LINEAS_LOG = 10;

    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo = null;

    private TextView logTextView;
    private StringBuilder logBuilder = new StringBuilder();
    private TextView textViewCO2;
    private TextView textViewTemperatura;
    private ApiService apiService;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.236.37.23:13000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        textViewCO2 = findViewById(R.id.ValorCO2);
        textViewTemperatura = findViewById(R.id.ValorTemp);
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

    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        buscarEsteDispositivoBTLE(Utilidades.stringToUUID("EQUIPO-JAVIER-3A"));
    }

    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        detenerBusquedaDispositivosBTLE();
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

        UUID uuidObjetivo = Utilidades.stringToUUID("EQUIPO-JAVIER-3A");
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
        onDataReceived(valorCO2, valorTemperatura);
    }

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

    public void onDataReceived(String co2, String temp) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        SensorData co2Data = new SensorData("CO2", Integer.parseInt(co2));
        Call<Void> co2Call = apiService.postData(co2Data);
        co2Call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "CO2 data sent successfully");
                } else {
                    Log.e("API", "Failed to send CO2 data");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Error sending CO2 data: " + t.getMessage());
            }
        });

        SensorData tempData = new SensorData("Temperatura", Integer.parseInt(temp));
        Call<Void> tempCall = apiService.postData(tempData);
        tempCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Temperature data sent successfully");
                } else {
                    Log.e("API", "Failed to send Temperature data");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Error sending Temperature data: " + t.getMessage());
            }
        });
    }

    public void onDataReceived(int co2, int temperature) {
        agregarAlLog("Datos recibidos: CO2: " + co2 + ", Temp: " + temperature);

        textViewCO2.setText("CO2: " + co2);
        textViewTemperatura.setText("Cº: " + temperature);

        SensorData co2Data = new SensorData("CO2", co2);
        SensorData tempData = new SensorData("Temperatura", temperature);

        sendDataToServer(co2Data);
        sendDataToServer(tempData);
    }

    private void sendDataToServer(SensorData sensorData) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<Void> call = apiService.postData(sensorData);

        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    agregarAlLog("Datos enviados con éxito: " + sensorData.getName() + ": " + sensorData.getValue());
                } else {
                    agregarAlLog("Error al enviar datos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                agregarAlLog("Error en la solicitud: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ETIQUETA_LOG, " onResume() ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(ETIQUETA_LOG, " onPause() ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(ETIQUETA_LOG, " onStop() ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(ETIQUETA_LOG, " onDestroy() ");
    }
}