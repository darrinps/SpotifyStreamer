package udacity.standardandroid.com.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by darri_000 on 6/25/2015.
 *
 * Javabean like class to hold a search result entry
 */
public class SearchRowItem implements Parcelable
{
    private String imageURLAsString;
    private String artistName;
    private String spotifyId;

    public SearchRowItem(String imageId, String artist_name, String spotifyId)
    {
        this.imageURLAsString = imageId;
        this.artistName       = artist_name;
        this.spotifyId        = spotifyId;
    }

    protected SearchRowItem(Parcel in)
    {
        imageURLAsString = in.readString();
        artistName       = in.readString();
        spotifyId        = in.readString();
    }

    public static final Creator<SearchRowItem> CREATOR = new Creator<SearchRowItem>() {
        @Override
        public SearchRowItem createFromParcel(Parcel in) {
            return new SearchRowItem(in);
        }

        @Override
        public SearchRowItem[] newArray(int size) {
            return new SearchRowItem[size];
        }
    };

    public String getImageId()
    {
        return imageURLAsString;
    }

    public void setImageId(String imageId)
    {
        this.imageURLAsString = imageId;
    }

    public String getArtistName()
    {
        return artistName;
    }

    public void setArtistName(String artistName)
    {
        this.artistName = artistName;
    }

    public String getSpotifyId()
    {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId)
    {
        this.spotifyId = spotifyId;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(imageURLAsString);
        dest.writeString(artistName);
        dest.writeString(spotifyId);
    }
}
