package cs310.fidgetspinners.grubmate.model;

/**
 * Created by timothylew on 9/25/17.
 */

public class Notification {
    private User intendedUser;
    private boolean isRequestNotification;
    private boolean isSubscriptionNotification; // TODO double check spellings.
    private String text;

    public Notification() {}

    public Notification(String text, User intendedUser, boolean isRequest, boolean isSubscription) {
        this.text = text;
        this.intendedUser = intendedUser;
        this.isRequestNotification = isRequest;
        this.isSubscriptionNotification = isSubscription;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getIntendedUser() {
        return intendedUser;
    }

    public void setIntendedUser(User intendedUser) {
        this.intendedUser = intendedUser;
    }

    public boolean isRequestNotification() {
        return isRequestNotification;
    }

    public void setRequestNotification(boolean requestNotification) {
        isRequestNotification = requestNotification;
    }

    public boolean isSubscriptionNotification() {
        return isSubscriptionNotification;
    }

    public void setIsSubscriptionNotification(boolean isSubscriptionNotification) {
        this.isSubscriptionNotification = isSubscriptionNotification;
    }

}
