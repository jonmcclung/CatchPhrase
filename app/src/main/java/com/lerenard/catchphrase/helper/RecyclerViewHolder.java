package com.lerenard.catchphrase.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import co.paulburke.android.itemtouchhelperdemo.helper.ItemTouchHelperViewHolder;

/**
 * Created by mc on 18-Dec-16.
 */

public class RecyclerViewHolder<T extends HasId> extends RecyclerView.ViewHolder
        implements View.OnClickListener, ItemTouchHelperViewHolder,
                   View.OnLongClickListener {
    protected RecyclerAdapter<T, ? extends RecyclerViewHolder<T>> adapter;
    private T item;

    public RecyclerViewHolder(
            View itemView, RecyclerAdapter<T, ? extends RecyclerViewHolder<T>> adapter) {
        super(itemView);
        this.adapter = adapter;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (adapter.getListener() != null) {
            adapter.getListener().onClick(item, getAdapterPosition());
        }
    }

    /**
     * use this method to update your views based on the value of item
     *
     * @param item the new item that should be represented by this ViewHolder
     */
    public void setItem(T item) {
        this.item = item;
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundColor(adapter.getSelectedColor());
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(adapter.getUnselectedColor());
    }

    @Override
    public boolean onLongClick(View view) {
        if (adapter.getListener() != null) {
            return adapter.getListener().onLongPress(item, getAdapterPosition());
        }
        return false;
    }
}
