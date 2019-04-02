/*
 * TextUtils.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.utils;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;

import java.util.Locale;


public class TextUtils {

    public static String integerToOrdinalSuffix(int i) {

        String suffix;

        switch (i) {
            case 1:
                suffix = App.getAppResources().getString(R.string.utils_ordinal_suffix_first);
                break;

            case 2:
                suffix = App.getAppResources().getString(R.string.utils_ordinal_suffix_second);
                break;

            case 3:
                suffix = App.getAppResources().getString(R.string.utils_ordinal_suffix_third);
                break;

            default:
                suffix = App.getAppResources().getString(R.string.utils_ordinal_suffix_generic);
                break;
        }

        return suffix;
    }

    public static String integerToOrdinalString(int i) {
        return String.format(Locale.getDefault(), "%d%s", i, integerToOrdinalSuffix(i));
    }
}
