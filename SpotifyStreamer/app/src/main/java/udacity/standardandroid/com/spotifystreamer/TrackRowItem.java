package udacity.standardandroid.com.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by darri_000 on 7/1/2015.
 *
 * Javabean like class to hold a specific sound track entry's data
 */
public class TrackRowItem implements Parcelable
{
    private String thumbnailUrl;
    private String albumName;
    private String trackName;
    private String previewUrl;
    private String bigImageUrl;
    private String artistName;


    public TrackRowItem(String thumbnailUrl, String bigImageUrl, String album_name, String track_name, String spotifyId, String artistName)
    {
        this.thumbnailUrl = thumbnailUrl;
        this.albumName    = album_name;
        this.trackName    = track_name;
        this.previewUrl   = spotifyId;
        this.bigImageUrl  = bigImageUrl;
        this.artistName   = artistName;
    }

    protected TrackRowItem(Parcel in)
    {
        thumbnailUrl = in.readString();
        albumName    = in.readString();
        trackName    = in.readString();
        previewUrl   = in.readString();
        bigImageUrl  = in.readString();
        artistName   = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(thumbnailUrl);
        dest.writeString(albumName);
        dest.writeString(trackName);
        dest.writeString(previewUrl);
        dest.writeString(bigImageUrl);
        dest.writeString(artistName);
    }

    public static final Creator<TrackRowItem> CREATOR = new Creator<TrackRowItem>()
    {
        @Override
        public TrackRowItem createFromParcel(Parcel in)
        {
            return new TrackRowItem(in);
        }

        @Override
        public TrackRowItem[] newArray(int size)
        {
            return new TrackRowItem[size];
        }
    };

    public boolean hasBigImage()
    {
        return !(bigImageUrl == null || bigImageUrl.length() < 1);
    }

    public String getBigImageUrl()
    {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl)
    {
        this.bigImageUrl = bigImageUrl;
    }

    public String getImageId()
    {
        return thumbnailUrl;
    }

    public String getTrackName()
    {
        return trackName;
    }

    public void setTrackName(String trackName)
    {
        this.trackName = trackName;
    }

    public void setImageId(String imageId)
    {
        this.thumbnailUrl = imageId;
    }

    public String getAlbumName()
    {
        return albumName;
    }

    public void setAlbumName(String albumName)
    {
        this.albumName = albumName;
    }

    public String getPreviewUrl()
    {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl)
    {
        this.previewUrl = previewUrl;
    }

    public String getArtistName()
    {
        return artistName;
    }

    public void setArtistName(String artistName)
    {
        this.artistName = artistName;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }


}
