/*
 * Aurora Store
 * Copyright (C) 2018  Rahul Kumar Patel <whyorean@gmail.com>
 *
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * Aurora Store (a fork of Yalp Store )is free software: you can redistribute it and/or modify
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
 */

package com.dragons.aurora.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.GridAutoFitLayoutManager;
import com.dragons.aurora.R;
import com.dragons.aurora.Util;
import com.dragons.aurora.adapters.AllCategoriesAdapter;
import com.dragons.aurora.adapters.TopCategoriesAdapter;
import com.dragons.aurora.helpers.Accountant;
import com.dragons.aurora.task.playstore.CategoryListTask;
import com.percolate.caffeine.ViewUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.dragons.aurora.Util.hide;
import static com.dragons.aurora.Util.show;


public class CategoryListFragment extends CategoryListTask {

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Disposable loadApps;
    private CategoryManager categoryManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryManager = new CategoryManager(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            if ((ViewGroup) view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }
        view = inflater.inflate(R.layout.fragment_categories, container, false);
        if (Accountant.isLoggedIn(getContext()) && categoryManager.categoryListEmpty())
            getAllCategories();
        else {
            setupAllCategories();
            setupTopCategories();
        }

        swipeRefreshLayout = ViewUtils.findViewById(view, R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Accountant.isLoggedIn(getContext()) && Util.isConnected(getActivity()))
                getAllCategories();
            else swipeRefreshLayout.setRefreshing(false);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Accountant.isLoggedIn(getContext()) && categoryManager.categoryListEmpty())
            getAllCategories();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setupTopCategories() {
        RecyclerView recyclerView = ViewUtils.findViewById(view, R.id.top_cat_view);
        recyclerView.setAdapter(new TopCategoriesAdapter(this, getResources().getStringArray(R.array.topCategories)));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.anim_falldown));
    }

    private void setupAllCategories() {
        show(view, R.id.all_cat_view);
        RecyclerView recyclerView = ViewUtils.findViewById(view, R.id.all_cat_view);
        recyclerView.setAdapter(new AllCategoriesAdapter(this, categoryManager.getCategoriesFromSharedPreferences()));
        recyclerView.setLayoutManager(new GridAutoFitLayoutManager(getContext(), 150));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.anim_falldown));
    }

    private void getAllCategories() {
        hide(view, R.id.all_cat_view);
        loadApps = Observable.fromCallable(() -> getResult(getContext()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (result) {
                        if (view != null) {
                            setupTopCategories();
                            setupAllCategories();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, this::processException);
    }
}
