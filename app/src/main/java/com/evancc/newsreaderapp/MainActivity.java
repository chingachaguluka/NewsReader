package com.evancc.newsreaderapp;

import android.content.Intent;
import android.database.Cursor;
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
    List<String> storyItems = new ArrayList<String>();
    List<String> storyId = new ArrayList<String>();
    SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create or open database and tables
        createDatabaseTables();
        //Load the IDs for the top 500 stories
        loadDatabaseFromApi();

        listView = findViewById(R.id.listView);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, storyItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String currentStoryID = storyId.get(position);
                String url = "SELECT * FROM stories WHERE id = " + currentStoryID;
                Log.i("SQL Query", url);
                Cursor c = db.rawQuery(url, null);
                c.moveToFirst();


                int storyIdUrl = c.getColumnIndex("url");
                Log.i("SQL Column Index", Integer.toString(storyIdUrl));
                String storyUrl = c.getString(storyIdUrl);
                Log.i("Story URL", storyUrl);


                //Toast.makeText(MainActivity.this, storyItems.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                intent.putExtra("url", storyUrl);
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
                for(int cnt = 1; cnt <= 5; cnt++) {

                    String url = "https://hacker-news.firebaseio.com/v0/item/" + topNewsArray[cnt] + ".json?print=pretty";
                    Log.i("Story URL", url);
                    DownloadTask getStory = new DownloadTask();
                    String story = getStory.execute(url).get();
                    Log.i("Story", story);
                    JSONObject currentStory = new JSONObject(story);
                    topStories.put(currentStory);
                    storyItems.add(currentStory.getString("title"));
                    storyId.add(currentStory.getString("id"));

                    String title = currentStory.getString("title").replaceAll("'", "''");

                    String sql = "INSERT INTO stories (id, title, url) VALUES (" + currentStory.getString("id")
                            + ", '" + title + "', '" + currentStory.getString("url") + "')";
                    Log.i("SQL Statement", sql);
                    db.execSQL(sql);
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
