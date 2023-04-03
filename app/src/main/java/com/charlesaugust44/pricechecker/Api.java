package com.charlesaugust44.pricechecker;


import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Api {
    public static final String INVOICE_ENDPOINT = "/invoices";
    public static final String PRODUCT_ENDPOINT = "/products";
    private static final String URL = "http://192.168.100.18:8000/api";

    @NonNull
    public static String get(String endpoint, String getParams) {
        StringBuilder result = new StringBuilder();
        try {
            URL url;
            HttpURLConnection urlConnection = null;
            System.setProperty("http.keepAlive", "false");

            try {
                url = new URL(URL + endpoint + "?" + getParams);
                //open a URL coonnection
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);
                int data = isw.read();
                while (data != -1) {
                    result.append((char) data);
                    data = isw.read();
                }
                // return the data to onPostExecute method
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
        return result.toString();
    }

    public static String post(String endpoint, String jsonBody) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(URL + endpoint);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonBody.getBytes());
            os.flush();
            switch (urlConnection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    br.close();
                    break;
                case HttpURLConnection.HTTP_CONFLICT:
                    result.append("409");
                    break;
                case HttpURLConnection.HTTP_CREATED:
                    result.append("201");
                    break;
                default:
                    throw new RuntimeException("Error: HTTP error code : " + urlConnection.getResponseCode());
            }

            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        return result.toString();
    }
}
