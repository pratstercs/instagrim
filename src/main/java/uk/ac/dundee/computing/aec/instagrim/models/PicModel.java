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
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));
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
                    processedb = picdecolour(picid.toString(),types[1]);
                    break;
                case INVERT:
                    processedb = picInvert(picid.toString(),types[1]);
                    break;
            }
            
            thumbb = picresize(processedb, types[1]);
            
            int thumblength= thumbb.length;
            ByteBuffer thumbbuf=ByteBuffer.wrap(thumbb);
            ByteBuffer processedbuf = ByteBuffer.wrap(processedb);
            
            int processedlength=b.length;
            Session session = cluster.connect("instagrim");

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
    
    public byte[] picresize(byte[] pic, String type) throws IOException {
            InputStream in = new java.io.ByteArrayInputStream(pic);
            BufferedImage bImageFromConvert = ImageIO.read(in);
            return picresize(bImageFromConvert, type);
    }
    public byte[] picresize(String picid, String type) throws IOException {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            return picresize(BI, type);
    }
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
    
        public byte[] picConvert(String picid,String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
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
    
    public byte[] picdecolour(String picid,String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage processed = createProcessed(BI);
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
     * Adapted from picSepia
     */
    public byte[] picWeird(String picid, String type) {
        try {
            BufferedImage img = ImageIO.read(new File("/var/tmp/instagrim/" + picid));

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
     * Adapted from http://stackoverflow.com/questions/21899824/java-convert-a-greyscale-and-sepia-version-of-an-image-with-bufferedimage
     * In turn from https://groups.google.com/forum/#!topic/comp.lang.java.programmer/nSCnLECxGdA
     */
    public byte[] picSepia(String picid, String type) {
        try {
            BufferedImage img = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
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
    
    public byte[] picInvert(String picid, String type) {
        try {
            BufferedImage img = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
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
    
    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, 250, OP_ANTIALIAS);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    
   public static BufferedImage createProcessed(BufferedImage img) {
        int Width=img.getWidth()-1;
        img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(img, 4);
    }
   
    public java.util.LinkedList<Pic> getPicsForUser(String User) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrim");
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

        public Pic getPic(int image_type, java.util.UUID picid) {
        cluster = CassandraHosts.getCluster();
        Session session = cluster.connect("instagrim");
        
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        
        try {
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
         
            if (image_type == Convertors.DISPLAY_IMAGE) {
                
                ps = session.prepare("select image,imagelength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_THUMB) {
                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                ps = session.prepare("select processed,processedlength,type from pics where picid =?");
            }
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));

            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");
                
                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }
                    
                    type = row.getString("type");

                }
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
            return null;
        }
        session.close();
        Pic p = new Pic();
        p.setPic(bImage, length, type);
        p.setUUID(picid);

        return p;

    }

}
