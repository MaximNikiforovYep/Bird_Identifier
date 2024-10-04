package com.example.birdidentifier;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.birdidentifier.adapters.ViewPagerAdapter;
import com.example.birdidentifier.databinding.ActivityMainBinding;

public class MainActivity extends FragmentActivity {
    ActivityMainBinding binding;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(1, false);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem != 1) {
                    viewPager.setCurrentItem(1, true);
                } else {
                    finish();
                }
            }
        });
    }
}