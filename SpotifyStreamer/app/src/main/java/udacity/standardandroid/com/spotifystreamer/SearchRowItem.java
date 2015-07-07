package udacity.standardandroid.com.spotifystreamer;

/**
 * Created by darri_000 on 6/25/2015.
 */
public class SearchRowItem
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
}
