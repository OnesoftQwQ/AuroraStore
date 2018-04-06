package in.dragons.galaxy.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.percolate.caffeine.ViewUtils;

import java.util.List;

import in.dragons.galaxy.AppListIterator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.ScrollEdgeListener;
import in.dragons.galaxy.adapters.AppListAdapter;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.playstore.EndlessScrollTask;
import in.dragons.galaxy.view.ListItem;
import in.dragons.galaxy.view.ProgressIndicator;
import in.dragons.galaxy.view.SearchResultAppBadge;

abstract public class EndlessScrollActivity extends AppListActivity {

    protected AppListIterator iterator;
    public GooglePlayAPI.SUBCATEGORY subCategory;

    abstract protected EndlessScrollTask getTask();

    public void setIterator(AppListIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity);

        FrameLayout contentFrameLayout = ViewUtils.findViewById(this, R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_endless_inc, contentFrameLayout);

        //Defaults to TOP_FREE
        subCategory = GooglePlayAPI.SUBCATEGORY.TOP_FREE;

        setupListView();
        setupTabs();

        onNewIntent(getIntent());

        getListView().setOnScrollListener(new ScrollEdgeListener() {

            @Override
            protected void loadMore() {
                loadApps(subCategory);
            }
        });

        getListView().setOnItemClickListener((parent, view, position, id) -> {
            DetailsActivity.app = getAppByListPosition(position);
            startActivity(DetailsActivity.getDetailsIntent(EndlessScrollActivity.this, DetailsActivity.app.getPackageName()));
        });

        registerForContextMenu(getListView());
    }

    protected void setupTabs() {
        TabLayout categoryTabs = ViewUtils.findViewById(this, R.id.category_tabs);
        categoryTabs.setVisibility(View.VISIBLE);
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.category_topFree));
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.category_topGrossing));
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.category_trending));

        categoryTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        clearApps();
                        loadApps(GooglePlayAPI.SUBCATEGORY.TOP_FREE);
                        break;
                    case 1:
                        clearApps();
                        loadApps(GooglePlayAPI.SUBCATEGORY.TOP_GROSSING);
                        break;
                    case 2:
                        clearApps();
                        loadApps(GooglePlayAPI.SUBCATEGORY.MOVERS_SHAKERS);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected ListItem getListItem(App app) {
        SearchResultAppBadge appBadge = new SearchResultAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    public void addApps(List<App> appsToAdd) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        if (!adapter.isEmpty()) {
            ListItem last = adapter.getItem(adapter.getCount() - 1);
            if (last instanceof ProgressIndicator) {
                adapter.remove(last);
            }
        }
        super.addApps(appsToAdd, false);
        if (!appsToAdd.isEmpty()) {
            adapter.add(new ProgressIndicator());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearApps() {
        super.clearApps();
        iterator = null;
    }

    protected EndlessScrollTask prepareTask(EndlessScrollTask task, GooglePlayAPI.SUBCATEGORY subcategory) {
        task.setContext(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setSubCategory(subcategory);
        if (listItems.isEmpty())
            task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    public void loadApps(GooglePlayAPI.SUBCATEGORY subcategory) {
        prepareTask(getTask(), subcategory).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(true);
        menu.findItem(R.id.filter_apps_with_ads).setVisible(true);
        menu.findItem(R.id.filter_paid_apps).setVisible(true);
        menu.findItem(R.id.filter_gsf_dependent_apps).setVisible(true);
        menu.findItem(R.id.filter_rating).setVisible(true);
        menu.findItem(R.id.filter_downloads).setVisible(true);
        return result;
    }
}