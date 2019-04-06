package com.sfhmmy.mobile;

import androidx.fragment.app.Fragment;

/**
 * Interface to be implemented by activities whose main content is the contents of a fragment.
 */
public interface TopLevelFragmentEventsListener {
    /**
     * Updates Activity's ActionBar title.
     *
     * @param newTitle : The new title to be displayed by ActionBar.
     */
    void updateTitle(String newTitle);

    /**
     * Hides navigation elements.
     */
    void hideNavigationBar();

    /**
     * Displays navigation elements.
     */
    void showNavigationBar();

    public void navigateToNavigableKey(NavigableKey target, boolean skipCurrentOnBack);
}
