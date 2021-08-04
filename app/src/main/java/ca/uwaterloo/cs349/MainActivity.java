package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SharedViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_library, R.id.navigation_addition)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        mViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("yo", "read" + getFilesDir());
        readGestures();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d("yo", "write");
        saveGestures();
    }

    private void readGestures() {
        try {
            File file = new File(getFilesDir() + "/MyFile.ser");
            if (file.exists()) {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                ArrayList<Gesture> gestures = (ArrayList<Gesture>) in.readObject();
                TimeUnit.MILLISECONDS.sleep(500);
                mViewModel.setGestures(gestures);
                in.close();
                fileIn.close();
            }
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveGestures() {
        ArrayList<Gesture> gestures = mViewModel.getGestures().getValue();
        try {
            FileOutputStream fileOut = new FileOutputStream(getFilesDir() + "/MyFile.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gestures);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}