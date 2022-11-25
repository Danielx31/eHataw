package com.danielx31.ehataw;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {

    private Context mContext;
    private List<FoodData> myFoodList;

    public FoodAdapter(Context mContext, List<FoodData> myFoodList) {
        this.mContext = mContext;
        this.myFoodList = myFoodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet, parent, false);
        return new FoodViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {

        holder.imageView.setImageResource(myFoodList.get(position).getItemImage());
        holder.mTitle.setText(myFoodList.get(position).getItemName());
        holder.mDescription.setText(myFoodList.get(position).getItemPrice());
        //holder.mPrice.setText(myFoodList.get(position).getItemprice());
        holder.mPrice.setText("");

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailFoodActivity.class);
                intent.putExtra("Image", myFoodList.get(holder.getAbsoluteAdapterPosition()).getItemImage());
                intent.putExtra("Description", myFoodList.get(holder.getAbsoluteAdapterPosition()).getItemDescription());
                mContext.startActivity(intent);

            }
        });

//        holder.mCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext, DetailFoodFragment.class);
//                intent.putExtra("Image", myFoodList.get(holder.getAdapterPosition()).getItemImage());
//                intent.putExtra("Description", myFoodList.get(holder.getAdapterPosition()).getItemDescription());
//                mContext.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return myFoodList.size();
    }
}
class FoodViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView mTitle, mDescription, mPrice;
    CardView mCardView;

    public FoodViewHolder(@NonNull View itemView){
        super(itemView);

        imageView = itemView.findViewById(R.id.diet_imageview_thumbnail);
        mTitle = itemView.findViewById(R.id.diet_textview_title);
        mDescription = itemView.findViewById(R.id.diet_textview_nutritionfacts);

        mCardView = itemView.findViewById(R.id.myCardView);
    }
}
