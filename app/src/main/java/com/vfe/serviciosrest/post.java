package com.vfe.serviciosrest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class post extends AppCompatActivity {

    private EditText txtCompetidores, txtDistancia;
    private Button btnCrearCarrera, btnVerCarreras;
    private TextView txtCarreras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        obtenerCarreras();
        txtCompetidores = findViewById(R.id.txtCompetidores);
        txtDistancia = findViewById(R.id.txtDistancia);
        btnCrearCarrera = findViewById(R.id.btnCrearCarrera);
        btnVerCarreras = findViewById(R.id.btnVerCarreras);
        txtCarreras = findViewById(R.id.txtCarreras);

        btnCrearCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearCarrera();
            }
        });

        btnVerCarreras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerCarreras();
            }
        });
    }

    private void crearCarrera() {
        String competidores = txtCompetidores.getText().toString().trim();
        String distancia = txtDistancia.getText().toString().trim();

        if (competidores.isEmpty() || distancia.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.getBaseUrl() + "/iniciar-carrera";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cantidadCompetidores", Integer.parseInt(competidores));
            jsonObject.put("distancia", Integer.parseInt(distancia));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(post.this, "Carrera creada con éxito", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(post.this, "Error al crear la carrera", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void obtenerCarreras() {
        String url = Config.getBaseUrl() + "/carreras";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        StringBuilder carreras = new StringBuilder();
                        try {
                            // Iterar sobre el array de carreras
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject carrera = response.getJSONObject(i);

                                // Usar long para manejar números grandes
                                long id = carrera.getLong("id");
                                int distanciaTotal = carrera.getInt("distanciaTotal");
                                boolean finalizada = carrera.getBoolean("finalizada");

                                // Añadir la información de la carrera al StringBuilder
                                carreras.append("ID: ").append(id)
                                        .append("\nDistancia: ").append(distanciaTotal).append("Km")
                                        .append("\nFinalizada: ").append(finalizada)
                                        .append("\n\n");
                            }

                            // Mostrar la lista de carreras en el TextView
                            txtCarreras.setText(carreras.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            txtCarreras.setText("Error al procesar las carreras");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(post.this, "Error al obtener las carreras", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }



}
