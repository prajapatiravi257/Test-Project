package com.project.testProject.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.materialize.MaterializeBuilder;
import com.project.testProject.R;
import com.project.testProject.items.UsersItems;
import com.project.testProject.model.Address;
import com.project.testProject.model.Geo;
import com.project.testProject.model.Response;
import com.project.testProject.network.ApiClient;
import com.project.testProject.network.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


public class UsersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static String selectedItems = "selectedItems";
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.show_details)
    TextView detail;
    List<Response> usersList;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    // save our FastAdapter
    private FastItemAdapter<UsersItems> adapter;
    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String TAG = UsersActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_users);

        ButterKnife.bind(this);
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        initViews();

        // restore selections (this has to be done after the items were added
        adapter.withSavedInstanceState(savedInstanceState);
    }

    private void initViews() {

        // style our ui
        new MaterializeBuilder().withActivity(this).build();

        usersList = new ArrayList<>();
        // create our FastAdapter which will manage everything
        adapter = new FastItemAdapter<>();
        adapter.withSelectable(true);

        // configure our fastAdapter
        adapter.withOnClickListener(
                (v, adapter, item, position) -> {

                    return false;
                });

        adapter.withOnPreClickListener(
                (v, adapter, item, position) -> {
                    // consume otherwise radio/checkbox will be deselected
                    return true;
                });
        adapter.withEventHook(new UsersItems.CheckBoxClickEvent());

        // get our recyclerView and do basic setup
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.postDelayed(() -> getUsers(false), 2000);

        getUsers(true);

    }

    @SuppressLint("CheckResult")
    private void getUsers(boolean show) {

        MaterialDialog progress = new MaterialDialog.Builder(this)
                .title("Working...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();
        if (show)
            progress.show();
        apiService
                .getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(
                        new DisposableSingleObserver<List<Response>>() {
                            @Override
                            public void onSuccess(List<Response> responses) {
                                usersList.clear();
                                usersList.addAll(responses);
                                List<UsersItems> items = new ArrayList<>();
                                for (int i = 0; i <= responses.size() - 1; i++) {
                                    UsersItems usersItems = new UsersItems()
                                            .withName("Name: " + responses.get(i).getName())
                                            .withEmail("Email: " + responses.get(i).getEmail());
                                    usersItems.setFullAddress(responses.get(i).getAddress().getFullAdd());
                                    usersItems.setLng(responses.get(i).getAddress().getGeo().getLng());
                                    usersItems.setLat(responses.get(i).getAddress().getGeo().getLat());
                                    items.add(usersItems);

                                }
                                adapter.add(items);

                                if (progress.isShowing())
                                    progress.dismiss();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());

                                if (progress.isShowing())
                                    progress.dismiss();
                                mSwipeRefreshLayout.setRefreshing(false);
                                showError(e);
                            }
                        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // add the values which need to be saved from the adapter to the bundle
        outState = adapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.show_details)
    public void showDetails() {
        List<Address> items = getSelectedItems();
        if (items.size() <= 0) {
            Toast.makeText(this, "Please select the item first", Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson = new Gson();
        String jsonList = gson.toJson(items);
        Log.d(TAG, jsonList);
        Intent intent = new Intent(UsersActivity.this, DetailActivity.class);
        intent.putExtra(selectedItems, jsonList);
        startActivity(intent);

    }

    private List<Address> getSelectedItems() {
        Iterator<UsersItems> iterable = adapter.getSelectedItems().iterator();
        List<Address> items = new ArrayList<>();
        while (iterable.hasNext()) {
            Address address = new Address();
            UsersItems usersItems = iterable.next();
            address.setFullAddress(usersItems.getFullAddress());
            address.setGeo(new Geo(usersItems.getLat(), usersItems.getLng()));
            items.add(address);
        }
        return items;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> getUsers(false), 2000);
    }

    /**
     * Showing a Snackbar with error message The error body will be in json format {"error": "Error
     * message!"}
     */
    private void showError(Throwable e) {
        String message = "";
        try {
            if (e instanceof IOException) {
                message = "No internet connection!";
            } else if (e instanceof HttpException) {
                HttpException error = (HttpException) e;
                String errorBody = error.response().errorBody().string();
                JSONObject jObj = new JSONObject(errorBody);

                message = jObj.getString("error");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (TextUtils.isEmpty(message)) {
            message = "Unknown error occurred!";
        }

        Snackbar snackbar = Snackbar.make(coordinator, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
