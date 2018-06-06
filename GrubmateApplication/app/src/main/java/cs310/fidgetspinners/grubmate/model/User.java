package cs310.fidgetspinners.grubmate.model;

import android.media.Image;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by timothylew on 9/25/17.
 */

public class User {
    private long userId;
    private String name;
    private String email;
    private Image profilePicture;
    private String profilephoto;
    private int ratingsCount;
    private float averageRating;
    private ArrayList<Group> groups;
    private ArrayList<Post> activePosts;
    private ArrayList<Request> activeRequests;
    private ArrayList<Post> postHistory;
    private ArrayList<Request> requestHistory;
    private ArrayList<Subscription> subscriptions;
    private ArrayList<Notification> notifications;
    private ArrayList<Post> viewablePosts;

    public User() {}

    // TODO we need another constructor here.
    public User(FirebaseUser user) {
        this.name = user.getDisplayName();
        this.email = String.valueOf(user.getEmail());
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() { return this.email; }

    public void setName(String name) {
        this.name = name;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }



    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public ArrayList<Post> getActivePosts() {
        return activePosts;
    }

    public void setActivePosts(ArrayList<Post> activePosts) {
        this.activePosts = activePosts;
    }

    public ArrayList<Request> getActiveRequests() {
        return activeRequests;
    }

    public void setActiveRequests(ArrayList<Request> activeRequests) {
        this.activeRequests = activeRequests;
    }

    public ArrayList<Post> getPostHistory() {
        return postHistory;
    }

    public void setPostHistory(ArrayList<Post> postHistory) {
        this.postHistory = postHistory;
    }

    public ArrayList<Request> getRequestHistory() {
        return requestHistory;
    }

    public void setRequestHistory(ArrayList<Request> requestHistory) {
        this.requestHistory = requestHistory;
    }

    public ArrayList<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(ArrayList<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    public ArrayList<Post> getViewablePosts() {
        return viewablePosts;
    }

    public void setViewbablePosts (ArrayList<Post> viewablePosts){
        this.viewablePosts = viewablePosts;
    }

    public void addViewablePost(Post post) {
        viewablePosts.add(post);
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }
}
