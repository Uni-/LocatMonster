package net.aurynj.rne.locatmonster.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class LatLngImpl implements Parcelable {
    public static final Parcelable.Creator<LatLngImpl> CREATOR = new ParcelCreator();
    public final double latitude;
    public final double longitude;
    private final LatLng mLatLng;

    public LatLngImpl(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.mLatLng = new LatLng(latitude, longitude);
    }

    private LatLngImpl(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.mLatLng = latLng;
    }

    @Override
    public int describeContents() {
        return mLatLng.describeContents();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        mLatLng.writeToParcel(parcel, i);
    }

    @Override
    public int hashCode() {
        return mLatLng.hashCode();
    }

    @Override
    public String toString() {
        return mLatLng.toString();
    }

    public LatLng toMapLatLng() {
        return mLatLng;
    }

    private static class ParcelCreator implements Parcelable.Creator<LatLngImpl> {
        private static Parcelable.Creator<LatLng> CREATOR = LatLng.CREATOR;

        @Override
        public LatLngImpl createFromParcel(Parcel parcel) {
            return new LatLngImpl(CREATOR.createFromParcel(parcel));
        }

        @Override
        public LatLngImpl[] newArray(int i) {
            return new LatLngImpl[i];
        }
    }
}
