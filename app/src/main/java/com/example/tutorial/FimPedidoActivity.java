package com.example.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class FimPedidoActivity extends AppCompatActivity {
    private EditText editTextURL;
    private TextView textViewResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fimpedido);

    }

    private void consultarURL() {
        String url = editTextURL.getText().toString();

        if (url.isEmpty()) {
            Toast.makeText(this, "Digite uma URL válida", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> realizarConsulta(url));

        try {
            // Define um timeout de 15 segundos
            String result = future.get(15, TimeUnit.SECONDS);
            Toast.makeText(this, "Agora vai !!!", Toast.LENGTH_SHORT).show();
            runOnUiThread(() -> processarResultado(result));
            //runOnUiThread(() -> exibirResultado("teste"));
        } catch (TimeoutException e) {
            // Tratar o caso em que a consulta excede o tempo limite
            runOnUiThread(() -> exibirResultado("A consulta excedeu o tempo limite de 15 segundos"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Encerra a thread do executor
        }
    }

    private String realizarConsulta(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    private void processarResultado(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject usdbrl =
                        jsonObject.getJSONObject("USDBRL");
                String high = usdbrl.getString("low");
                String resultado = "Cotação US$ = " + high;
                exibirResultado(resultado);
            } catch (JSONException e) {
                e.printStackTrace();
                exibirResultado("Erro ao analisar JSON");
            }
        } else {
            exibirResultado("Erro na consulta à URL");
        }
    }

    private void exibirResultado(String resultado) {
        textViewResultado.setText(resultado);
    }

    public void novoPedido(View view) {
        Intent intent = new Intent(FimPedidoActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
