package com.vfe.serviciosrest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnPost, btnPut, btnAll, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de los botones
        btnPost = findViewById(R.id.btnPost);
        btnPut = findViewById(R.id.btnPut);
        btnDelete = findViewById(R.id.btnDelete);
        btnAll = findViewById(R.id.btnAll);

        // Listener común para los botones
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnPost) {
                    startActivity(new Intent(MainActivity.this, post.class));
                } else if (v.getId() == R.id.btnPut) {
                    startActivity(new Intent(MainActivity.this, put.class));
                } else if (v.getId() == R.id.btnDelete) {
                    startActivity(new Intent(MainActivity.this, delete.class));
                } else if (v.getId() == R.id.btnAll) {
                    startActivity(new Intent(MainActivity.this, SimulacionActivity.class)); // Asegúrate de crear la clase "all"
                }
            }
        };

        // Asignar el listener a los botones
        btnPost.setOnClickListener(listener);
        btnPut.setOnClickListener(listener);
        btnDelete.setOnClickListener(listener);
        btnAll.setOnClickListener(listener);
    }
}
