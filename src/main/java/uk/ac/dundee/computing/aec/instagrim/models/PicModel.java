package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (user,interaction_time)
 ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.Bytes;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.awt.Color;
import java.awt.image.WritableRaster;

import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
//import uk.ac.dundee.computing.aec.stores.TweetStore;

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
            
            processedb = picConvert(picid.toString(),types[1]);
            
            switch(mode) {
                case WEIRD:
                    processedb = picWeird(picid.toString(),types[1]);
                    break;
                case SEPIA:
                    processedb = picSepia(picid.toString(),types[1]);
                    break;
                case GREYSCALE:
                    processedb = picdecolour(picid.toString(),types[1],OP_GRAYSCALE);
                    break;
                case INVERT:
                    processedb = picInvert(picid.toString(),types[1]);
                    break;
                case LIGHTEN:
                    processedb = picdecolour(picid.toString(),types[1],OP_BRIGHTER);
                    break;
                case DARKEN:
                    processedb = picdecolour(picid.toString(),types[1],OP_DARKER);
                    break;
            }
            
            thumbb = picresize(processedb, types[1]);
            
            int thumblength= thumbb.length;
            ByteBuffer thumbbuf=ByteBuffer.wrap(thumbb);
            ByteBuffer processedbuf = ByteBuffer.wrap(processedb);
            
            int processedlength=b.length;
            Session session = cluster.connect("instagrim_PJP");

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
     * Accessor method for picResize using a byte array
     * @param pic The byte array to resize
     * @param type The type of the image
     * @return The resized image as a byte array
     * @throws IOException If image stream does not exist
     */
    public byte[] picresize(byte[] pic, String type) throws IOException {
            InputStream in = new java.io.ByteArrayInputStream(pic);
            BufferedImage bImageFromConvert = ImageIO.read(in);
            return picresize(bImageFromConvert, type);
    }
    /**
     * Accessor method for picResize using a String for the image's location
     * @param picid The image's location
     * @param type The type of the image
     * @return The resized image as a byte array
     * @throws IOException If the image does not exist at that location
     */
    public byte[] picresize(String picid, String type) throws IOException {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrimpjp/" + picid));
            return picresize(BI, type);
    }
    /**
     * Resizes images when passed a BufferedImage
     * @param pic The image to resize
     * @param type The type of the image
     * @return The resized image as a byte array
     */
    private byte[] picresize(BufferedImage pic, String type) {
        try {
            BufferedImage thumbnail = createThumbnail(pic);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();
            
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }
    
    /**
     * Converts a picture stored on disk to a byte array
     * @param picid The image's location on disk
     * @param type The type of image
     * @return The converted picture as a byte array
     */
    public byte[] picConvert(String picid, String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrimpjp/" + picid));
            //BufferedImage processed = createProcessed(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(BI, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }
    
    /**
     * Transforms the colours of an image
     * @param picid The image to transform
     * @param type The type of image
     * @param mode The transformation to perform
     * @return The transformed image as a byte array
     */
    public byte[] picdecolour(String picid, String type, java.awt.image.BufferedImageOp mode) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrimpjp/" + picid));
            BufferedImage processed = createProcessed(BI, mode);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }
    
    /**
     * Transforms the colours of an image to a pink-ish mix
     * Adapted from picSepia
     * @param picid The image to transform
     * @param type The type of image
     * @return The transformed image as a byte array
     */
    public byte[] picWeird(String picid, String type) {
        try {
            BufferedImage img = ImageIO.read(new File("/var/tmp/instagrimpjp/" + picid));

            int sepiaIntensity = 0;
            BufferedImage sepia = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int sepiaDepth = 20;

            int w = img.getWidth();
            int h = img.getHeight();

            WritableRaster raster = sepia.getRaster();

            // We need 3 integers (for R,G,B color values) per pixel.
            int[] pixels = new int[w * h * 3];
            img.getRaster().getPixels(0, 0, w, h, pixels);

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    int rgb = img.getRGB(x, y); //get rgb value
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    int gry = (r + g + b) / 3; //get greyscale/average

                    r = g = b = gry; //sets all to average
                    r = r + (sepiaDepth * 3); //adds 2*sepiadepth to red channel
                    g = g + (sepiaDepth*2); //add sepiadepth to green channel

                    if (r > 255) {
                        r = 255;
                    }
                    if (g > 255) {
                        g = 255;
                    }
                    if (b > 255) {
                        b = 255;
                    }

                    // Darken blue color to increase sepia effect
                    b -= sepiaIntensity;

                    // normalize if out of bounds
                    if (b < 0) {
                        b = 0;
                    }
                    if (b > 255) {
                        b = 255;
                    }

                    color = new Color(r, g, b, color.getAlpha());
                    sepia.setRGB(x, y, color.getRGB());
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(sepia, type, baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        }
        catch (IOException ioe) {
            return null;
        }
    }

    /**
     * Transforms an image into sepia
     * @param picid The image to transform
     * @param type The type of image
     * @return The transformed image as a byte array
     * Adapted from http://stackoverflow.com/questions/21899824/java-convert-a-greyscale-and-sepia-version-of-an-image-with-bufferedimage
     * In turn from https://groups.google.com/forum/#!topic/comp.lang.java.programmer/nSCnLECxGdA
     */
    public byte[] picSepia(String picid, String type) {
        try {
            BufferedImage img = ImageIO.read(new File("/var/tmp/instagrimpjp/" + picid));
            BufferedImage sepia = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                    // Play around with this.  20 works well and was recommended
                    //   by another developer. 0 produces black/white image
                    int sepiaDepth = 20;
                    int sepiaIntensity = 30;

                    int w = img.getWidth();
                    int h = img.getHeight();

                    WritableRaster raster = sepia.getRaster();

                    // We need 3 integers (for R,G,B color values) per pixel.
                    int[] pixels = new int[w * h * 3];
                    img.getRaster().getPixels(0, 0, w, h, pixels);

                    //  Process 3 ints at a time for each pixel.  Each pixel has 3 RGB
                    //    colors in array
                    for (int i = 0; i < pixels.length; i += 3) {
                        int r = pixels[i];
                        int g = pixels[i + 1];
                        int b = pixels[i + 2];

                        int gry = (r + g + b) / 3;
                        r = g = b = gry;
                        r = r + (sepiaDepth * 2);
                        g = g + sepiaDepth;

                        if (r > 255) {
                            r = 255;
                        }
                        if (g > 255) {
                            g = 255;
                        }
                        if (b > 255) {
                            b = 255;
                        }

                        // Darken blue color to increase sepia effect
                        b -= sepiaIntensity;

                        // normalize if out of bounds
                        if (b < 0) {
                            b = 0;
                        }
                        if (b > 255) {
                            b = 255;
                        }

                        pixels[i] = r;
                        pixels[i + 1] = g;
                        pixels[i + 2] = b;
                    }
                    raster.setPixels(0, 0, w, h, pixels);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(sepia, type, baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        }
        catch (IOException ioe) {
            return null; 
        }
    }
    
    /**
     * Inverts the colours of an image by RGB value (255 to 0, etc)
     * @param picid The image to transform
     * @param type the type of the image
     * @return The transformed picture as a byte array
     */
    public byte[] picInvert(String picid, String type) {
        try {
            BufferedImage img = ImageIO.read(new File("/var/tmp/instagrimpjp/" + picid));
            BufferedImage sepia = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

            int w = img.getWidth();
            int h = img.getHeight();

            WritableRaster raster = sepia.getRaster();

            // We need 3 integers (for R,G,B color values) per pixel.
            int[] pixels = new int[w * h * 3];
            img.getRaster().getPixels(0, 0, w, h, pixels);

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    int rgb = img.getRGB(x, y); //get rgb value
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    int gry = (r + g + b) / 3; //get greyscale/average

                    b = 255 - b;
                    r = 255 - r;
                    g = 255 - g;

                    color = new Color(r, g, b, color.getAlpha());
                    sepia.setRGB(x, y, color.getRGB());
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(sepia, type, baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        }
        catch (IOException ioe) {
            return null;
        }
    }
    
    /**
     * Resizes and antialiases an image into a thumbnail
     * @param img The image to transform
     * @return The thumbnail of the image
     */
    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, 250, OP_ANTIALIAS);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    
    /**
     * Processes image - resizes, performs anti-aliasing and adds a small border
     * @param img The image to process
     * @param mode The operation to perform
     * @return The processed image
     */
    public static BufferedImage createProcessed(BufferedImage img, java.awt.image.BufferedImageOp mode) {
        int Width=img.getWidth()-1;
        img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, mode);
        return pad(img, 4);
    }
   
    /**
     * Returns a list of all the user's images
     * @param User the user to return images for
     * @return A linked list of all the user's pictures
     */
    public java.util.LinkedList<Pic> getPicsForUser(String User) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrim_PJP");
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
        return Pics;
    }
    
    public String[] getData(String picid) {
        String[] toReturn = new String[2];
        cluster = CassandraHosts.getCluster();
        Session session = cluster.connect("instagrim_PJP");
        java.util.UUID uuid = java.util.UUID.fromString(picid);
        
        PreparedStatement ps = session.prepare("select user,pic_added from userpiclist where picid =?");
        BoundStatement boundStatement = new BoundStatement(ps);
            ResultSet rs = session.execute( boundStatement.bind(uuid) );
            
            Row row = rs.one();
            toReturn[0] = row.getString("user");
            toReturn[1] = row.getString("pic_added");
            
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
        Session session = cluster.connect("instagrim_PJP");
        
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        String user = "";
        String date = "";
        
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
//            
//            if (image_type == Convertors.DISPLAY_IMAGE) {
//                
//                ps = session.prepare("select image,imagelength,type from pics where picid =?");
//            } else if (image_type == Convertors.DISPLAY_THUMB) {
//                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
//            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
//                ps = session.prepare("select processed,processedlength,type from pics where picid =?");
//            }
            
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));

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
                    date = row.getString("interaction_time");
//                    if (image_type == Convertors.DISPLAY_IMAGE) {
//                        bImage = row.getBytes("image");
//                        length = row.getInt("imagelength");
//                    } else if (image_type == Convertors.DISPLAY_THUMB) {
//                        bImage = row.getBytes("thumb");
//                        length = row.getInt("thumblength");
//                
//                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
//                        bImage = row.getBytes("processed");
//                        length = row.getInt("processedlength");
//                    }
                    
                    type = row.getString("type");

                }
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
            return null;
        }
        session.close();
        
        Pic p = new Pic();
        p.setPic(bImage, length, type, user, date);
        p.setUUID(picid);

        return p;
    }

}
