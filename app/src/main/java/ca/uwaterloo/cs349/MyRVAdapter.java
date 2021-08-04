package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.ViewHolder> {

    private ArrayList<Gesture> gestures;
    private LayoutInflater mInflater;
    private SharedViewModel sharedViewModel;
    private Context context;

    public MyRVAdapter(Context context, SharedViewModel sharedViewModel) {
        this.context = context;
        ;
        this.mInflater = LayoutInflater.from(context);
        this.sharedViewModel = sharedViewModel;
        this.gestures = sharedViewModel.getGestures().getValue();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_row, parent, false);
        return new ViewHolder(view);
    }

    // Add data to each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Gesture gesture = gestures.get(position);
        holder.myTextView.setText(gesture.name);
        holder.imageView.setImageBitmap(gesture.bitmap.bitmap);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.deleteGesture(position);
            }
        });
        holder.changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialogue
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Gesture");
                View viewInflated = LayoutInflater.from(context)
                        .inflate(R.layout.edit_gesture_dialogue, null, false);
                final EditText input = viewInflated.findViewById(R.id.gestureNameInput);
                final LinearLayout linearLayout = viewInflated.findViewById(R.id.editLayout);
                final MyDrawingView dv = new MyDrawingView(context, sharedViewModel, "nothing");
                linearLayout.addView(dv);

                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        if (!name.isEmpty() && dv.getPath() != null)
                            sharedViewModel.editGesture(position, name, dv);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return gestures.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myTextView;
        public ImageView imageView;
        public Button deleteBtn;
        public Button changeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textViewLib);
            imageView = itemView.findViewById(R.id.gesturePreviewLib);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            changeBtn = itemView.findViewById(R.id.changeBtn);
        }
    }
}