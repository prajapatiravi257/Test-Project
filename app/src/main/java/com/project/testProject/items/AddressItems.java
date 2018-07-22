package com.project.testProject.items;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;
import com.project.testProject.R;
import com.project.testProject.view.MapsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressItems extends AbstractItem<AddressItems, AddressItems.ViewHolder> {

    public StringHolder fullAddress;
    private StringHolder lat;
    private StringHolder lng;

    public AddressItems withFullAddress(String address) {
        this.fullAddress = new StringHolder(address);
        return this;
    }

    public AddressItems withLat(String lat) {
        this.lat = new StringHolder(lat);
        return this;
    }

    public AddressItems withLng(String lng) {
        this.lng = new StringHolder(lng);
        return this;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        //set the text for the name
        StringHolder.applyTo(fullAddress, viewHolder.address);
        viewHolder.button.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MapsActivity.class);
            intent.putExtra("lat", lat.getText());
            intent.putExtra("long", lng.getText());
            intent.putExtra("address", fullAddress.getText());
            v.getContext().startActivity(intent);
        });
        //set the text for the description or hide
        // StringHolder.applyToOrHide(description, viewHolder.description);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.address.setText(null);
    }


    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.detail_item;
    }


    static

            /**
             * our ViewHolder
             */
    class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @BindView(R.id.address)
        TextView address;
        @BindView(R.id.button)
        Button button;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }


    }
}
