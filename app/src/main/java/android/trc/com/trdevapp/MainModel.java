package android.trc.com.trdevapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;

/**
 * JiangyeLin on 2018/6/6
 */
public class MainModel implements Parcelable {
    String label;
    String platform;

    @DrawableRes
    int icon;

    public MainModel(String label, String platform, int icon) {
        this.label = label;
        this.icon = icon;
        this.platform = platform;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.label);
        dest.writeString(this.platform);
        dest.writeInt(this.icon);
    }

    protected MainModel(Parcel in) {
        this.label = in.readString();
        this.platform = in.readString();
        this.icon = in.readInt();
    }

    public static final Creator<MainModel> CREATOR = new Creator<MainModel>() {
        @Override
        public MainModel createFromParcel(Parcel source) {
            return new MainModel(source);
        }

        @Override
        public MainModel[] newArray(int size) {
            return new MainModel[size];
        }
    };
}
