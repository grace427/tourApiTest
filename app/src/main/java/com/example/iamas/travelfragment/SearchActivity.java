package com.example.iamas.travelfragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    final static String TAG = "ThemeActivity";

    String keyword;
    private EditText edt_search2;
    private Button button2;

    RequestQueue queue;
    ArrayList<ThemeData> list = new ArrayList<>();

    RecyclerView recyclerView;
    SearchAdapter adapter;
    LinearLayoutManager layoutManager;
    ProgressDialog pDialog;


    static final String KEY = "OEZDFxQGYkA8crUzSlj51nwQQb9Jh78Y5UWvaW5gXccZ5t2ttRXNjcdXjJJ8FsHlriUWu%2B%2FVhFfuI32FbuMhTA%3D%3D";
    static final String appName = "Zella";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edt_search2 = findViewById(R.id.edt_search2);
        button2 = findViewById(R.id.button2);

        Intent intent = getIntent();
        String word = intent.getStringExtra("word");


//        AsyncTaskClassSearch async = new AsyncTaskClassSearch();
//        async.execute(word);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = edt_search2.getText().toString().trim();
                if(word.length() > 1){
                    searchData(word);
                }else{
                    Toast.makeText(getApplicationContext(), "두 글자 이상 입력해 주세요", Toast.LENGTH_LONG).show();
                }
//                AsyncTaskClassSearch async = new AsyncTaskClassSearch();
//                async.execute(edt_search2.getText().toString().trim());
            }
        });

        recyclerView = findViewById(R.id.grid_recyclerview);
        adapter = new SearchAdapter(R.layout.item_theme, SearchActivity.this, list);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);


        searchData(word);

    }

    class AsyncTaskClassSearch extends android.os.AsyncTask<String, Long, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayLoader();
        }

        @Override
        protected String doInBackground(String... strings) {
            String word = strings[0];
            Log.d(TAG, word);
            searchData(word);
            return "작업 종료";
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    } // end of AsyncTaskClassSearch


    public void searchData(String word) {
        try {
            keyword = URLEncoder.encode(word, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        queue = Volley.newRequestQueue(getApplicationContext());

        String url = "http://api.visitkorea.or.kr/openapi/service/"
                + "rest/KorService/searchKeyword?ServiceKey=" + KEY
                + "&keyword=" + keyword + "&areaCode=1&listYN=Y&arrange=P"
                + "&numOfRows=20&pageNo=1&MobileOS=AND&MobileApp="
                + appName + "&_type=json";

        if(url != null) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject parse_response = (JSONObject) response.get("response");
                                JSONObject parse_body = (JSONObject) parse_response.get("body");
                                JSONObject parse_items = (JSONObject) parse_body.get("items");
                                JSONArray parse_itemlist = (JSONArray) parse_items.get("item");

                                list.removeAll(list);

                                for (int i = 0; i < parse_itemlist.length(); i++) {
                                    JSONObject imsi = (JSONObject) parse_itemlist.get(i);

                                    ThemeData themeData = new ThemeData();
                                    themeData.setFirstImage(imsi.getString("firstimage"));
                                    themeData.setTitle(imsi.getString("title"));
                                    themeData.setContentsID(Integer.valueOf(imsi.getString("contentid")));

                                    list.add(themeData);
                                }

                                recyclerView.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(getApplicationContext(), "검색결과 없음", Toast.LENGTH_LONG).show();
        }
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ThemeData.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

}
