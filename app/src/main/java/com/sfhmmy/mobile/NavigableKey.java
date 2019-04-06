package com.sfhmmy.mobile;

import androidx.fragment.app.Fragment;


public interface NavigableKey {
    String getKey();
    Fragment createFragment();
}
