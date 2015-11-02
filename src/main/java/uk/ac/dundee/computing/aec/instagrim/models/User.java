package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.*;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import com.datastax.driver.core.Cluster;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public class User {
    Cluster cluster;
    public User(){
        
    }
    
    public boolean updateUser(LoggedIn lg) {
        return updateUser(lg.getUsername(), lg.getFirstName(), lg.getLastName(), lg.getEmail(), lg.getAddress(), lg.getProfilePic());
    }
    
    public boolean updateUser(String username, String firstName, String lastName, String email, String[] address, java.util.UUID profilePic) {
        cluster = CassandraHosts.getCluster();
        Session session = cluster.connect("instagrimPJP");
        
        try {
            UserType addressUDT = session.getCluster().getMetadata().getKeyspace("instagrimPJP").getUserType("address"); //get actual UDTValue type
            UDTValue addressDB = addressUDT.newValue().setString("street", address[0]).setString("city", address[1]).setString("postcode",address[2]); //make new addressUDT
            Map<String, UDTValue> addressMap = new HashMap<String, UDTValue>(); //make map to hold addressUDT
            addressMap.put("Home", addressDB); //put into map
        
        Statement st = QueryBuilder.update("instagrimPJP","userprofiles")
                .with(
                            QueryBuilder.set("first_name",firstName))
                       .and(QueryBuilder.set("last_name",lastName))
                       .and(QueryBuilder.set("email",email))
                       .and(QueryBuilder.set("addresses",addressMap))
                       .and(QueryBuilder.set("profilepicid",profilePic)
                     )
                .where(QueryBuilder.eq ("login",username));
        session.execute(st);
        
        }
        catch (ArrayIndexOutOfBoundsException e) {
            
        }
        finally {
            session.close();
        }
        
        
        return false;
    }
    
    public boolean RegisterUser(String username, String Password, String email){
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't encode your password");
            return false;
        }
        Session session = cluster.connect("instagrimPJP");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,password,email) Values(?,?,?)");
       
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute( boundStatement.bind( username,EncodedPassword,email) );
        
        session.close();
        
        if ( rs.getAvailableWithoutFetching() == 0) {
            return false;
        }
        else {
            return true;
        }
    }
    
    /**
     * Completes the missing data for the passed LoggedIn object
     * @param lg The LoggedIn object to find data for
     * @return The complete LoggedIn object
     * @throws NullPointerException If user does not exist in database
     */
    public LoggedIn getUserData(LoggedIn lg) throws NullPointerException {
        String username = lg.getUsername();
        
        Session session = cluster.connect("instagrimPJP");
        PreparedStatement ps = session.prepare("select * from userprofiles where login =?");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute( boundStatement.bind(username) );
        
        //convert result database to single row
        Row row = rs.one();
        //set values from returned data
        lg.setUsername(row.getString("login"));
        lg.setFirstName(row.getString("first_name"));
        lg.setLastName(row.getString("last_name"));
        lg.setEmail(row.getString("email"));
        lg.setProfilePic(row.getUUID("profilePicId"));
        
        Object[] objAddress = row.getMap("addresses", String.class, UDTValue.class).values().toArray();
        String[] strAddress = new String[3];
        
        try {
            UDTValue address = (UDTValue)objAddress[0];
            strAddress[0] = address.getString("street");
            strAddress[1] = address.getString("city");
            strAddress[2] = address.getString("postcode");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            
        }
        
        lg.setAddress(strAddress);
        
        session.close();
        
        return lg;
    }
    
    /**
     * Encodes the password into a SHA1 encoded string
     * @param password The plaintext password to encode
     * @return A SHA1 encoded string of the password
     * @throws UnsupportedEncodingException Could not encode the password
     * @throws NoSuchAlgorithmException SHA1 does not exist
     */
    public static String encodePass(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        return sha1handler.SHA1(password);
    }
    
    /**
     * Checks if the user is valid in the database and returns a LoggedIn object for the user's data
     * @param username The username to check
     * @param Password The password to check
     * @return The LoggedIn object for that user - null if user does not exist
     */
    public LoggedIn IsValidUser(String username, String Password){
        LoggedIn toReturn = new LoggedIn();
        String encodedPassword = null;
        
        try {
            encodedPassword = encodePass(Password);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            
            toReturn.clearData();
            return toReturn;
        }
        
        Session session = cluster.connect("instagrimPJP");
        PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            
            toReturn.clearData();
            return toReturn;
        } else {
            for (Row row : rs) {
               
                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(encodedPassword) == 0) {
                    toReturn.setUsername(username);
                    toReturn.setPassword(encodedPassword);
                    LoggedIn newlg = getUserData(toReturn);
                    newlg.setLogedin();
                    return newlg;
                }
            }
        }
        session.close();
    
        toReturn.clearData();
        return toReturn;
    }
    
       public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    
}
