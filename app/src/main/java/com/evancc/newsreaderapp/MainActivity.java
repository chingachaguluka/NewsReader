package com.evancc.newsreaderapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<String> dummyData = new ArrayList<String>();
    SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        listView = findViewById(R.id.listView);
        dummyData.add("Windows 11 Rumours");
        dummyData.add("Will Linux Ever Overtake Windows");
        dummyData.add("Apple Pencil Support on Android");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dummyData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, dummyData.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(intent);
            }
        });

    }

    public void loadDatabaseFromApi() {

        String results = "";
        URL url = new URL("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        try {
            //Clear previous stories and replace with new ones
            db.execSQL("DELETE FROM news");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createDatabaseTables() {
        //create tables in the database for loading of news
        db = this.openOrCreateDatabase("HackersNews", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS news (id INTEGER, author VARCHAR, score INT(4), time DOUBLE, title VARCHAR, articleType VARCHAR, url VARCHAR)");
    }

    public class DownloadJSON extends AsyncTask <String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}
