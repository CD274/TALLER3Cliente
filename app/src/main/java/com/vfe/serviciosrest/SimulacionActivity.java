package com.vfe.serviciosrest;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

import java.util.ArrayList;
import java.util.List;

public class SimulacionActivity extends AppCompatActivity {

    private Spinner spinnerCarreras;
    private TextView txtResultado;
    private Button btnIniciarSimulacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simulacion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerCarreras = findViewById(R.id.spinnerCarreras);
        txtResultado = findViewById(R.id.txtResultado);
        btnIniciarSimulacion = findViewById(R.id.btnIniciarSimulacion);

        obtenerCarreras();

        btnIniciarSimulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSimulacion();
            }
        });
    }

    private void obtenerCarreras() {
        String url = Config.getBaseUrl() + "/carreras";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<String> carrerasList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject carrera = response.getJSONObject(i);

                                boolean finalizada = carrera.getBoolean("finalizada");
                                JSONObject ganador = carrera.optJSONObject("ganador");

                                // Verificar si la carrera no está finalizada y no tiene ganador
                                if (!finalizada && ganador == null) {
                                    int id = carrera.getInt("id");
                                    carrerasList.add("Carrera " + id);
                                }
                            }

                            if (carrerasList.isEmpty()) {
                                Toast.makeText(SimulacionActivity.this, "No hay carreras disponibles para simular", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(SimulacionActivity.this,
                                        android.R.layout.simple_spinner_item, carrerasList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCarreras.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SimulacionActivity.this, "Error al procesar las carreras", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SimulacionActivity.this, "Error al obtener las carreras", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }


    private void iniciarSimulacion() {
        String seleccion = spinnerCarreras.getSelectedItem().toString();
        String carreraId = seleccion.split(" ")[1]; // Extrae el ID de la carrera seleccionada

        String url = Config.getBaseUrl() + "/simular-carrera/" + carreraId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String mensaje = response.getString("mensaje");
                            JSONArray historial = response.getJSONArray("historial");
                            JSONObject ganador = response.getJSONObject("ganador");

                            StringBuilder resultado = new StringBuilder();
                            resultado.append(mensaje).append("\n\nHistorial:\n");

                            for (int i = 0; i < historial.length(); i++) {
                                JSONObject estado = historial.getJSONObject(i);
                                int hora = estado.getInt("hora");
                                JSONObject estadoActual = estado.getJSONObject("estado");

                                resultado.append("Hora ").append(hora).append(":\n");
                                for (int j = 0; j < estadoActual.names().length(); j++) {
                                    String corredor = estadoActual.names().getString(j);
                                    double distancia = estadoActual.getDouble(corredor);
                                    resultado.append(corredor).append(": ").append(distancia).append("Km\n");
                                }
                                resultado.append("\n");
                            }

                            resultado.append("Ganador: Competidor ").append(ganador.getInt("id"))
                                    .append(" con una distancia de ").append(ganador.getString("distanciaRecorrida"))
                                    .append("Km en ").append(ganador.getInt("tiempoTotal")).append(" horas.");

                            txtResultado.setText(resultado.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            txtResultado.setText("Error al procesar la simulación");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SimulacionActivity.this, "Error al iniciar la simulación", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}
