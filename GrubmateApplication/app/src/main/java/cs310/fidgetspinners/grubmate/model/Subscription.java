package cs310.fidgetspinners.grubmate.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by timothylew on 9/25/17.
 */

public class Subscription implements Serializable{
    private String subscriptionName;
    private Date startTime;
    private Date endTime;

    public Subscription() {}

    public Subscription(String subscriptionName, Date startTime, Date endTime) {
        this.subscriptionName = subscriptionName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Subsciprion{" +
                "subscriptionName=" + subscriptionName +
                ", startTime='" + startTime.toString() + '\'' +
                ", endTime='" + endTime.toString() + "\'" +
                "}";
    }
}
