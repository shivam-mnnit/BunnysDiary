package aman.agrawal.bunnysdiary.Trips;

/**
 * Created by Dell-1 on 9/26/2018.
 */

public class BucketListContent {

    private String CityName,PhotoUrl;

    public BucketListContent(){

    }

    public BucketListContent(String CityName, String PhotoUrl) {
        this.CityName = CityName;
        this.PhotoUrl = PhotoUrl;
    }

    public void setCityName(String CityName) {
        this.CityName = CityName;
    }

    public void setPhotoUrl(String PhotoUrl) {
        this.PhotoUrl = PhotoUrl;
    }

    public String getCityName() {
        return CityName;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }
}
