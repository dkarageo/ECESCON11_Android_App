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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ContentPage<T> implements Serializable {

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

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        if (!(mContentList instanceof Serializable)) {
            ArrayList<T> serializableList = new ArrayList<>(mContentList);
            oos.writeObject(serializableList);
        } else {
            oos.writeObject((Serializable) mContentList);
        }
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        mContentList = (List<T>) ois.readObject();
    }
}
