package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    private SharedViewModel mViewModel;
    MyRVAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_library, container, false);

        // set up the RecyclerView
        final RecyclerView recyclerView = root.findViewById(R.id.rvLib);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        adapter = new MyRVAdapter(root.getContext(), mViewModel);
        recyclerView.setAdapter(adapter);

        mViewModel.getGestures().observe(getViewLifecycleOwner(), new Observer<ArrayList<Gesture>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Gesture> gestures) {
                adapter = new MyRVAdapter(root.getContext(), mViewModel);
                recyclerView.setAdapter(adapter);
            }
        });
        return root;
    }
}