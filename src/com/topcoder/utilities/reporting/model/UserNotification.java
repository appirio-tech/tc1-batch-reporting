/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.model;

import java.util.List;

/**
 *<p>
 * The notifications of the user.
 *</p>
 *
 * @author Veve
 * @version 1.0
 */
public class UserNotification {
    
    private long user_id;
    private List<Notification> notifications;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
