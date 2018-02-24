package com.evancc.newsreaderapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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

        //Load the IDs for the top 500 stories
        loadDatabaseFromApi();

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

        try {
            //Clear previous stories and replace with new ones
            db.execSQL("DELETE FROM stories");

            DownloadTask task = new DownloadTask();
            String topNews = task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
            String[] topNewsArray = topNews.split(", ");
            Log.i("Number of Stories", Integer.toString(topNewsArray.length));
            //Log.i("New Stories Array", topNewsArray.toString());

            JSONArray topStories = new JSONArray();
            //JSONObject topStories = new JSONObject(topNews);
            //Log.i("Top News Ids ", topNews);

            if (topNewsArray.length > 0 ) {
                for(int cnt = 1; cnt <= 20; cnt++) {

                    String url = "https://hacker-news.firebaseio.com/v0/item/" + topNewsArray[cnt] + ".json?print=pretty";
                    Log.i("Story URL", url);
                    DownloadTask getStory = new DownloadTask();
                    String story = getStory.execute(url).get();
                    Log.i("Story", story);
                    JSONObject currentStory = new JSONObject(story);
                    topStories.put(currentStory);

                    String sql = "INSERT INTO stories (id, title, url) VALUES (" + currentStory.getString("id")
                            + ", " + currentStory.getString("title") + ", " + currentStory.getString("url") + ")";

                    db.execSQL(sql, null);
                }





            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createDatabaseTables() {
        //create tables in the database for loading of news
        db = this.openOrCreateDatabase("HackersNews", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS stories (id INTEGER, author VARCHAR, score INT(4), time DOUBLE, title VARCHAR, articleType VARCHAR, url VARCHAR)");
    }

}
