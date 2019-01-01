package aman.agrawal.bunnysdiary.CityData;

import java.io.Serializable;

/**
 * Created by Dell-1 on 9/21/2018.
 */

public class PlacesData implements Serializable {

    private String placeName,placeIcon,placeAddress;
    private double placeLatitude,placeLongitude,placeRating;

    public PlacesData(String placeName, String placeIcon, String placeAddress, double placeLatitude, double placeLongitude, double placeRating) {
        this.placeName = placeName;
        this.placeIcon = placeIcon;
        this.placeAddress = placeAddress;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.placeRating = placeRating;
    }

    public String getPlaceVicinity() {
        return placeAddress;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceIcon() {
        return placeIcon;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }

    public double getPlaceRating() {
        return placeRating;
    }
}
