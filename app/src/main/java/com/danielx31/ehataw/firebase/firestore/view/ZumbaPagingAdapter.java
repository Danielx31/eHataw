package com.danielx31.ehataw.firebase.firestore.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danielx31.ehataw.R;
import com.danielx31.ehataw.ZumbaAdapter;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ZumbaPagingAdapter extends FirestorePagingAdapter<Zumba, ZumbaPagingAdapter.ZumbaViewHolder> {

    private OnItemClickListener onItemClickListener;

    public ZumbaPagingAdapter(@NonNull FirestorePagingOptions<Zumba> options) {
        super(options);
    }

    @NonNull
    @Override
    public ZumbaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zumba,
                parent, false);
        return new ZumbaViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ZumbaViewHolder holder, int position, @NonNull Zumba model) {
        Glide.with(holder.imageView)
                .load(model.getThumbnailUrl())
                .into(holder.imageView);

        holder.titleTextView.setText(model.getTitle());
        holder.textTextView.setText(model.getCategory());
    }

    class ZumbaViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleTextView;
        private TextView textTextView;
        private ImageButton popupMenuImageButton;

        public ZumbaViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageview_thumbnail);
            this.titleTextView = itemView.findViewById(R.id.textview_title);
            this.textTextView = itemView.findViewById(R.id.textview_text);
            this.popupMenuImageButton = itemView.findViewById(R.id.imagebutton_popupmenu);

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

            popupMenuImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();

                    if (position == RecyclerView.NO_POSITION &&
                            onItemClickListener == null) {
                        return;
                    }

                    onItemClickListener.onPopupMenuClick(view, getItem(position), position);
                }
            });

        }
    }

    public interface OnItemClickListener {

        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onPopupMenuClick(View view, DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
