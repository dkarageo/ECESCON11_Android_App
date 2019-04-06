/*
 * PaginationRecyclerViewScrollListener.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public abstract class PaginationRecyclerViewScrollListener extends RecyclerView.OnScrollListener {


    private LinearLayoutManager mLayoutManager;
    private int mPageSize;


    public PaginationRecyclerViewScrollListener(LinearLayoutManager layoutManager, int pageSize) {
        mLayoutManager = layoutManager;
        mPageSize = pageSize;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemsCount = mLayoutManager.getChildCount();
        int totalItemsCount = mLayoutManager.getItemCount();
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemsCount + firstVisibleItemPosition) >= totalItemsCount
                        && firstVisibleItemPosition >= 0
                        && totalItemsCount >= mPageSize) {

                loadMoreItems();
            }
        }
    }

    protected abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}
