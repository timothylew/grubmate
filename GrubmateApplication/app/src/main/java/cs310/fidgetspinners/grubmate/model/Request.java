package cs310.fidgetspinners.grubmate.model;

/**
 * Created by timothylew on 9/25/17.
 */

public class Request {
    public static final int INITIALIZED = 0;
    public static final int ACCEPTED = 1;
    public static final int OWNER_CONFIRMED = 2;
    public static final int REQUESTER_CONFIRMED = 3;
    public static final int BOTH_CONFIRMED = 4;

    private String requestingUser;
    private Post originalPost;
    private int numShares;
    private long phoneNumber;
    private String location;
    private String requestDetails;
    private int status;

    public Request() {}

    public Request(String requestingUser, Post originalPost, long phoneNumber, String location, int numShares,
                   String requestDetails) {
        this.requestingUser = requestingUser;
        this.originalPost = originalPost;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.numShares = numShares;
        this.requestDetails = requestDetails;
    }

    public String getRequestDetails() { return requestDetails; }

    public void setRequestDetails(String newDetails) { this.requestDetails = newDetails; }

    public String getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(String requestingUser) {
        this.requestingUser = requestingUser;
    }

    public Post getOriginalPost() {
        return originalPost;
    }

    public void setOriginalPost(Post originalPost) {
        this.originalPost = originalPost;
    }

    public int getNumShares() {
        return numShares;
    }

    public void setNumShares(int numShares) {
        this.numShares = numShares;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
