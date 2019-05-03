package com.ssimo.remind;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class Notes extends Fragment {

    View v;
    private RecyclerView myRecycleView;
    private ArrayList<String> memo_texts = new ArrayList<>();
    private ArrayList<String> memo_images = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_notes, container, false);
        myRecycleView = v.findViewById(R.id.my_recycler_view);


        for(int i = 0; i < 30; i++){
            memo_texts.add("memo text value is : " + i);
            memo_images.add("https://picsum.photos/200/200/?image="+i);
        }

        // specify an adapter (see also next example)
        MyAdapter mAdapter = new MyAdapter(getContext(), memo_texts, memo_images);
        myRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecycleView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
