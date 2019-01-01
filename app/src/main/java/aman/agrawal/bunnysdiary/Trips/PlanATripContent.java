package aman.agrawal.bunnysdiary.Trips;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Dell-1 on 9/26/2018.
 */

public class PlanATripContent implements Parcelable{

    private String tripName,destination,startDate,pdfUrl,budget,noOfDays;
    private List<String> friends;

    public PlanATripContent(){

    }

    public PlanATripContent(String tripName, String destination, String startDate, String pdfUrl, String budget, String noOfDays, List<String> friends) {
        this.tripName = tripName;
        this.destination = destination;
        this.startDate = startDate;
        this.pdfUrl = pdfUrl;
        this.budget = budget;
        this.noOfDays = noOfDays;
        this.friends = friends;
    }

    protected PlanATripContent(Parcel in) {
        tripName = in.readString();
        destination = in.readString();
        startDate = in.readString();
        pdfUrl = in.readString();
        budget = in.readString();
        noOfDays = in.readString();
        friends = in.createStringArrayList();
    }

    public static final Creator<PlanATripContent> CREATOR = new Creator<PlanATripContent>() {
        @Override
        public PlanATripContent createFromParcel(Parcel in) {
            return new PlanATripContent(in);
        }

        @Override
        public PlanATripContent[] newArray(int size) {
            return new PlanATripContent[size];
        }
    };

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public void setNoOfDays(String noOfDays) {
        this.noOfDays = noOfDays;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getTripName() {
        return tripName;
    }

    public String getDestination() {
        return destination;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getBudget() {
        return budget;
    }

    public String getNoOfDays() {
        return noOfDays;
    }

    public List<String> getFriends() {
        return friends;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tripName);
        parcel.writeString(destination);
        parcel.writeString(startDate);
        parcel.writeString(pdfUrl);
        parcel.writeString(budget);
        parcel.writeString(noOfDays);
        parcel.writeStringList(friends);
    }
}
