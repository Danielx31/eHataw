package com.danielx31.ehataw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danielx31.ehataw.firebase.model.Zumba;

import java.util.List;

public class ZumbaAdapter extends RecyclerView.Adapter<ZumbaAdapter.ZumbaViewHolder> {

    private List<Zumba> zumbaList;
    private OnItemClickListener onItemClicklistener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onPopupMenuImageButtonClick(View view, int position);
    }

    public void setOnItemClicklistener(OnItemClickListener onItemClicklistener) {
        this.onItemClicklistener = onItemClicklistener;
    }

    public static class ZumbaViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleTextView;
        private TextView textTextView;
        private ImageButton popupMenuImageButton;

        public ZumbaViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageview_thumbnail);
            this.titleTextView = itemView.findViewById(R.id.textview_title);
            this.textTextView = itemView.findViewById(R.id.textview_text);
            this.popupMenuImageButton = itemView.findViewById(R.id.imagebutton_popupmenu);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener == null) {
                        return;
                    }

                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return;
                    }

                    onItemClickListener.onItemClick(position);
                }
            });

            popupMenuImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener == null) {
                        return;
                    }

                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return;
                    }

                    onItemClickListener.onPopupMenuImageButtonClick(v, position);
                }
            });

        }
    }

    public ZumbaAdapter(List<Zumba> zumbaList) {
        this.zumbaList = zumbaList;
    }

    @NonNull
    @Override
    public ZumbaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zumba, parent, false);
        ZumbaViewHolder zumbaViewHolder = new ZumbaViewHolder(view, onItemClicklistener);
        return zumbaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ZumbaViewHolder holder, int position) {
        Zumba zumba = zumbaList.get(position);

        Glide.with(holder.imageView)
                .load(zumba.getVideoThumbnailUrl())
                .into(holder.imageView);

        holder.titleTextView.setText(zumba.getTitle());
        holder.textTextView.setText(zumba.getDescription());
    }

    public int getItemCount() {
        return zumbaList.size();
    }
}
