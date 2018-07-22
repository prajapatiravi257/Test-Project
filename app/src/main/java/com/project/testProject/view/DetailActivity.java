package com.project.testProject.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.materialize.MaterializeBuilder;
import com.project.testProject.R;
import com.project.testProject.items.AddressItems;
import com.project.testProject.items.UsersItems;
import com.project.testProject.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    // save our FastAdapter
    private FastItemAdapter<AddressItems> adapter;
    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String TAG = DetailActivity.class.getSimpleName();
    private List<UsersItems> list;
    private String jsonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        Intent intent = getIntent();
        jsonList = intent.getStringExtra(UsersActivity.selectedItems);

        initViews();

        // restore selections (this has to be done after the items were added
        adapter.withSavedInstanceState(savedInstanceState);
    }

    private void initViews() {

        // style our ui
        new MaterializeBuilder().withActivity(this).build();

        // create our FastAdapter which will manage everything
        adapter = new FastItemAdapter<>();
        // get our recyclerView and do basic setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        try {
            JSONArray array = new JSONArray(jsonList);
            List<AddressItems> items = new ArrayList<>();
            for (int i = 0; i <= array.length() - 1; i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String address = jsonObject.optString("fullAddress");
                JSONObject geo = jsonObject.optJSONObject("geo");
                String lat = geo.optString("lat");
                String lng = geo.optString("lng");
                items.add(new AddressItems().withFullAddress(address).withLat(lat).withLng(lng));
            }
            adapter.add(items);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // add the values which need to be saved from the adapter to the bundle
        outState = adapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
