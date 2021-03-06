/*
 * User.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.3
 */

package com.sfhmmy.mobile.users;

import android.os.Parcel;
import android.os.Parcelable;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;

import org.threeten.bp.ZonedDateTime;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class User implements Parcelable, Serializable {

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public enum Role { VISITOR, SECRETARY, ADMINISTRATOR }

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    @SerializedName("id")
    private long mUid;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("name")
    private String mName;
    @SerializedName("surname")
    private String mSurname;
    private String mEducationLevel;
    private String mOrganization;
    private String mDepartment;
    private String mDepartmentSpecialization;
    @SerializedName("years_of_experience")
    private int mYearsOfExperience;
    private String mToken;
    private Role mRole;
    @SerializedName("profile_image_url")
    private String mProfilePictureURL;
    @SerializedName("passport_id")
    private String mPassportValue;
    private Gender mGender;
    private String mPreferedLanguage;
    @SerializedName("created_at")
    private ZonedDateTime mRegistrationDate;
    @SerializedName("checkins")
    private ArrayList<CheckinDate> mCheckInDates;

    public User() {}

    public User(long uid, String email, String name) {
        setUid(uid);
        setEmail(email);
        setName(name);
    }

    public User(Parcel source) {
        mUid               = source.readLong();
        mEmail             = source.readString();
        mName              = source.readString();
        mSurname           = source.readString();
        mEducationLevel    = source.readString();
        mOrganization      = source.readString();
        mDepartment        = source.readString();
        mDepartmentSpecialization = source.readString();
        mYearsOfExperience = source.readInt();
        mToken             = source.readString();
        mRole              = (Role) source.readSerializable();
        mProfilePictureURL = source.readString();
        mPassportValue     = source.readString();
        source.readTypedList(mCheckInDates, CheckinDate.CREATOR);
        mGender            = (Gender) source.readSerializable();
        mPreferedLanguage  = source.readString();
        mRegistrationDate  = (ZonedDateTime) source.readSerializable();
    }

    public long getUid() { return mUid; }
    public String getEmail() { return mEmail; }
    public String getName() { return mName; }
    public String getSurname() { return mSurname; }
    public String getEducationLevel() { return mEducationLevel; }
    public String getDepartment() { return mDepartment; }
    public String getDepartmentSpecialization() { return mDepartmentSpecialization; }
    public int getYearsOfExperience() { return mYearsOfExperience; }
    public String getToken() { return mToken; }
    public Role getRole() { return mRole; }
    public String getProfilePictureURL() { return mProfilePictureURL; }
    public String getOrganization() { return mOrganization; }
    public String getPassportValue() { return mPassportValue; }
    public Gender getGender() { return mGender; }
    public String getPreferedLanguage() { return mPreferedLanguage; }
    public ZonedDateTime getRegistrationDate() { return mRegistrationDate; }
    public ArrayList<CheckinDate> getCheckinDates() { return mCheckInDates; }

    public ZonedDateTime getLastCheckInDate() {
        return mCheckInDates != null && mCheckInDates.size() > 0 ? mCheckInDates.get(0).getDate() : null;
    }

    public void setUid(long uid) { mUid = uid; }
    public void setEmail(String email) { mEmail = email; }
    public void setName(String name) { mName = name; }
    public void setSurname(String surname) { mSurname = surname; }
    public void setEducationLevel(String educationLevel) { mEducationLevel = educationLevel; }
    public void setDepartment(String department) { mDepartment = department; }
    public void setToken(String token) { mToken = token; }
    public void setRole(Role role) { mRole = role; }
    public void setProfilePictureURL(String profilePictureURL) { mProfilePictureURL = profilePictureURL; }
    public void setOrganization(String organization) { mOrganization = organization; }
    public void setPassportValue(String passportValue) { mPassportValue = passportValue; }
       public void setGender(Gender gender) { mGender = gender; }
    public void setPreferedLanguage(String preferedLanguage) { mPreferedLanguage = preferedLanguage; }
    public void setRegistrationDate(ZonedDateTime registrationDate) { mRegistrationDate = registrationDate; }

    public void setDepartmentSpecialization(String departmentSpecialization) {
        mDepartmentSpecialization = departmentSpecialization;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        mYearsOfExperience = yearsOfExperience;
    }

    public void setCheckinDates(List<CheckinDate> checkinDates) {
        if (checkinDates instanceof ArrayList) {
            mCheckInDates = (ArrayList<CheckinDate>) checkinDates;
        } else {
            mCheckInDates = new ArrayList<>();
            mCheckInDates.addAll(checkinDates);
        }
    }

    public boolean hasRoleOf(Role role) {
        if (mRole == role) return true;
        else if (role == Role.SECRETARY && mRole == Role.ADMINISTRATOR) return true;
        else return false;
    }

    public String getRoleText() {
        String text = null;

        if (mRole != null) {
            switch (mRole) {
                case VISITOR:
                    text = App.getAppResources().getString(R.string.user_role_visitor_text);
                    break;

                case SECRETARY:
                    text = App.getAppResources().getString(R.string.user_role_secretary_text);
                    break;

                case ADMINISTRATOR:
                    text = App.getAppResources().getString(R.string.user_role_administrator_text);
                    break;
            }
        }

        return text;
    }

    public String getGenderText() {
        String text = null;

        if (mGender != null) {
            switch(mGender) {
                case MALE:
                    text = App.getAppResources().getString(R.string.user_gender_male_text);
                    break;

                case FEMALE:
                    text = App.getAppResources().getString(R.string.user_gender_female_text);
                    break;

                case OTHER:
                    text = App.getAppResources().getString(R.string.user_gender_other_text);
                    break;
            }
        }

        return text;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mUid);
        dest.writeString(mEmail);
        dest.writeString(mName);
        dest.writeString(mSurname);
        dest.writeString(mEducationLevel);
        dest.writeString(mOrganization);
        dest.writeString(mDepartment);
        dest.writeString(mDepartmentSpecialization);
        dest.writeInt(mYearsOfExperience);
        dest.writeString(mToken);
        dest.writeSerializable(mRole);
        dest.writeString(mProfilePictureURL);
        dest.writeString(mPassportValue);
        dest.writeTypedList(mCheckInDates);
        dest.writeSerializable(mGender);
        dest.writeString(mPreferedLanguage);
        dest.writeSerializable(mRegistrationDate);
    }
}
