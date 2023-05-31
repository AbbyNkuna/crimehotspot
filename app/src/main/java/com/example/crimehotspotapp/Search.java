package com.example.crimehotspotapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.crimehotspotapp.Interface.ItemClickListener;
import com.example.crimehotspotapp.Model.Report;
import com.example.crimehotspotapp.ViewHolder.ReportViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class Search extends AppCompatActivity {
    RecyclerView recyclerView;

    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Report, ReportViewHolder> adapter;
    EditText searchView;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Report");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
          searchView = findViewById(R.id.Search);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                insertValues(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insertValues(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                insertValues(s.toString());
            }
        });
    }
    private void insertValues(String key) {
        adapter = new FirebaseRecyclerAdapter<Report, ReportViewHolder>(Report.class,R.layout.reportlayout,ReportViewHolder.class,reference.orderByChild("city").startAt(key).endAt(key+"\uf8ff")) {
            @Override
            protected void populateViewHolder(ReportViewHolder viewHolder, Report model, int position) {
                viewHolder.Crime.setText("Crime Reported : "+model.getCrime());
                viewHolder.Code.setText("Postal Code : "+ model.getCode());
                viewHolder.City.setText("City :"+model.getCity());
                viewHolder.Province.setText("Province : " + model.getProvince());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int Position, Boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void init() {

        recyclerView = findViewById(R.id.RecyclerItems);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
}