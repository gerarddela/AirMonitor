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
    private ScanCallback callbackDelEscaneo;

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

        Log.d(ETIQUETA_LOG, "onCreate(): empieza");
        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, "onCreate(): termina");

        String baseUrl = ApiConfig.BASE_URL;
        Retrofit retrofit = ApiClient.getClient(baseUrl);
        apiService = retrofit.create(ApiService.class);

        SensorData co2Data = new SensorData("CO2", 218.00f);
        sendDataToServer(co2Data);

        SensorData temperaturaData = new SensorData("Temperatura", 25.00f);
        sendDataToServer(temperaturaData);
    }

    private void buscarTodosLosDispositivosBTLE() {
        detenerBusquedaDispositivosBTLE();
        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empieza");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);

                String nombreDispositivo = resultado.getDevice().getName();
                String uuid = nombreDispositivo != null ? nombreDispositivo : "";

                if (!"EQUIPO-GERARD-3A".equals(uuid)) {
                    mostrarInformacionDispositivoBTLE(resultado);
                    agregarAlLog("Dispositivo detectado:\n" + resultado.getDevice().getAddress());
                } else {
                    Log.d(ETIQUETA_LOG, "Dispositivo ignorado: " + uuid);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                for (ScanResult result : results) {
                    String nombreDispositivo = result.getDevice().getName();
                    String uuid = nombreDispositivo != null ? nombreDispositivo : "";

                    if (!"EQUIPO-GERARD-3A".equals(uuid)) {
                        mostrarInformacionDispositivoBTLE(result);
                        agregarAlLog("Dispositivo detectado:\n" + result.getDevice().getAddress());
                    } else {
                        Log.d(ETIQUETA_LOG, "Dispositivo ignorado: " + uuid);
                    }
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanFailed() - Código de error: " + errorCode);
            }
        };

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
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
                    Log.d(ETIQUETA_LOG, "Conectado al dispositivo Sparkfun: " + dispositivo.getName());
                    agregarAlLog("Conectado al dispositivo Sparkfun: " + dispositivo.getName());

                    mostrarValores(bytes, true, true);
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

        runOnUiThread(() -> {
            textViewCO2.setText("CO2: - ");
            textViewTemperatura.setText("Temperatura: - ");
        });
    }


    public void botonBuscarDispositivosBTLEPulsado(View v) {
        this.buscarTodosLosDispositivosBTLE();
    }

    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        buscarEsteDispositivoBTLE(Utilidades.stringToUUID("EQUIPO-GERARD-3A"));
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

        mostrarValores(bytes, false, false);
    }

    private void mostrarValores(byte[] bytes, boolean enviarDatos, boolean mostrarEnUI) {
        float valorCO2 = ((float)((bytes[11] & 0xFF) << 8 | (bytes[12] & 0xFF))) / 100.0f;
        float valorTemperatura = ((float)(bytes[10] & 0xFF)) / 10.0f;

        Log.d(ETIQUETA_LOG, "CO2 = " + valorCO2 + " ppm");
        Log.d(ETIQUETA_LOG, "Temperatura = " + valorTemperatura + " Cº");

        if (mostrarEnUI) {
            runOnUiThread(() -> {
                textViewCO2.setText("CO2: " + valorCO2 + " ppm");
                textViewTemperatura.setText("Temperatura: " + valorTemperatura + " Cº");
            });
        }

        if (enviarDatos) {
            onDataReceived((int) valorCO2, (int) valorTemperatura);
        }
    }

    public void onDataReceived(int co2, int temperature) {
        agregarAlLog("Datos recibidos: CO2: " + co2 + ", Temp: " + temperature);

        boolean validCo2 = co2 >= 0;
        boolean validTemperature = temperature >= 0;

        agregarAlLog("Validación: CO2 " + (validCo2 ? "válido" : "no válido") + ", Temperatura " + (validTemperature ? "válido" : "no válido"));

        if (validCo2 || validTemperature) {
            if (validCo2) {
                SensorData co2Data = new SensorData("CO2", (float) co2);
                agregarAlLog("Enviando CO2: " + co2Data.getValor());
                sendDataToServer(co2Data);
            } else {
                agregarAlLog("Valor CO2 no válido: " + co2);
            }

            if (validTemperature) {
                SensorData tempData = new SensorData("Temperatura", (float) temperature);
                agregarAlLog("Enviando Temperatura: " + tempData.getValor());
                sendDataToServer(tempData);
            } else {
                agregarAlLog("Valor de Temperatura no válido: " + temperature);
            }
        } else {
            agregarAlLog("Ambos valores no válidos: CO2: " + co2 + ", Temp: " + temperature);
        }
    }

    private void sendDataToServer(SensorData sensorData) {
        agregarAlLog("Enviando datos al servidor: " + sensorData.getTipo_dato_nombre() + ", Valor: " + sensorData.getValor());

        Call<Void> call = apiService.postData(sensorData);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    agregarAlLog("Datos enviados con éxito: " + sensorData.getTipo_dato_nombre() + ", Valor: " + sensorData.getValor());
                } else {
                    agregarAlLog("Error al enviar datos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                agregarAlLog("Error en la comunicación: " + t.getMessage());
            }
        });
    }

    private void agregarAlLog(String mensaje) {
        logBuilder.append(mensaje).append("\n");
        logTextView.setText(logBuilder.toString());

        if (logBuilder.toString().split("\n").length > MAX_LINEAS_LOG) {
            String[] lineas = logBuilder.toString().split("\n");
            logBuilder = new StringBuilder();
            for (int i = lineas.length - MAX_LINEAS_LOG; i < lineas.length; i++) {
                logBuilder.append(lineas[i]).append("\n");
            }
        }
        logTextView.setText(logBuilder.toString());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerBusquedaDispositivosBTLE();
    }
}