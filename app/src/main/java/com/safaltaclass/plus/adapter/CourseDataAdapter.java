package com.safaltaclass.plus.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.NotesActivity;
import com.safaltaclass.plus.R;
import com.safaltaclass.plus.VideoPlayerActivity;
import com.safaltaclass.plus.WebViewPlayerActivity;
import com.safaltaclass.plus.YoutubePlayerActivity;
import com.safaltaclass.plus.model.TopicData;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.RoundedCornersTransformation;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CourseDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnDialogButtonClickListener {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<TopicData> dataResults;
    private Context context;
    private boolean isLoadingAdded = false;
    private final OnItemClickListener listener;
    private int position;
//    private File file;

    public CourseDataAdapter(Context context,OnItemClickListener listener) {
        this.context = context;
        dataResults = new ArrayList<>();
        this.listener = listener;
    }

    public List<TopicData> getCourse() {
        return dataResults;
    }

    public void setCourse(List<TopicData> movieResults) {
        this.dataResults = movieResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress_bar, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.layout_course_data_item, parent, false);
        viewHolder = new SubjectVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case ITEM:
                final SubjectVH subjectVH = (SubjectVH) holder;
                subjectVH.tvCourse.setText(dataResults.get(position).getTitle());
                subjectVH.tvDate.setText( dataResults.get(position).getDate());
                subjectVH.tvDescription.setText(dataResults.get(position).getDescription());
                Glide.with(context).load(dataResults.get(position).getThumb())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .bitmapTransform(new RoundedCornersTransformation(context,10,0))
                        .into(subjectVH.ivCourse);
                subjectVH.ivDownload.setVisibility(View.GONE);
                subjectVH.ivDelete.setVisibility(View.GONE);
                subjectVH.ivResume.setVisibility(View.GONE);
                if(dataResults.get(position).getOffline_view().equals("1")) {
                    if(SafaltaPlusPreferences.getInstance().isDownloading().equals(dataResults.get(position).getUid())){
                        subjectVH.ivDownload.setVisibility(View.GONE);
                        subjectVH.ivDelete.setVisibility(View.VISIBLE);
                        subjectVH.ivResume.setVisibility(View.VISIBLE);
                        if(SafaltaPlusPreferences.getInstance().isStatus().equals(dataResults.get(position).getUid())) {
                            Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.blink);
                            subjectVH.ivResume.startAnimation(aniFade);
                            subjectVH.ivResume.setEnabled(false);
                        }else{
                            subjectVH.ivResume.setEnabled(true);
                        }
                    }else {
                        if (SafaltaPlusPreferences.getInstance().isDownloaded().equals(dataResults.get(position).getUid())) {
                            subjectVH.ivDownload.setVisibility(View.GONE);
                            subjectVH.ivDelete.setVisibility(View.VISIBLE);
                            subjectVH.ivResume.setVisibility(View.GONE);
                        } else {
                            subjectVH.ivDownload.setVisibility(View.VISIBLE);
                            subjectVH.ivDelete.setVisibility(View.GONE);
                            subjectVH.ivResume.setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case LOADING:
//                Do nothing
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataResults == null ? 0 : dataResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dataResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(TopicData data) {
        dataResults.add(data);
        notifyItemInserted(dataResults.size() - 1);
    }


    public void addAll(List<TopicData> moveResults) {
        for (TopicData result : moveResults) {
            add(result);
        }
    }

    public void remove(TopicData data) {
        int position = dataResults.indexOf(data);
        if (position > -1) {
            dataResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearAll(){
        dataResults.clear();
        notifyDataSetChanged();
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new TopicData());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = dataResults.size() - 1;
        TopicData result = getItem(position);

        if (result != null) {
            dataResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public TopicData getItem(int position) {
        return dataResults.get(position);
    }

    @Override
    public void onPositiveButtonClicked() {

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onErrorButtonClicked() {

    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class SubjectVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivCourse;
        private TextView tvCourse;
        private TextView tvDate;
        private TextView tvDescription;
        private ImageView ivDownload;
        private ImageView ivDelete;
        private ImageView ivResume;


        public SubjectVH(View itemView) {
            super(itemView);
            ivCourse = (ImageView) itemView.findViewById(R.id.iv_course);
            tvCourse = (TextView) itemView.findViewById(R.id.tv_course);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            ivDownload = (ImageView) itemView.findViewById(R.id.iv_download);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            ivResume = (ImageView) itemView.findViewById(R.id.iv_resume);


            itemView.setOnClickListener(this);
            ivDownload.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
            ivResume.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == ivDownload.getId()) {
                if(SafaltaPlusUtility.getInstance().isConnected(context)) {
                    if (SafaltaPlusPreferences.getInstance().isStatus().isEmpty()) {
                        Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.blink);
                        ivResume.startAnimation(aniFade);
                        ivDelete.setVisibility(View.VISIBLE);
                        ivDownload.setVisibility(View.GONE);
                        ivResume.setVisibility(View.VISIBLE);
                    }
                    listener.onItemClick(dataResults.get(getAdapterPosition()), getAdapterPosition(), false);
                }else{
                    Toast.makeText(context,"No Internet",Toast.LENGTH_LONG).show();
                }
            }else if(v.getId() == ivDelete.getId()){
                listener.onItemClick(dataResults.get(getAdapterPosition()),getAdapterPosition(),true);
            }else if(v.getId() == ivResume.getId()){
                if(SafaltaPlusUtility.getInstance().isConnected(context)) {
                    if (SafaltaPlusPreferences.getInstance().isStatus().isEmpty()) {
                        Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.blink);
                        ivResume.startAnimation(aniFade);
                        ivDelete.setVisibility(View.VISIBLE);
                    }
                    listener.onItemClick(dataResults.get(getAdapterPosition()), getAdapterPosition(), false);
                }else{
                    Toast.makeText(context,"No Internet",Toast.LENGTH_LONG).show();
                }
            } else{
                if (dataResults.get(getAdapterPosition()).getCategory() != null && dataResults.get(getAdapterPosition()).getCategory().equals("video")) {
                    if(dataResults.get(getAdapterPosition()).getContentformat() != null && dataResults.get(getAdapterPosition()).getContentformat().equals("youtube")){
                        Intent intent = new Intent(context, YoutubePlayerActivity.class);
                        intent.putExtra("date", dataResults.get(getAdapterPosition()).getDate());
                        intent.putExtra("category", dataResults.get(getAdapterPosition()).getCategory());
                        intent.putExtra("contentformat", dataResults.get(getAdapterPosition()).getContentformat());
                        intent.putExtra("thumb", dataResults.get(getAdapterPosition()).getThumb());
                        intent.putExtra("title", dataResults.get(getAdapterPosition()).getTitle());
                        intent.putExtra("description", dataResults.get(getAdapterPosition()).getDescription());
                        intent.putExtra("content", dataResults.get(getAdapterPosition()).getContent());
                        intent.putExtra("source", dataResults.get(getAdapterPosition()).getSource());
                        context.startActivity(intent);
                    }else if(dataResults.get(getAdapterPosition()).getContentformat() != null && dataResults.get(getAdapterPosition()).getContentformat().equals("webview")) {
                        Intent intent = new Intent(context, WebViewPlayerActivity.class);
                        intent.putExtra("date", dataResults.get(getAdapterPosition()).getDate());
                        intent.putExtra("category", dataResults.get(getAdapterPosition()).getCategory());
                        intent.putExtra("contentformat", dataResults.get(getAdapterPosition()).getContentformat());
                        intent.putExtra("thumb", dataResults.get(getAdapterPosition()).getThumb());
                        intent.putExtra("title", dataResults.get(getAdapterPosition()).getTitle());
                        intent.putExtra("description", dataResults.get(getAdapterPosition()).getDescription());
                        intent.putExtra("content", dataResults.get(getAdapterPosition()).getContent());
                        intent.putExtra("source", dataResults.get(getAdapterPosition()).getSource());
                        context.startActivity(intent);
                    }else{
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra("date", dataResults.get(getAdapterPosition()).getDate());
                        intent.putExtra("category", dataResults.get(getAdapterPosition()).getCategory());
                        intent.putExtra("contentformat", dataResults.get(getAdapterPosition()).getContentformat());
                        intent.putExtra("thumb", dataResults.get(getAdapterPosition()).getThumb());
                        intent.putExtra("title", dataResults.get(getAdapterPosition()).getTitle());
                        intent.putExtra("description", dataResults.get(getAdapterPosition()).getDescription());
                        intent.putExtra("content", dataResults.get(getAdapterPosition()).getContent());
                        intent.putExtra("source", dataResults.get(getAdapterPosition()).getSource());
                        intent.putExtra("uid", dataResults.get(getAdapterPosition()).getUid());
                        context.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(context, NotesActivity.class);
                    intent.putExtra("date", dataResults.get(getAdapterPosition()).getDate());
                    intent.putExtra("category", dataResults.get(getAdapterPosition()).getCategory());
                    intent.putExtra("contentformat", dataResults.get(getAdapterPosition()).getContentformat());
                    intent.putExtra("thumb", dataResults.get(getAdapterPosition()).getThumb());
                    intent.putExtra("title", dataResults.get(getAdapterPosition()).getTitle());
                    intent.putExtra("description", dataResults.get(getAdapterPosition()).getDescription());
                    intent.putExtra("content", dataResults.get(getAdapterPosition()).getContent());
                    intent.putExtra("source",dataResults.get(getAdapterPosition()).getSource());
                    context.startActivity(intent);
                }
            }
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TopicData item,int position,boolean value);
    }
}
