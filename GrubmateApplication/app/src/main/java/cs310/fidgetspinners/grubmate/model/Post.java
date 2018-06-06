package cs310.fidgetspinners.grubmate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by timothylew on 9/25/17.
 */

public class Post implements Serializable{
    private String originalPoster;
    private String type;
    private String category;
    private String postName;
    private String location;
    private ArrayList<String> tags;
    private String price;
    private int maxPortions;
    private int portionsAvailable;
    private Date startRange;
    private Date endRange;
    private ArrayList<String> pictures;
    private String description;
    private double latitude;
    private double longitude;
    private ArrayList<Request> requests;

    public Post() {}

    public Post(String originalPoster, String postName, String type, String category, String location,
                 ArrayList<String> tags, String description, String price, int maxPortions, int portionsAvailable,
                 Date startRange, Date endRange, ArrayList<String> pictures, ArrayList<Request> requests,
                 double latitude, double longitude) {
        this.originalPoster = originalPoster;
        this.postName = postName;
        this.type = type;
        this.category = category;
        this.location = location;
        this.tags = tags;
        this.description = description;
        this.price = price;
        this.maxPortions = maxPortions;
        this.portionsAvailable = portionsAvailable;
        this.startRange = startRange;
        this.endRange = endRange;
        this.pictures = pictures;
        this.requests = requests;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void initializePost(String originalPoster, String postName, String type, String category, String location,
                ArrayList<String> tags, String description, String price, int maxPortions, int portionsAvailable,
                Date startRange, Date endRange, ArrayList<String> pictures, ArrayList<Request> requests,
                double latitude, double longitude) {
        this.originalPoster = originalPoster;
        this.postName = postName;
        this.type = type;
        this.category = category;
        this.location = location;
        this.tags = tags;
        this.description = description;
        this.price = price;
        this.maxPortions = maxPortions;
        this.portionsAvailable = portionsAvailable;
        this.startRange = startRange;
        this.endRange = endRange;
        this.pictures = pictures;
        this.requests = requests;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getOriginalPoster() {
        return originalPoster;
    }

    public void setOriginalPoster(String originalPoster) {
        this.originalPoster = originalPoster;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getMaxPortions() {
        return maxPortions;
    }

    public void setMaxPortions(int maxPortions) {
        this.maxPortions = maxPortions;
    }

    public int getPortionsAvailable() {
        return portionsAvailable;
    }

    public void setPortionsAvailable(int portionsAvailable) {
        this.portionsAvailable = portionsAvailable;
    }

    public Date getStartRange() {
        return startRange;
    }

    public void setStartRange(Date startRange) {
        this.startRange = startRange;
    }

    public Date getEndRange() {
        return endRange;
    }

    public void setEndRange(Date endRange) {
        this.endRange = endRange;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

    @Override
    public String toString() {
        return "Post{" +
                "originalPoster=" + originalPoster +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", postName='" + postName + '\'' +
                ", location='" + location + '\'' +
                ", tags=" + tags +
                ", price='" + price + '\'' +
                ", maxPortions=" + maxPortions +
                ", portionsAvailable=" + portionsAvailable +
                ", startRange=" + startRange +
                ", endRange=" + endRange +
                ", pictures=" + pictures +
                ", description='" + description + '\'' +
                ", requests=" + requests +
                '}';
    }

}
