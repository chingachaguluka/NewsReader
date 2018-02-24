package com.evancc.newsreaderapp;

import android.content.Intent;
import android.os.AsyncTask;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cchaguluka on 24/02/2018.
 */

public class DownloadTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {

        String result = "";

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            int inputStreamReader = inputStream.read();

            while(inputStreamReader != -1) {
                char current = (char) inputStreamReader;
                result += current;

                inputStreamReader = inputStream.read();
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}

