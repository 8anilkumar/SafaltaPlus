package com.safaltaclass.plus.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.safaltaclass.plus.AboutUsActivity;
import com.safaltaclass.plus.BatchesActivity;
import com.safaltaclass.plus.CentreActivity;
import com.safaltaclass.plus.FeedbackActivity;
import com.safaltaclass.plus.R;
import com.safaltaclass.plus.WebViewActivity;
import com.safaltaclass.plus.model.PosterImage;
import com.safaltaclass.plus.utility.CircleTransform;

import java.util.List;

public class PosterAdapter  extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    private Context mContext;
    private List<PosterImage> mPosterList;


    public PosterAdapter(Context context, List<PosterImage> posterList) {
        mContext = context;
        mPosterList = posterList;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PosterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_poster_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
       /* holder.tvTitle.setText(mPosterList.get(position).getTitle());
        holder.tvDate.setText(mPosterList.get(position).getDate());
        Glide.with(mContext).load(mPosterList.get(position).getThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CircleTransform(mContext))
                .fitCenter()
                .into(holder.ivPosterLogo);*/
        Glide.with(mContext).load(mPosterList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivPoster);
    }

    @Override
    public int getItemCount() {
        return mPosterList.size();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPoster;
     //   private ImageView ivPosterLogo;
     //   private TextView tvTitle;
     //   private TextView tvDate;


        public PosterViewHolder(View itemView) {
            super(itemView);
            ivPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
       //     ivPosterLogo = (ImageView) itemView.findViewById(R.id.iv_circle);
       //     tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
       //     tvDate = (TextView)itemView.findViewById(R.id.tv_date);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPosterList.get(getAdapterPosition()).getUrl() != null) {
                        if (mPosterList.get(getAdapterPosition()).getUrl().contains("https")) {
                            if (mPosterList.get(getAdapterPosition()).getAction() != null && mPosterList.get(getAdapterPosition()).getAction().equals("webview")) {
                                    Intent intent = new Intent(mContext, WebViewActivity.class);
                                    intent.putExtra("url", mPosterList.get(getAdapterPosition()).getUrl());
                                    mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPosterList.get(getAdapterPosition()).getUrl()));
                                mContext.startActivity(intent);
                            }
                        } else {
                            if (mPosterList.get(getAdapterPosition()).getAction() != null) {
                                String caseKey = mPosterList.get(getAdapterPosition()).getAction();
                                switch (caseKey) {
                                    case "AboutUs":
                                        Intent aboutIntent = new Intent(mContext, AboutUsActivity.class);
                                        mContext.startActivity(aboutIntent);
                                        break;
                                    case "Centres":
                                        Intent centresIntent = new Intent(mContext, CentreActivity.class);
                                        mContext.startActivity(centresIntent);
                                        break;
                                    case "Support":
                                        Intent supportIntent = new Intent(mContext, FeedbackActivity.class);
                                        mContext.startActivity(supportIntent);
                                        break;
                                    case "Batches":
                                        Intent batchesIntent = new Intent(mContext, BatchesActivity.class);
                                        mContext.startActivity(batchesIntent);
                                        break;
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}
