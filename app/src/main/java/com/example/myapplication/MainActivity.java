package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);


        configureToolBar();

        searchButtonClick();
        openDrawerButton();
        configureDrawer();
        button1Click();
        button2Click();
        button3Click();
    }

    private void configureToolBar(){
        setSupportActionBar(binding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    private void openDrawerButton(){
        binding.drawerButton.setOnClickListener(view -> {
            binding.drawerLayout.open();
        });
    }

    private void searchButtonClick(){
        binding.searchButton.setOnClickListener(view -> {
            Toast searchButton = Toast.makeText(MainActivity.this, "search", Toast.LENGTH_SHORT);
            searchButton.show();
        });
    }

    private void configureDrawer(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_drawer, R.string.close_drawer);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void button1Click(){
        binding.button1.setOnClickListener(view -> {
            Toast button1 = Toast.makeText(MainActivity.this, "button1", Toast.LENGTH_SHORT);
            button1.show();
        });
    }

    private void button2Click(){
        binding.button2.setOnClickListener(view -> {
            Toast button1 = Toast.makeText(MainActivity.this, "button2", Toast.LENGTH_SHORT);
            button1.show();
        });
    }

    private void button3Click(){
        binding.button3.setOnClickListener(view -> {
            Toast button1 = Toast.makeText(MainActivity.this, "button3", Toast.LENGTH_SHORT);
            button1.show();
        });
    }

}