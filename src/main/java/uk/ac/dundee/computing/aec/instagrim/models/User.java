/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
//import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.*;
//import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
//import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
//import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import com.datastax.driver.core.Cluster;
//import java.io.IOException;
//import java.io.PrintWriter;
//import javax.servlet.RequestDispatcher;
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
//import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
//import com.datastax.driver.core.UserType;
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
    
    public boolean updateUser(String username, String firstName, String lastName, String email, String[] address) {
        cluster = CassandraHosts.getCluster();
        Session session = cluster.connect("instagrim");
        
        try {
            UserType addressUDT = session.getCluster().getMetadata().getKeyspace("instagrim").getUserType("address"); //get actual UDTValue type
            UDTValue addressDB = addressUDT.newValue().setString("street", address[0]).setString("city", address[1]).setString("postcode",address[2]); //make new addressUDT
            Map<String, UDTValue> addressMap = new HashMap<String, UDTValue>(); //make map to hold addressUDT
            addressMap.put("Home", addressDB); //put into map
        
        Statement st = QueryBuilder.update("instagrim","userprofiles")
                .with(
                            QueryBuilder.set("first_name",firstName))
                       .and(QueryBuilder.set("last_name",lastName))
                       .and(QueryBuilder.set("email",email))
                       .and(QueryBuilder.set("addresses",addressMap)
                     )
                .where(QueryBuilder.eq ("login",username));
        session.execute(st);
        
        }
        catch (ArrayIndexOutOfBoundsException e) {
            
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
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,password,email) Values(?,?,?)");
       
        BoundStatement boundStatement = new BoundStatement(ps);
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username,EncodedPassword,email));
        //We are assuming this always works.  Also a transaction would be good here !
        
        return true;
    }
    
    public LoggedIn getUserData(LoggedIn lg) {
        String username = lg.getUsername();
        
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select * from userprofiles where login =?");
        //ResultSet rs;
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        
        //convert result database to single row
        Row row = rs.one();
        //set values from returned data
        lg.setUsername(row.getString("login"));
        lg.setFirstName(row.getString("first_name"));
        lg.setLastName(row.getString("last_name"));
        lg.setEmail(row.getString("email"));
        
        //UserType addressUDT = session.getCluster().getMetadata().getKeyspace("instagrim").getUserType("address"); //get actual UDTValue type
        
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
        
        return lg;
    }
    
    public static String encodePass(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        return sha1handler.SHA1(password);
    }
    
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
        
        Session session = cluster.connect("instagrim");
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
    
    toReturn.clearData();
    return toReturn;
    }
    
       public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    
}
