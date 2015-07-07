package udacity.standardandroid.com.spotifystreamer;

/**
 * Created by darri_000 on 7/1/2015.
 */
public class TrackRowItem
{
    private String thumbnailUrl;
    private String albumName;
    private String trackName;
    private String previewUrl;
    private String bigImageUrl;

    public TrackRowItem(String thumbnailUrl, String bigImageUrl, String album_name, String track_name, String spotifyId)
    {
        this.thumbnailUrl = thumbnailUrl;
        this.bigImageUrl  = bigImageUrl;
        this.albumName    = album_name;
        this.trackName    = track_name;
        this.previewUrl   = spotifyId;
    }

    public boolean hasBigImage()
    {
        if(bigImageUrl == null || bigImageUrl.length() < 1)
        {
            return false;
        }

        return true;
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
}
