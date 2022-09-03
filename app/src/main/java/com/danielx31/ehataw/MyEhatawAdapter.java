package com.danielx31.ehataw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyEhatawAdapter extends RecyclerView.Adapter<MyEhatawAdapter.ViewHolder>{

    MyEhatawData[] myEhatawData;
    Context context;

    public MyEhatawAdapter(MyEhatawData[] myEhatawData, HomeActivity activity) {
        this.myEhatawData = myEhatawData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.zumba_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MyEhatawData myEhatawDataList = myEhatawData[position];
        holder.textViewName.setText(myEhatawDataList.getZumbaName());
        holder.textViewDescription.setText(myEhatawDataList.getZumbaDescription());
        holder.zumbaImage.setImageResource(myEhatawDataList.getZumbaImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, myEhatawDataList.getZumbaName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myEhatawData.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView zumbaImage;
        TextView textViewName;
        TextView textViewDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            zumbaImage = itemView.findViewById(R.id.imageview_logo);
            textViewName = itemView.findViewById(R.id.textName);
            textViewDescription = itemView.findViewById(R.id.textdescription);
        }
    }
}
