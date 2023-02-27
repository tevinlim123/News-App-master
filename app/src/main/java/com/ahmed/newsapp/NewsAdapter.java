package com.ahmed.newsapp;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    Context context;
    ArrayList<NewsModel> newsModels;
    NewsOnItemClickListener newsOnItemClickListener;
    public NewsAdapter(Context context , ArrayList<NewsModel> newsModels, NewsOnItemClickListener newsOnItemClickListener) {
        this.newsOnItemClickListener = newsOnItemClickListener;
        this.context = context;
        this.newsModels = newsModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.news_item , parent , false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        String titleString = "<b>" + "Title: " + "</b> " + newsModels.get(i).getTitle();
        viewHolder.news_title.setText(Html.fromHtml(titleString));
        String sectionString = "<b>" + "Section: " + "</b> " + newsModels.get(i).getSectionName();
        viewHolder.news_section.setText(Html.fromHtml(sectionString));
        String typeString = "<b>" + "Type: " + "</b> " + newsModels.get(i).getType();
        viewHolder.news_type.setText(Html.fromHtml(typeString));
        if(!newsModels.get(i).getAuthorName().isEmpty()) {
            String authorString = "<b>" + "By: " + "</b> " + newsModels.get(i).getAuthorName();
            viewHolder.author.setText(Html.fromHtml(authorString));
        }else{
            viewHolder.author.setVisibility(View.GONE);
        }

        viewHolder.news_date.setText(newsModels.get(i).getDate().substring(0 , 10));
        viewHolder.news_time.setText(newsModels.get(i).getDate().substring(11 ,19));

        Picasso.get()
                .load("https://place-hold.it/200x200/aaa/fff&bold&fontsize=25&text="+ newsModels.get(i).getSectionName().split("-" )[0])
                .placeholder(R.drawable.loading)
                .into(viewHolder.img_url);

        viewHolder.news_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsOnItemClickListener.onItemClick(newsModels.get(i));
            }});
        viewHolder.news_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(newsModels.get(i).getUrl())));
            }});
        viewHolder.news_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(newsModels.get(i).getUrl())));
            }});
        viewHolder.news_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(newsModels.get(i).getUrl())));
            }});
        viewHolder.news_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(newsModels.get(i).getUrl())));
            }});
        viewHolder.img_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(newsModels.get(i).getUrl())));
            }});
        viewHolder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(newsModels.get(i).getAuthorName())));
            }});
    }
    @Override
    public int getItemCount() {
        return newsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView news_title;
        TextView news_type;
        TextView news_section;
        TextView news_date;
        TextView news_time;
        ImageView img_url;
        TextView author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            news_title = itemView.findViewById(R.id.news_title);
            news_type = itemView.findViewById(R.id.news_type);
            news_section = itemView.findViewById(R.id.news_section);
            news_date = itemView.findViewById(R.id.news_date);
            news_time = itemView.findViewById(R.id.news_time);
            img_url = itemView.findViewById(R.id.news_img);
            author = itemView.findViewById(R.id.author_name);
        }
    }
}









