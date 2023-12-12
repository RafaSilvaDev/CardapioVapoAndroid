package com.example.tutorial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.tutorial.model.Bebida;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FimPedidoActivity extends AppCompatActivity {
    List<Bebida> pedido;
    private LinearLayout menuLinearLayout;
    private EditText editTextTitular;
    private TextView textViewTotal;
    private TextView textViewTempoPreparo;
    private double valorTotal = 0.0;
    private int tempoPreparo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fimpedido);

        menuLinearLayout = findViewById(R.id.itemsListLayout);
        editTextTitular = findViewById(R.id.titularText);
        textViewTotal = findViewById(R.id.orderTotalTextView);
        textViewTempoPreparo = findViewById(R.id.preparationTimeTextView);

        Intent intent = getIntent();
        pedido = intent.getParcelableArrayListExtra("PEDIDO");
        if (pedido != null)
            carregarPedido(pedido);
        else
            Toast.makeText(FimPedidoActivity.this, "Sua lista de pedidos é nula!", Toast.LENGTH_SHORT).show();
    }

    private void carregarPedido(List<Bebida> pedido) {
        tempoPreparo = 2 * pedido.size();
        for(Bebida item : pedido) {
            adicionarCard(item, item.getId());
            valorTotal += item.getCost();
        }
        textViewTempoPreparo.setText(tempoPreparo + " minutos");
        textViewTotal.setText("R$" + valorTotal);
    }

    // Modificar o método adicionarCard para criar e adicionar o CardView ao menuLinearLayout
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

        // Adicionar a imagem ao novo LinearLayout usando o Picasso
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

        // Adicionar os textos ao novo LinearLayout de texto
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

        cardLinearLayout.addView(textLinearLayout);

        cardView.addView(cardLinearLayout);

        menuLinearLayout.addView(cardView);
    }

    public void novoPedido(View view) {
        Intent intent = new Intent(FimPedidoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void finalizarPedido(View view) {
        if(!editTextTitular.getText().toString().isEmpty()) {
            try {
                String data = "Dados do pedido: " +
                        "Titular: " + editTextTitular.getText().toString() +
                        ", Valor total: R$" + textViewTotal.getText().toString() +
                        ", Tempo de preparo: " + textViewTempoPreparo.getText().toString() + "." +
                        "   | Itens do pedido: ";
                for (Bebida item : pedido) {
                    data += "\n" + item.getName();
                }
                String url = "https://api.qrserver.com/v1/create-qr-code/?size=350x350&data=" + data;
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url)));
            } catch (Exception e) {
                System.out.println("Rafael: " + e);
            }
        } else {
            Toast.makeText(FimPedidoActivity.this, "Ei, precisamos saber seu nome!", Toast.LENGTH_SHORT).show();
        }
    }
}