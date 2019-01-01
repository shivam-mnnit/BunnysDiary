package aman.agrawal.bunnysdiary.Trips;

/**
 * Created by Dell-1 on 9/23/2018.
 */

public class UploadExperience {

    private String cityName,tripDateFrom,tripDateTo,expenses,experience;

    public  UploadExperience(){
    }

    public UploadExperience(String cityName,String expenses, String experience,String tripDateFrom, String tripDateTo) {
        this.cityName = cityName;
        this.tripDateFrom = tripDateFrom;
        this.tripDateTo = tripDateTo;
        this.expenses = expenses;
        this.experience = experience;
    }

    public String getTripDateFrom() {
        return tripDateFrom;

    }

    public String getTripDateTo() {
        return tripDateTo;
    }

    public String getCityName() {

        return cityName;
    }

    public String getExpenses() {
        return expenses;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setTripDateFrom(String tripDateFrom) {
        this.tripDateFrom = tripDateFrom;
    }

    public void setTripDateTo(String tripDateTo) {
        this.tripDateTo = tripDateTo;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getExperience() {
        return experience;
    }

}
