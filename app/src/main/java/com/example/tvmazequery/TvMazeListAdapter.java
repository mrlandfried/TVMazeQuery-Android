package com.example.tvmazequery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class TvMazeListAdapter extends RecyclerView.Adapter<TvMazeListAdapter.TvMazeListAdapterViewHolder> {

    JSONArray jsonArray;
    Context context;

    public TvMazeListAdapter(String result){

        try {
            jsonArray = new JSONArray(result);
            //Log.d("DEBUG_TVMAZE", jsonArray.length() +"");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public TvMazeListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        int layoutIdForListItem = R.layout.tvmaze_list_item;
        LayoutInflater inflator = LayoutInflater.from(context);
        boolean shouldAttachToLayoutImmediately = false;

        View view = inflator.inflate(layoutIdForListItem, parent, shouldAttachToLayoutImmediately);
        TvMazeListAdapterViewHolder viewHolder = new TvMazeListAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TvMazeListAdapterViewHolder holder, int position) {
        try{
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            //Log.d("DEBUG_TVMAZE", jsonObject + "");
            JSONObject show = jsonObject.getJSONObject("show");
            //Log.d("DEBUG_TVMAZE", show + "");
            final String url = show.getString("url");
            //Log.d("DEBUG_TVMAZE", url);
            String name = show.getString("name");
            holder.tv_media_name.setText(name);
            String summary = show.getString("summary");
            holder.tv_media_summary.setText(summary);
            JSONObject image = show.getJSONObject("image");
            //Log.d("DEBUG_TVMAZE", image + "");
            String mImage = image.getString("medium");
            Log.d("DEBUG_TVMAZE", mImage);
            JSONObject externals = show.getJSONObject("externals");
            final String imdb = externals.getString("imdb");

            holder.iv_tvmaze_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                }
            });

            holder.iv_imdb_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.imdb.com/title/" + imdb));
                    context.startActivity(intent);
                }
            });

            new DownloadImageTask(holder.iv_poster_art).execute(mImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class TvMazeListAdapterViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_poster_art, iv_tvmaze_logo, iv_imdb_logo;
        TextView tv_media_name, tv_media_summary;


        public TvMazeListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_poster_art = itemView.findViewById(R.id.iv_poster_art);
            iv_tvmaze_logo = itemView.findViewById(R.id.iv_tvmaze_logo);
            iv_imdb_logo = itemView.findViewById(R.id.iv_imdb_logo);
            tv_media_name = itemView.findViewById(R.id.tv_media_name);
            tv_media_summary = itemView.findViewById(R.id.tv_media_summary);

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView poster;

        public DownloadImageTask(ImageView poster){
            this.poster = poster;
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap image = null;

            try {
                InputStream in = new URL(url).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            poster.setImageBitmap(bitmap);
        }
    }


}
