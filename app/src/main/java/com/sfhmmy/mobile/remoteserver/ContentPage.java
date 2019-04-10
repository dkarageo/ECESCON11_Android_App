/*
 * ContentPage.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.remoteserver;

import java.util.List;


public class ContentPage<T> {

    private List<T> mContentList;
    private int mCurrentPage;
    private int mTotalPages;
    private int mitemsPerPage;


    public List<T> getContentList() { return mContentList; }
    public int getCurrentPage() { return mCurrentPage; }
    public int getTotalPages() { return mTotalPages; }
    public int getItemsPerPage() { return mitemsPerPage; }

    public void setContentList(List<T> contentList) { mContentList = contentList; }
    public void setCurrentPage(int currentPage) { mCurrentPage = currentPage; }
    public void setTotalPages(int totalPages) { mTotalPages = totalPages; }
    public void setItemsPerPage(int itemsPerPage) { mitemsPerPage = itemsPerPage; }
}
