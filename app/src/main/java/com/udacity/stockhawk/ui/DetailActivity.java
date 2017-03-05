package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.adapters.DetailPagerAdapter;
import com.udacity.stockhawk.data.StockParcelable;

import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("stock_information")) {
            StockParcelable stockParcelable = intent.getParcelableExtra("stock_information");
            DetailPagerAdapter detailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), this, stockParcelable);

            ViewPager viewPager = (ViewPager) this.findViewById(R.id.pager_container);
            viewPager.setAdapter(detailPagerAdapter);

            TabLayout tabLayout = (TabLayout) this.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

        }
    }
}
