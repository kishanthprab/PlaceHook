package com.example.kishanthprab.placehook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kishanthprab.placehook.R;
import com.example.kishanthprab.placehook.RecyclerAdapter;
import com.example.kishanthprab.placehook.RecyclerListItem;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;

    private List<RecyclerListItem> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_discover,container,false);

        recyclerView = (RecyclerView)fragmentView.findViewById(R.id.disc_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();

        for (int i =0;i<=10;i++){

            RecyclerListItem listItem = new RecyclerListItem(
                    "place name "+i,
                    4.2 +i,
                    i
            );
            list.add(listItem);

        }

        recyclerAdapter = new RecyclerAdapter(list,getActivity());
        recyclerView.setAdapter(recyclerAdapter);

        return fragmentView;
    }
}
