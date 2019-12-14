package com.example.iamas.travelfragment;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    Context context;
    ArrayList<ThemeData> list;
    int layout;

    View view;
    View viewDialog;
    static final String KEY = "OEZDFxQGYkA8crUzSlj51nwQQb9Jh78Y5UWvaW5gXccZ5t2ttRXNjcdXjJJ8FsHlriUWu%2B%2FVhFfuI32FbuMhTA%3D%3D";
    static final String appName = "Zella";

    ThemeData detailThemeData = new ThemeData();

    RequestQueue queue;


    public SearchAdapter(int layout, Context context, ArrayList<ThemeData> list) {
        super();
        this.layout = layout;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);

        SearchAdapter.MyViewHolder viewHolder = new SearchAdapter.MyViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder myViewHolder, int position) {
        myViewHolder.txtView.setText(list.get(position).getTitle());

        Glide.with(context).load(list.get(position).getFirstImage()).into(myViewHolder.imgView);

        myViewHolder.itemView.setTag(position);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = (int) v.getTag();

                SearchAdapter.AsyncTaskClass asyncSub = new SearchAdapter.AsyncTaskClass();
                asyncSub.execute(position);

            }
        });
    }


    @Override
    public int getItemCount() {
        return list == null ? 0: list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        public ImageView imgView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.txtView);
            imgView = itemView.findViewById(R.id.imgView);
        }
    }

    class AsyncTaskClass extends android.os.AsyncTask<Integer, ThemeData, ThemeData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ThemeData doInBackground(Integer... integers) {

            int position = integers[0];

            ThemeData myThemeData1 = list.get(position);

            ThemeData themeData = getData(myThemeData1.getContentsID());


            return themeData;
        }

        @Override
        protected void onProgressUpdate(ThemeData... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ThemeData themeData) {
            super.onPostExecute(themeData);

            viewDialog = View.inflate(context, R.layout.dialog_info, null);

            TextView txt_Detail_title = viewDialog.findViewById(R.id.txt_Detail_title);
            TextView txt_Detail_addr = viewDialog.findViewById(R.id.txt_Detail_addr);
            TextView txt_Detail_info = viewDialog.findViewById(R.id.txt_Detail_info);

            ImageView img_Datail_info = viewDialog.findViewById(R.id.img_Datail_info);

            Glide.with(context).load(themeData.getFirstImage()).override(500, 300).into(img_Datail_info);


            txt_Detail_title.setText(themeData.getTitle());
            txt_Detail_addr.setText(themeData.getAddr());
            txt_Detail_info.setText(themeData.getOverView());

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            dialog.setTitle(" 정보");
            dialog.setView(viewDialog); // 이미지가 들어감

            dialog.setPositiveButton("닫기", null);

            dialog.show();


        }
    } // end of AsyncTaskClass

    private ThemeData getData(int contentID) {

        queue = Volley.newRequestQueue(context);

        String url = "http://api.visitkorea.or.kr/openapi/service/"
                + "rest/KorService/detailCommon?ServiceKey=" + KEY
                + "&contentId=" + contentID
                + "&firstImageYN=Y&mapinfoYN=Y&addrinfoYN=Y&defaultYN=Y&overviewYN=Y"
                + "&pageNo=1&MobileOS=AND&MobileApp="
                + appName + "&_type=json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject parse_response = (JSONObject) response.get("response");
                            JSONObject parse_body = (JSONObject) parse_response.get("body");
                            JSONObject parse_items = (JSONObject) parse_body.get("items");
                            JSONObject parse_itemlist = (JSONObject) parse_items.get("item");

                            //list.removeAll(list);

                            // detailThemeData = null;
                            detailThemeData.setFirstImage(parse_itemlist.getString("firstimage"));
                            detailThemeData.setTitle(parse_itemlist.getString("title"));
                            detailThemeData.setAddr(parse_itemlist.getString("addr1"));
                            detailThemeData.setOverView(parse_itemlist.getString("overview"));

                            //Toast.makeText(getActivity(), "봐야됨 "+ parse_itemlist.getString("addr1"), Toast.LENGTH_SHORT).show();

                            //list.add(detailThemeData);

                            // Log.d(TAG, " Two frg : "+detailThemeData.getTitle());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(jsObjRequest);
        // Log.d(TAG, "getDATA에서 : "+detailThemeData);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return detailThemeData;
    }
}
