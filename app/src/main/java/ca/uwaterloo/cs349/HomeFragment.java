package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {

    private SharedViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        View dv = new MyDrawingView(container.getContext(), mViewModel, "recognize");
        LinearLayout layout = root.findViewById(R.id.viewHome);
        layout.addView(dv);

        // Top3 layout
        final TextView tv0 = root.findViewById(R.id.textView0);
        final TextView tv1 = root.findViewById(R.id.textView1);
        final TextView tv2 = root.findViewById(R.id.textView2);
        final ImageView iv0 = root.findViewById(R.id.view0);
        final ImageView iv1 = root.findViewById(R.id.view1);
        final ImageView iv2 = root.findViewById(R.id.view2);
        mViewModel.getTop3().observe(getViewLifecycleOwner(), new Observer<Gesture[]>() {
            @Override
            public void onChanged(@Nullable Gesture[] gestures) {
                if (gestures[0] != null) {
                    tv0.setText("1: " + gestures[0].name);
                    iv0.setImageBitmap(gestures[0].bitmap.bitmap);
                }

                if (gestures[1] != null) {
                    tv1.setText("2: " + gestures[1].name);
                    iv1.setImageBitmap(gestures[1].bitmap.bitmap);
                }

                if (gestures[2] != null) {
                    tv2.setText("3: " + gestures[2].name);
                    iv2.setImageBitmap(gestures[2].bitmap.bitmap);
                }
            }
        });
        return root;
    }
}