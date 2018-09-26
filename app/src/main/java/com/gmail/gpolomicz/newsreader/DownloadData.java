package com.gmail.gpolomicz.newsreader;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GPDEB";

    @Override
    protected String doInBackground(String... strings) {

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            InputStreamReader streamReader = new InputStreamReader(in);

            int data = streamReader.read();
            String allData = "";

            while (data != -1) {
                char current = (char) data;
                allData += current;
                data = streamReader.read();
            }
            return allData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
