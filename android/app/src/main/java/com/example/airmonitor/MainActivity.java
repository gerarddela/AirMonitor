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
        textViewTemperatura.setText("Temperatura: -");

        Log.d(ETIQUETA_LOG, "onCreate(): empieza");
        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, "onCreate(): termina");

        String baseUrl = ApiConfig.BASE_URL;
        Retrofit retrofit = ApiClient.getClient(baseUrl);
        apiService = retrofit.create(ApiService.class);
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
        if (bytes.length < 14) {
            Log.d(ETIQUETA_LOG, "El array de bytes es demasiado corto.");
            return; // Salir si no hay suficientes bytes
        }

        int major = ((bytes[10] & 0xFF) << 8 | (bytes[11] & 0xFF));
        int minor = ((bytes[12] & 0xFF) << 8 | (bytes[13] & 0xFF));

        float valorCO2 = 0;
        float valorTemperatura = 0;

        Log.d(ETIQUETA_LOG, "Major = " + major + ", Minor = " + minor);

        // Usar el valor de major para determinar si es CO2 o Temperatura
        if (major == 0x11) { // Identificador para CO2
            valorCO2 = minor; // El valor está en el campo minor para CO2
        } else if (major == 0x12) { // Identificador para Temperatura
            valorTemperatura = minor / 10.0f; // Asegúrate de que este ajuste sea correcto
        }

        Log.d(ETIQUETA_LOG, "CO2 = " + valorCO2 + " ppm");
        Log.d(ETIQUETA_LOG, "Temperatura = " + valorTemperatura + " Cº");

        // Declarar las variables finales para usarlas dentro de la lambda
        final float finalValorCO2 = valorCO2;
        final float finalValorTemperatura = valorTemperatura;

        // Actualizar la UI con los valores
        if (mostrarEnUI) {
            Log.d(ETIQUETA_LOG, "Actualizando la UI con CO2: " + finalValorCO2 + ", Temperatura: " + finalValorTemperatura);
            runOnUiThread(() -> {
                textViewCO2.setText("CO2: " + finalValorCO2 + " ppm");
                textViewTemperatura.setText("Temperatura: " + finalValorTemperatura + " Cº");
            });
        }

        // Enviar datos al servidor si corresponde
        if (enviarDatos) {
            Log.d(ETIQUETA_LOG, "Enviando datos al servidor");
            onDataReceived((int) valorCO2, (int) valorTemperatura); // Enviar valores al método onDataReceived
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
        if (logBuilder.length() > MAX_LINEAS_LOG) {
            int indexFin = logBuilder.indexOf("\n", logBuilder.indexOf("\n") + 1);
            logBuilder.delete(0, indexFin + 1);
        }
    }

    private void inicializarBlueTooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            agregarAlLog("Este dispositivo no soporta Bluetooth");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            agregarAlLog("Bluetooth está desactivado");
            // Puedes iniciar una actividad para activar Bluetooth aquí
        }
        this.elEscanner = bluetoothAdapter.getBluetoothLeScanner();
        Log.d(ETIQUETA_LOG, "Bluetooth LE Scanner inicializado.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerBusquedaDispositivosBTLE();
    }
}