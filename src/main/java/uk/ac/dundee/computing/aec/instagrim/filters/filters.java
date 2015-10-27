package uk.ac.dundee.computing.aec.instagrim.filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static org.imgscalr.Scalr.pad;
import static org.imgscalr.Scalr.resize;

/**
 * Class to store all the static filtering methods
 * @author Phil
 */
public class filters {
    
    /**
     * Accessor method for picResize using a byte array
     * @param pic The byte array to resize
     * @param type The type of the image
     * @return The resized image as a byte array
     * @throws IOException If image stream does not exist
     */
    public static byte[] picresize(byte[] pic, String type) throws IOException {
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
    public static byte[] picresize(String picid, String type) throws IOException {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrimpjp/" + picid));
            return picresize(BI, type);
    }
    /**
     * Resizes images when passed a BufferedImage
     * @param pic The image to resize
     * @param type The type of the image
     * @return The resized image as a byte array
     */
    private static byte[] picresize(BufferedImage pic, String type) {
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
    public static byte[] picConvert(String picid, String type) {
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
    public static byte[] picdecolour(String picid, String type, java.awt.image.BufferedImageOp mode) {
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
    public static byte[] picWeird(String picid, String type) {
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
    public static byte[] picSepia(String picid, String type) {
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
    public static byte[] picInvert(String picid, String type) {
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
        img = resize(img, Scalr.Method.SPEED, 250, OP_ANTIALIAS);
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
        img = resize(img, Scalr.Method.SPEED, Width, OP_ANTIALIAS, mode);
        return pad(img, 4);
    }
}
