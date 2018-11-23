package com.example.android.bakersapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.bakersapp.R;
import com.example.android.bakersapp.models.Step;

import java.util.ArrayList;

public class StepListRecyclerAdapter
        extends RecyclerView.Adapter<StepListRecyclerAdapter.StepsViewHolder> {

    private final ListItemClickListener mStepOnClickListener;
    private ArrayList<Step> dataList;
    private Context context;

    public StepListRecyclerAdapter(ListItemClickListener listener) {
        mStepOnClickListener = listener;
    }

    public void setStepData(ArrayList<Step> stepData, Context contextIn) {
        dataList = stepData;
        context = contextIn;
        notifyDataSetChanged();
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.rvi_step_list_item, viewGroup, false);
        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {

        Integer stepId = dataList.get(position).getId();
        String stepNum = (stepId < 1) ? "" :
                context.getResources().getString(R.string.step_header) + " " + stepId.toString();
        String shortDescription = dataList.get(position).getShortDescription();
        String thumbnailUrl = dataList.get(position).getThumbnailURL();

        holder.mStepNumberTextView.setText(stepNum);
        holder.mStepsShortDescriptionTextView.setText(shortDescription);

        Context context = holder.mStepThumbnailImageView.getContext();
        Glide.with(context).load(thumbnailUrl).asBitmap()
                .placeholder(R.drawable.vg_spoon).into(holder.mStepThumbnailImageView);

    }

    @Override
    public int getItemCount() {
        return (dataList == null) ? 0 : dataList.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public class StepsViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView mStepNumberTextView;
        private final TextView mStepsShortDescriptionTextView;
        private final ImageView mStepThumbnailImageView;

        private StepsViewHolder(View itemView) {
            super(itemView);

            mStepNumberTextView = (TextView) itemView.findViewById(R.id.tv_step_number);
            mStepsShortDescriptionTextView = (TextView) itemView.findViewById(R.id.tv_step_short_description);
            mStepThumbnailImageView = (ImageView) itemView.findViewById(R.id.iv_step_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mStepOnClickListener.onListItemClick(clickedPosition);
        }
    }

}
