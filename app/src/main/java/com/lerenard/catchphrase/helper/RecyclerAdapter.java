package com.lerenard.catchphrase.helper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lerenard.catchphrase.Game;

import java.util.ArrayList;
import java.util.Collections;

import co.paulburke.android.itemtouchhelperdemo.helper.ItemTouchHelperAdapter;

/**
 * The adapter has an internal copy of the data. When something happens,
 * it notifies the DataSetListener about it so that it can react appropriately. It also
 * allows dragging items around and swiping to delete.
 */
public class RecyclerAdapter<T extends HasId, VH extends RecyclerViewHolder<T>>
        extends RecyclerView.Adapter<VH>
        implements ItemTouchHelperAdapter {

    private static String TAG = "RecyclerAdapter_";
    protected final DataSetListener<T> listener;
    protected final Supplier<VH> supplier;
    protected ArrayList<T> items;
    protected int layout;
    private int selectedColor;
    private int unselectedColor;

    public RecyclerAdapter(
            ArrayList<T> items,
            DataSetListener<T> listener,
            int layout,
            Supplier<VH> supplier,
            int unselectedColor,
            int selectedColor
    ) {
        this.items = items;
        this.listener = listener;
        this.layout = layout;
        this.supplier = supplier;
        this.selectedColor = selectedColor;
        this.unselectedColor = unselectedColor;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        T moved = items.get(fromPosition);
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if (listener != null) {
            listener.onDrag(moved, fromPosition, toPosition);
        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        remove(position, true);
    }

    public void remove(int position, boolean notify) {
        T removed = items.remove(position);
        notifyItemRemoved(position);
        if (notify && listener != null) {
            listener.onDelete(removed, position);
        }
    }

    public void add(T t, boolean notify) {
        insert(items.size(), t, notify);
    }

    public void insert(int index, T t, boolean notify) {
        items.add(index, t);
        notifyItemInserted(index);
        if (notify && listener != null) {
            listener.onAdd(t, index);
        }
    }

    public void set(int index, T t, boolean notify) {
        items.set(index, t);
        notifyItemChanged(index);
        if (notify && listener != null) {
            listener.onUpdate(t);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return supplier.get(view, this);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public DataSetListener<T> getListener() {
        return listener;
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public int getUnselectedColor() {
        return unselectedColor;
    }

    public void setUnselectedColor(int unselectedColor) {
        this.unselectedColor = unselectedColor;
    }

    public T get(int position) {
        return items.get(position);
    }

    public int indexOf(T t) {
        for (int i = 0; i < getItemCount(); ++i) {
            if (t.getId() == items.get(i).getId()) {
                return i;
            }
        }
        return -1;
    }
}
