package com.vfe.serviciosrest;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class delete extends AppCompatActivity {

    private Spinner spinnerCarreras;
    private Button btnEliminarCarrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        spinnerCarreras = findViewById(R.id.spinnerCarreras);
        btnEliminarCarrera = findViewById(R.id.btnEliminarCarrera);

        // Cargar el Spinner automáticamente al abrir la actividad
        obtenerCarreras();

        // Configuración del botón para eliminar la carrera seleccionada
        btnEliminarCarrera.setOnClickListener(v -> eliminarCarrera());
    }

    private void eliminarCarrera() {
        String seleccion = spinnerCarreras.getSelectedItem().toString();
        String carreraId = seleccion.split(" ")[1];

        if (carreraId.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona una carrera válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.getBaseUrl() + "/carreras/" + carreraId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> Toast.makeText(delete.this, "Carrera eliminada con éxito", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(delete.this, "Error al eliminar la carrera", Toast.LENGTH_SHORT).show());

        requestQueue.add(stringRequest);
    }

    private void obtenerCarreras() {
        String url = Config.getBaseUrl() + "/carreras";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
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
                            Toast.makeText(delete.this, "No hay carreras disponibles", Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(delete.this,
                                    android.R.layout.simple_spinner_item, carrerasList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCarreras.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(delete.this, "Error al procesar las carreras", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(delete.this, "Error al obtener las carreras", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonArrayRequest);
    }
}
