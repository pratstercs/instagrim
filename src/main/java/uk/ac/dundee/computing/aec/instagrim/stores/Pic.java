package uk.ac.dundee.computing.aec.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;
import java.util.Date;

/*
 * Class to store pictures
 */
public class Pic {

    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private java.util.UUID UUID=null;
    private String user = null;
    private Date timestamp = null;
    
    /**
     * Constructor
     */
    public void Pic() {

    }
    /**
     * Method to set the picture's UUID
     * @param UUID The UUID to be set to
     */
    public void setUUID(java.util.UUID UUID){
        this.UUID = UUID;
    }
    /**
     * Returns the picture's UUID
     * @return The UUID of the picture as a String
     */
    public String getSUUID(){
        return UUID.toString();
    }
    /**
     * Sets the actual image data of the picture
     * @param bImage The bytebuffer of the image
     * @param length The length of the buffer
     * @param type The type of the buffer
     */
    public void setPic(ByteBuffer bImage, int length,String type) {
        this.bImage = bImage;
        this.length = length;
        this.type=type;
    }
    public void setPic(ByteBuffer bImage, int length,String type, String user, Date date) {
        this.bImage = bImage;
        this.length = length;
        this.type = type;
        this.user = user;
        timestamp = date;
    }
    
    public String getUser() {
        return user;
    }
    public Date getDate() {
        return timestamp;
    }
    
    public void setUser(String newUser) {
        user = newUser;
    }
    public void setDate(Date newDate) {
        timestamp = newDate;
    }

    /**
     * Returns the buffer of the image data
     * @return 
     */
    public ByteBuffer getBuffer() {
        return bImage;
    }

    /***
     * Returns the length of the image
     * @return The image length
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Returns the type of the image
     * @return The type of the image
     */
    public String getType(){
        return type;
    }

    /**
     * Returns the image data as a byte array
     * @return The byte array containing the image data
     */
    public byte[] getBytes() {
         
        byte image[] = Bytes.getArray(bImage);
        return image;
    }

}
