/*
 * DateTimeUtils.java
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

import org.threeten.bp.Duration;
import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;


public class DateTimeUtils {

    /**
     * Generates a textual representation of the elapsed time.
     *
     * Generated text can be directly used on UI, e.g. for the elapsed time from the upload of a
     * photo.
     *
     * @param start A date-time object representing the initial moment, also containing timezone
     *              info.
     * @param stop  A date-time object, after *start*, representing the ending moment, also
     *              containing timezone info.
     *
     * @return Textual representation of the elapsed time.
     */
    public static String getElapsedTimeInLocalizedText(ZonedDateTime start, ZonedDateTime stop) {
        Period   period = Period.between(start.toLocalDate(), stop.toLocalDate());
        Duration duration = Duration.between(start, stop);

        final String formatter = App.getAppResources().getString(R.string.utils_elapsed_time_format);
        int timeVal;
        String unitText;

        // Check all time units from years to seconds and keep the first non-zero one.
        if ((timeVal = period.getYears()) > 0) {
            if (timeVal > 1) unitText = App.getAppResources().getString(R.string.utils_elapsed_time_years_plural);
            else unitText = App.getAppResources().getString(R.string.utils_elapsed_time_years_single);

        } else if ((timeVal = period.getMonths()) > 0) {
            if (timeVal > 1) unitText = App.getAppResources().getString(R.string.utils_elapsed_time_months_plural);
            else unitText = App.getAppResources().getString(R.string.utils_elapsed_time_months_single);

        } else if ((timeVal = period.getDays()) > 0) {
            if (timeVal > 1) unitText = App.getAppResources().getString(R.string.utils_elapsed_time_days_plural);
            else unitText = App.getAppResources().getString(R.string.utils_elapsed_time_days_single);

        } else if ((timeVal = (int) (duration.toHours())) > 0) {
            if (timeVal > 1) unitText = App.getAppResources().getString(R.string.utils_elapsed_time_hours_plural);
            else unitText = App.getAppResources().getString(R.string.utils_elapsed_time_hours_single);

        } else if ((timeVal = (int) (duration.toMinutes())) > 0) {
            if (timeVal > 1) unitText = App.getAppResources().getString(R.string.utils_elapsed_time_mins_plural);
            else unitText = App.getAppResources().getString(R.string.utils_elapsed_time_mins_single);

        } else {
            timeVal = (int) duration.getSeconds();
            if (timeVal == 1) unitText = App.getAppResources().getString(R.string.utils_elapsed_time_secs_plural);
            else unitText = App.getAppResources().getString(R.string.utils_elapsed_time_secs_single);
        }

        return String.format(formatter, Integer.toString(timeVal), unitText);
    }
}
