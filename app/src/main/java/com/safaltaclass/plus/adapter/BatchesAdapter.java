package com.safaltaclass.plus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safaltaclass.plus.R;
import com.safaltaclass.plus.model.BatchData;

import java.util.List;

public class BatchesAdapter extends RecyclerView.Adapter<BatchesAdapter.BatchViewHolder> {

    private Context mContext;
    private List<BatchData> mBatchList;

    public BatchesAdapter(Context context, List<BatchData> batchList) {
        mContext = context;
        mBatchList = batchList;
    }

    @NonNull
    @Override
    public BatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BatchViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_batch, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BatchViewHolder holder, int position) {
        holder.tvBatchCode.setText(mBatchList.get(position).getBatchcode());
        holder.tvBatchName.setText(mBatchList.get(position).getBatchname());
        holder.tvBatchCourse.setText(mBatchList.get(position).getCoursename());
        if (!mBatchList.get(position).getSubjectname().isEmpty()) {
            holder.tvBatchSubject.setText(mBatchList.get(position).getSubjectname());
            holder.tvBatchSubject.setVisibility(View.VISIBLE);
        }
        if (!mBatchList.get(position).getDowschedule().isEmpty()) {
            holder.tvBatchSchedule.setText(mBatchList.get(position).getDowschedule());
            holder.tvBatchSchedule.setVisibility(View.VISIBLE);
        }
        holder.tvBatchStart.setText(mBatchList.get(position).getDatestart());
        holder.tvBatchEnd.setText(mBatchList.get(position).getDateend());
        holder.tvBatchMode.setText(mBatchList.get(position).getDeliverymode());
    }

    @Override
    public int getItemCount() {
        return mBatchList.size();
    }

    public class BatchViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBatchCode;
        private TextView tvBatchName;
        private TextView tvBatchCourse;
        private TextView tvBatchSubject;
        private TextView tvBatchSchedule;
        private TextView tvBatchStart;
        private TextView tvBatchEnd;
        private TextView tvBatchMode;

        public BatchViewHolder(View itemView) {
            super(itemView);

            tvBatchCode = (TextView) itemView.findViewById(R.id.tv_batch_code);
            tvBatchName = (TextView) itemView.findViewById(R.id.tv_batch_name);
            tvBatchCourse = (TextView) itemView.findViewById(R.id.tv_course_value);
            tvBatchSubject = (TextView) itemView.findViewById(R.id.tv_subject_value);
            tvBatchSchedule = (TextView) itemView.findViewById(R.id.tv_batch_schedule);
            tvBatchStart = (TextView) itemView.findViewById(R.id.tv_start_date);
            tvBatchEnd = (TextView) itemView.findViewById(R.id.tv_end_date);
            tvBatchMode = (TextView) itemView.findViewById(R.id.tv_mode_value);
        }
    }
}