package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import static org.imgscalr.Scalr.*;
import java.util.Date;
import uk.ac.dundee.computing.aec.instagrim.filters.filters;
import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.*;

public class PicModel {

    Cluster cluster;
    
    public static final int NORMAL    = 0;
    public static final int GREYSCALE = 1;
    public static final int WEIRD     = 2;
    public static final int SEPIA     = 3;
    public static final int INVERT    = 4;
    public static final int LIGHTEN   = 5;
    public static final int DARKEN    = 6;

    public void PicModel() {
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void insertPic(byte[] b, String type, String name, String user, int mode) {
        try {
            Convertors convertor = new Convertors();

            String types[]=Convertors.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            int length = b.length;
            java.util.UUID picid = convertor.getTimeUUID();
            
            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrimpjp/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrimpjp/" + picid));
            output.write(b);
            
            byte[] thumbb, processedb;
            
            processedb = filters.picConvert(picid.toString(),types[1]);
            
            switch(mode) {
                case WEIRD:
                    processedb = filters.picWeird(picid.toString(),types[1]);
                    break;
                case SEPIA:
                    processedb = filters.picSepia(picid.toString(),types[1]);
                    break;
                case GREYSCALE:
                    processedb = filters.picdecolour(picid.toString(),types[1],OP_GRAYSCALE);
                    break;
                case INVERT:
                    processedb = filters.picInvert(picid.toString(),types[1]);
                    break;
                case LIGHTEN:
                    processedb = filters.picdecolour(picid.toString(),types[1],OP_BRIGHTER);
                    break;
                case DARKEN:
                    processedb = filters.picdecolour(picid.toString(),types[1],OP_DARKER);
                    break;
            }
            
            thumbb = filters.picresize(processedb, types[1]);
            
            int thumblength= thumbb.length;
            ByteBuffer thumbbuf=ByteBuffer.wrap(thumbb);
            ByteBuffer processedbuf = ByteBuffer.wrap(processedb);
            
            int processedlength=b.length;
            Session session = cluster.connect("instagrimPJP");

            PreparedStatement psInsertPic = session.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);

            Date DateAdded = new Date();
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf,processedbuf, user, DateAdded, length,thumblength,processedlength, type, name));
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
            session.close();

        } catch (IOException ex) {
            System.out.println("Error --> " + ex);
        }
        catch (NullPointerException npe) {
            System.out.println("Error --> " + npe);
        }
    }
   
    /**
     * Returns a list of all the user's images
     * @param User the user to return images for
     * @return A linked list of all the user's pictures
     */
    public java.util.LinkedList<Pic> getPicsForUser(String User) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrimPJP");
        PreparedStatement ps = session.prepare("select picid from userpiclist where user =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        User));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for (Row row : rs) {
                Pic pic = new Pic();
                java.util.UUID UUID = row.getUUID("picid");
                System.out.println("UUID" + UUID.toString());
                pic.setUUID(UUID);
                Pics.add(pic);

            }
        }
        session.close();
        
        return Pics;
    }
    
    public String[] getData(String picid) {
        String[] toReturn = new String[2];
        cluster = CassandraHosts.getCluster();
        Session session = cluster.connect("instagrimPJP");
        java.util.UUID uuid = java.util.UUID.fromString(picid);
        
        PreparedStatement ps = session.prepare("select user,pic_added from userpiclist where picid =?");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute( boundStatement.bind(uuid) );

        Row row = rs.one();
        toReturn[0] = row.getString("user");
        toReturn[1] = row.getString("pic_added");

        session.close();
        
        return toReturn;
    }

    /**
     * Returns an image by UUID
     * @param image_type The type of image
     * @param picid The UUID of the image to return
     * @return The picture specified
     */
    public Pic getPic(int image_type, java.util.UUID picid) {
        cluster = CassandraHosts.getCluster();
        Session session = cluster.connect("instagrimPJP");
        
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        String user = "";
        Date date = null;
        
        try {
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
         
            switch(image_type) {
                case Convertors.DISPLAY_IMAGE: 
                    ps = session.prepare("select image,imagelength,type,user,interaction_time from pics where picid =?");
                    break;
                case Convertors.DISPLAY_THUMB:
                    ps = session.prepare("select thumb,imagelength,thumblength,type,user,interaction_time from pics where picid =?");
                    break;
                case Convertors.DISPLAY_PROCESSED:
                    ps = session.prepare("select processed,processedlength,type,user,interaction_time from pics where picid =?");
                    break;
                default: break;
            }
            
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( boundStatement.bind(picid) );

            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    switch(image_type) {
                        case Convertors.DISPLAY_IMAGE:
                            bImage = row.getBytes("image");
                            length = row.getInt("imagelength");
                            break;
                        case Convertors.DISPLAY_THUMB:
                            bImage = row.getBytes("thumb");
                            length = row.getInt("thumblength");
                            break;
                        case Convertors.DISPLAY_PROCESSED:
                            bImage = row.getBytes("processed");
                            length = row.getInt("processedlength");
                            break;
                        default: break;
                    }
                    
                    user = row.getString("user");
                    date = row.getDate("interaction_time");
                    type = row.getString("type");

                }
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
            return null;
        }
        finally {
            session.close();   
        }
        
        Pic p = new Pic();
        p.setPic(bImage, length, type, user, date);
        p.setUUID(picid);

        return p;
    }
    
    public void postComment(java.util.UUID picid, String comment, String user) {
        cluster = CassandraHosts.getCluster();
        Session session = cluster.connect("instagrimPJP");
        
        PreparedStatement addComment = session.prepare("insert into comments (when, comment, picid, user) values(?,?,?,?)");
        BoundStatement bsAddComment = new BoundStatement(addComment);

        Date DateAdded = new Date();
        session.execute(bsAddComment.bind( DateAdded, comment, picid, user ));
        
        session.close();
    }
    
    public LinkedList<Comment> getComments(java.util.UUID picid) {
        java.util.LinkedList<Comment> comments = new java.util.LinkedList<>();
        
        Session session = cluster.connect("instagrimPJP");
        PreparedStatement ps = session.prepare("select * from comments where picid =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( boundStatement.bind(picid) );
        
        if (rs.isExhausted()) {
            System.out.println("No comments returned");
            return null;
        } else {
            for(Row row : rs) {
                String user = row.getString("user");
                String text = row.getString("comment");
                java.util.Date date = row.getDate("when");
                
                Comment comment = new Comment(user,date,text,picid);
                comments.add(comment);
            }
        }
        
        session.close();
        
        return comments;
    }

}
