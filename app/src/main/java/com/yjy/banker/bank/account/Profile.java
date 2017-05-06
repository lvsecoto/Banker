package com.yjy.banker.bank.account;

import java.io.Serializable;
import java.util.UUID;

public class Profile implements Serializable {

    private String mName;
    private String mDescription;
    private UUID mPhoto;

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setPhoto(UUID photo) {
        mPhoto = photo;
    }

    public UUID getPhoto() {
        return mPhoto;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Profile)) return false;

        Profile info = (Profile) other;

        return
                !(mName != null ? !mName.equals(info.mName) : info.mName != null)
                        && (mDescription != null ? mDescription.equals(info.mDescription) : info.mDescription == null
                        && (mPhoto != null ? mPhoto.equals(info.mPhoto) : info.mPhoto == null));

    }

    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        result = 31 * result + (mPhoto != null ? mPhoto.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mPhoto=" + mPhoto +
                '}';
    }
}
