package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {

    private Context mContext;
    private List<Food> foodList;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Food food);
    }

    public FoodAdapter(Context context, List<Food> list, OnDeleteClickListener listener) {
        super(context, 0, list);
        mContext = context;
        foodList = list;
        deleteClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Food foodItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.food_item, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);
        TextView textViewCreateAt = convertView.findViewById(R.id.textViewCreateAt);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        textViewName.setText(foodItem.getName());
        textViewPrice.setText(String.format("$%.2f", foodItem.getPrice()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(foodItem.getCreateAt());
        textViewCreateAt.setText(formattedDate);

        buttonDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(foodItem);
            }
        });

        return convertView;
    }
}
