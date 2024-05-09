package org.example.model;

/**
 * description:
 *
 * @author liuhuayang
 * date: 2024/5/9 17:49
 */
public class UserAccess {
    private int userId;
    private String[] accessList;
    private int accessCount; // 访问次数

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String[] getAccessList() {
        return accessList;
    }

    public void setAccessList(String[] accessList) {
        this.accessList = accessList;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }

    public UserAccess(int userId, String[] accessList) {
        this.userId = userId;
        this.accessList = accessList;
        this.accessCount = 0;
    }

    public boolean hasAccess(String resource) {
        for (String access : accessList) {
            if (access.equals(resource)) {
                return true;
            }
        }
        return false;
    }

    public void incrementAccessCount() {
        accessCount++;
    }

    public void decreaseAccessCount() {
        accessCount--;
    }
}
