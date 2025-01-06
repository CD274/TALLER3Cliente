package com.vfe.serviciosrest;

public class Config {

    // Dirección IP base para las consultas
    private static final String BASE_URL = "http://192.168.1.104:3000"; // Cambia esta IP según sea necesario
    public static String getBaseUrl() {
        return BASE_URL;
    }
}
