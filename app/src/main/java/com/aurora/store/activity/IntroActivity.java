/*
 * Aurora Store
 * Copyright (C) 2019, Rahul Kumar Patel <whyorean@gmail.com>
 *
 * Aurora Store is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Aurora Store is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Aurora Store.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.aurora.store.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aurora.store.R;
import com.aurora.store.adapter.CustomViewPagerAdapter;
import com.aurora.store.fragment.AccountsFragment;
import com.aurora.store.fragment.intro.PermissionFragment;
import com.aurora.store.fragment.intro.WelcomeFragment;
import com.aurora.store.utility.ThemeUtil;
import com.aurora.store.view.CustomViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    CustomViewPager mViewPager;


    private ThemeUtil mThemeUtil = new ThemeUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeUtil.onCreate(this);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThemeUtil.onResume(this);
    }

    private void init() {
        CustomViewPagerAdapter mViewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(0, new WelcomeFragment());
        mViewPagerAdapter.addFragment(1, new PermissionFragment());
        mViewPagerAdapter.addFragment(2, new AccountsFragment());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setPagingEnabled(false);
    }

    public void moveForward() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }
}