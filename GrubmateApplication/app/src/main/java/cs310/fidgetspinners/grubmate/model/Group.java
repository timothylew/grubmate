package cs310.fidgetspinners.grubmate.model;

import java.util.ArrayList;

/**
 * Created by timothylew on 9/25/17.
 */

public class Group {
    private ArrayList<User> groupMembers;
    private String groupName;

    public Group() {}

    public Group(String groupName, ArrayList<User> groupMembers) {
        this.groupMembers = groupMembers;
        this.groupName = groupName;
    }

    public ArrayList<User> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
