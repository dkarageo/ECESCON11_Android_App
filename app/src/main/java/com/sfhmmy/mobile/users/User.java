package com.sfhmmy.mobile.users;

import android.graphics.Bitmap;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;

import org.threeten.bp.ZonedDateTime;

import java.util.Date;

public class User {

    public enum Role { VISITOR, SECRETARY, ADMINISTRATOR }

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    private long mUid;
    private String mEmail;
    private String mName;
    private String mSurname;
    private String mEducationLevel;
    private String mOrganization;
    private String mDepartment;
    private String mDepartmentSpecialization;
    private int mYearsOfExperience;
    private String mToken;
    private Role mRole;
    private String mProfilePictureURL;
    private String mPassportValue;
    private ZonedDateTime mLastCheckInDate;
    private Gender mGender;
    private String mPreferedLanguage;

    public User() {}

    public User(long uid, String email, String name) {
        setUid(uid);
        setEmail(email);
        setName(name);
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
    public ZonedDateTime getLastCheckInDate() { return mLastCheckInDate; }
    public Gender getGender() { return mGender; }
    public String getPreferedLanguage() { return mPreferedLanguage; }

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
    public void setLastCheckInDate(ZonedDateTime lastCheckInDate) { mLastCheckInDate = lastCheckInDate; }
    public void setGender(Gender gender) { mGender = gender; }
    public void setPreferedLanguage(String preferedLanguage) { mPreferedLanguage = preferedLanguage; }

    public void setDepartmentSpecialization(String departmentSpecialization) {
        mDepartmentSpecialization = departmentSpecialization;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        mYearsOfExperience = yearsOfExperience;
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
}
