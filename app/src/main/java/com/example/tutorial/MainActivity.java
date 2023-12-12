package com.example.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial.model.Bebida;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    List<Bebida> bebidasResponse = new ArrayList<>();
    List<Bebida> pedido = new ArrayList<>();
    private LinearLayout menuLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuLinearLayout = findViewById(R.id.menuLinearLayout);

        consumirAPI();
    }

    private void consumirAPI() {
        String url = "http://10.0.2.2:8081/api/bebidas";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> realizarConsulta(url));

        try {
            String result = future.get(15, TimeUnit.SECONDS);
            runOnUiThread(() -> processarResultado(result));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            runOnUiThread(() -> exibirResultado(e.toString()));
        } finally {
            executor.shutdown();
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
            exibirResultado("Realizar consulta erro. -> " + e);
        }


        return null;
    }

    private void processarResultado(String result) {
        if (result != null) {
            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    int sizeMl = jsonObject.getInt("size_ml");
                    double cost = jsonObject.getDouble("cost");
                    String imageUrl = jsonObject.getString("image_base64");

                    Bebida bebida = new Bebida(id, name, sizeMl, cost, imageUrl);
                    bebidasResponse.add(bebida);

                    adicionarCard(bebida, i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                exibirResultado("Erro ao analisar JSON");
            }
        } else {
            exibirResultado("Erro na consulta à URL. Result nulo");
        }
    }

    private void adicionarCard(Bebida bebida, int itemId) {
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardView.setCardBackgroundColor(Color.parseColor("#FF9800"));
        cardView.setPadding(20, 20, 20, 20);

        // Criar um novo LinearLayout para o CardView
        LinearLayout cardLinearLayout = new LinearLayout(this);
        cardLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLinearLayout.setPadding(20, 20, 20, 20);

        // adicionando imagem com Picasso (pra pegar a imagem online)
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                350,
                350
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(bebida.getImage_base64()).into(imageView);

        cardLinearLayout.addView(imageView);

        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        textLinearLayout.setPadding(16, 0, 0, 0);

        TextView nameLabel = new TextView(this);
        nameLabel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        nameLabel.setText(bebida.getName());
        nameLabel.setTextColor(Color.WHITE);
        nameLabel.setTextSize(24);
        textLinearLayout.addView(nameLabel);

        TextView costLabel = new TextView(this);
        costLabel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        costLabel.setText(String.format("R$ %.2f", bebida.getCost()));
        costLabel.setTextColor(Color.WHITE);
        costLabel.setTextSize(18);
        textLinearLayout.addView(costLabel);

        Button addButton = new Button(this);
        addButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        addButton.setId(itemId);
        addButton.setPadding(0, 10, 0, 0);
        addButton.setText("Adicionar ao pedido");
        addButton.setTextColor(Color.WHITE);
        addButton.setBackgroundColor(Color.BLACK);
        textLinearLayout.addView(addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedItemId = view.getId();

                Bebida clickedBebida = findBebidaById(clickedItemId);

                if (clickedBebida != null) {
                    pedido.add(clickedBebida);
                    Toast.makeText(MainActivity.this, clickedBebida.getName() + " foi adicionado ao seu pedido.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cardLinearLayout.addView(textLinearLayout);
        cardView.addView(cardLinearLayout);

        menuLinearLayout.addView(cardView);
    }

    private Bebida findBebidaById(int itemId) {
        for (Bebida bebida : bebidasResponse) {
            if (bebida.getId() == itemId) {
                return bebida;
            }
        }
        return null;
    }

    private void exibirResultado(String resultado) {
        System.out.println("Rafael: " + resultado);
    }

    public void finalizarPedido(View view) {
        if (pedido != null && pedido.size() > 0) {
            exibirResultado(Integer.toString(pedido.size()));
            Intent intent = new Intent(MainActivity.this, FimPedidoActivity.class);
            intent.putParcelableArrayListExtra("PEDIDO", new ArrayList<>(pedido));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(MainActivity.this, "Seu pedido está vazio!", Toast.LENGTH_SHORT).show();
        }
    }

}

