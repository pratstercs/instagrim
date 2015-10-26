package uk.ac.dundee.computing.aec.instagrim.stores;

import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Phil
 */
public class Comment {
    private String user;
    private Date when;
    private String comment;
    private UUID picid;
    
    public Comment() {
        user = null;
        when = null;
        comment = null;
        picid = null;
    }
    
    public Comment(String user, Date date, String comment, UUID picid) {
        this.user = user;
        this.when = date;
        this.comment = comment;
        this.picid = picid;
    }
    
    public String getUser() {
        return user;
    }
    public Date getDate() {
        return when;
    }
    public String getCommentText() {
        return comment;
    }
    public UUID getPicID() {
        return picid;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    public void setDate(Date date) {
        this.when = date;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setPicID(UUID picID) {
        this.picid = picID;
    }
}
