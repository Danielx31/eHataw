package com.danielx31.ehataw.firebase.firestore.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danielx31.ehataw.R;
import com.danielx31.ehataw.firebase.firestore.model.Food;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class FoodPagingAdapter extends FirestorePagingAdapter<Food, FoodPagingAdapter.FoodViewHolder> {

    private FoodPagingAdapter.OnItemClickListener onItemClickListener;

    public FoodPagingAdapter(@NonNull FirestorePagingOptions<Food> options) {
        super(options);
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
        Glide.with(holder.thumbnailImageView)
                .load(model.getThumbnailUrl())
                .into(holder.thumbnailImageView);

        holder.titleTextView.setText(model.getName());
        holder.nutritionFactsTextView.setText(String.join(", ", model.getNutritionFacts()));
    }

    public interface OnItemClickListener {

        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(FoodPagingAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnailImageView;
        private TextView titleTextView;
        private TextView nutritionFactsTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.diet_imageview_thumbnail);
            titleTextView = itemView.findViewById(R.id.diet_textview_title);
            nutritionFactsTextView = itemView.findViewById(R.id.diet_textview_nutritionfacts);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();

                    if (position == RecyclerView.NO_POSITION &&
                            onItemClickListener == null) {
                        return;
                    }

                    onItemClickListener.onItemClick(getItem(position), position);
                }
            });
        }



    }

}
