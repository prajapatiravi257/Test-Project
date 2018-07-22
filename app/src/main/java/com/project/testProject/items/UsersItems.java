package com.project.testProject.items;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.materialize.holder.StringHolder;
import com.project.testProject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersItems extends AbstractItem<UsersItems, UsersItems.ViewHolder> {


    public StringHolder name;
    public StringHolder email;
    public String fullAddress;
    public String lat;
    public String lng;

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public UsersItems withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public UsersItems withName(@StringRes int NameRes) {
        this.name = new StringHolder(NameRes);
        return this;
    }

    public UsersItems withEmail(String Email) {
        this.email = new StringHolder(Email);
        return this;
    }

    public UsersItems withEmail(@StringRes int EmailRes) {
        this.email = new StringHolder(EmailRes);
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
        viewHolder.checkbox.setChecked(isSelected());

        //set the text for the name
        StringHolder.applyTo(name, viewHolder.name);
        StringHolder.applyTo(email, viewHolder.email);

        //set the text for the description or hide
        // StringHolder.applyToOrHide(description, viewHolder.description);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.email.setText(null);
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
        return R.layout.users_list_item;
    }

    public static class CheckBoxClickEvent extends ClickEventHook<UsersItems> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof UsersItems.ViewHolder) {
                return ((UsersItems.ViewHolder) viewHolder).checkbox;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<UsersItems> fastAdapter, UsersItems item) {
            fastAdapter.toggleSelection(position);
        }
    }

    /**
     * our ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @BindView(R.id.checkbox)
        AppCompatCheckBox checkbox;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.email)
        TextView email;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
