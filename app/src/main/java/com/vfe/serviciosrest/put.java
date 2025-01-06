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

public class put extends AppCompatActivity {

    private EditText txtCarreraId, txtNuevosCompetidores, txtNuevaDistancia;
    private Button btnActualizarCarrera, btnVerCarreras, btnBuscarCarrera;
    private TextView txtCarreras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put);

        txtCarreraId = findViewById(R.id.txtCarreraId);
        txtNuevosCompetidores = findViewById(R.id.txtNuevosCompetidores);
        txtNuevaDistancia = findViewById(R.id.txtNuevaDistancia);
        btnActualizarCarrera = findViewById(R.id.btnActualizarCarrera);
        btnVerCarreras = findViewById(R.id.btnVerCarreras);
        btnBuscarCarrera = findViewById(R.id.btnBuscarCarrera);
        txtCarreras = findViewById(R.id.txtCarreras);
        obtenerCarreras();
        btnActualizarCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarCarrera();
            }
        });

        btnVerCarreras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerCarreras();
            }
        });

        btnBuscarCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarCarrera();
            }
        });
    }

    private void actualizarCarrera() {
        String carreraId = txtCarreraId.getText().toString().trim();
        String nuevosCompetidores = txtNuevosCompetidores.getText().toString().trim();
        String nuevaDistancia = txtNuevaDistancia.getText().toString().trim();

        if (carreraId.isEmpty() || nuevosCompetidores.isEmpty() || nuevaDistancia.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.getBaseUrl() + "/actualizar-carrera/" + carreraId;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cantidadCompetidores", Integer.parseInt(nuevosCompetidores));
            jsonObject.put("distancia", Integer.parseInt(nuevaDistancia));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(put.this, "Carrera actualizada con éxito", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(put.this, "Error al actualizar la carrera", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(put.this, "Error al obtener las carreras", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void buscarCarrera() {
        String carreraId = txtCarreraId.getText().toString().trim();

        if (carreraId.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el ID de la carrera", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.getBaseUrl() + "/carreras/" + carreraId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener la distancia total de la carrera
                            int distanciaTotal = response.getInt("distanciaTotal");

                            // Obtener la lista de competidores
                            JSONArray competidores = response.getJSONArray("competidores");

                            // Total de competidores
                            int totalCompetidores = competidores.length();

                            // Crear el texto con los detalles requeridos
                            StringBuilder participantes = new StringBuilder();
                            participantes.append("Total de competidores: ").append(totalCompetidores).append("\n");
                            participantes.append("Distancia total de la carrera: ").append(distanciaTotal).append("km");

                            // Mostrar los detalles en el TextView
                            txtCarreras.setText(participantes.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            txtCarreras.setText("Error al obtener la información de la carrera");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(put.this, "Error al obtener los detalles de la carrera", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

}
