package com.example.crimehotspotapp.ViewHolder;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.crimehotspotapp.Interface.ItemClickListener;
import com.example.crimehotspotapp.R;

public class ReportViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

private ItemClickListener itemClickListener;
public TextView Crime,City,Province,Code;

    public ReportViewHolder(@NonNull View itemView) {
        super(itemView);
        Crime = itemView.findViewById(R.id.Crime);
        City = itemView.findViewById(R.id.City);
        Province = itemView.findViewById(R.id.Province);
        Code = itemView.findViewById(R.id.Code);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        }

@Override
public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
        }
}
