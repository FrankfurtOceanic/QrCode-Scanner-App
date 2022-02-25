package com.myApp.qrscannergenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.myApp.qrscannergenerator.R;

public class MainActivity extends AppCompatActivity {

    private  Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //seting up the action bar
        toolbar = findViewById(R.id.mytoolbar);
        //setSupportActionBar(toolbar);
        View navView = findViewById(R.id.nav_host_fragment);
        NavController navController = Navigation.findNavController(navView);

        AppBarConfiguration config = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, config);
    }
}