package com.gmail.gpolomicz.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GPDEB";

    JSONArray jsonArray;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        ArrayList<String> arrayList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = db.rawQuery("SELECT number FROM articles WHERE id=" +position+1, null);
                cursor.moveToFirst();
                int number = cursor.getInt(0);
                cursor.close();

                DownloadData downloadData = new DownloadData();
                try {
                    String dane = downloadData.execute("https://hacker-news.firebaseio.com/v0/item/" + number + ".json?print=pretty").get();
                    JSONObject reader = new JSONObject(dane);
                    String url = reader.getString("url");

                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        if(downloadData()) {
//            databaseCreate();
//        }

        db = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT number FROM articles WHERE id <= 10", null);
        c.moveToFirst();
        do {
            DownloadData data = new DownloadData();
            try {
                String dane = data.execute("https://hacker-news.firebaseio.com/v0/item/" + c.getInt(0) + ".json?print=pretty").get();
                JSONObject reader = new JSONObject(dane);
                String title = reader.getString("title");
                arrayList.add(title);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } while (c.moveToNext());

        c.close();
    }

    private void databaseCreate() {
        db = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, number INTEGER)");

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                db.execSQL("INSERT INTO articles (number) VALUES (" + jsonArray.getInt(i) + ")");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean downloadData() {
        DownloadData data = new DownloadData();
        try {
            String dane = data.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
            jsonArray = new JSONArray(dane);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
