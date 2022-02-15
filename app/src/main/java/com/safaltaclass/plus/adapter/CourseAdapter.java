package com.safaltaclass.plus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.safaltaclass.plus.R;
import com.safaltaclass.plus.SubCoursesActivity;
import com.safaltaclass.plus.SubjectActivity;
import com.safaltaclass.plus.model.CourseListData;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context mContext;
    private List<CourseListData> mCourseList;


    public CourseAdapter(Context context, List<CourseListData> courseList) {
        mContext = context;
        mCourseList = courseList;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_course_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        holder.tvCourse.setText(mCourseList.get(position).getTitle());
     //   holder.tvDate.setText( mCourseList.get(position).getDate());
     //   holder.tvDescription.setText(mCourseList.get(position).getDescription());

     /*   Glide.with(mContext).load(mCourseList.get(position).getThumb())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivCourse);*/
    }

    @Override
    public int getItemCount() {
        return mCourseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        //   private ImageView ivCourse;
        private TextView tvCourse;
      //  private TextView tvDate;
      //  private TextView tvDescription;
        //  private Button btnOpen;


        public CourseViewHolder(View itemView) {
            super(itemView);
            //     ivCourse = (ImageView)itemView.findViewById(R.id.iv_course);
            tvCourse = (TextView) itemView.findViewById(R.id.tv_course);
        //    tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        //    tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            //    btnOpen = (Button)itemView.findViewById(R.id.btn_open);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCourseList.get(getAdapterPosition()).getParentcode().isEmpty()){
                        Intent intent = new Intent(mContext, SubjectActivity.class);
                        intent.putExtra("courseId", mCourseList.get(getAdapterPosition()).getCode());
                        intent.putExtra("courseTitle", mCourseList.get(getAdapterPosition()).getTitle());
                        mContext.startActivity(intent);
                    }else{
                        Intent intent = new Intent(mContext, SubCoursesActivity.class);
                        intent.putExtra("courseId", mCourseList.get(getAdapterPosition()).getCode());
                        intent.putExtra("courseTitle", mCourseList.get(getAdapterPosition()).getTitle());
                        intent.putExtra("parentCode", mCourseList.get(getAdapterPosition()).getParentcode());
                        intent.putExtra("nextPageTitle", mCourseList.get(getAdapterPosition()).getNextPageTitle());
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }
}
