/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 *
 * @author Administrator
 */
public class LoggedIn {
    boolean loggedin=false;
    
    String Username=null;
    String firstName=null;
    String lastName=null;
    String email=null;
    String[] address = new String[3];
    private String encodedPass = null;
    
    public void LoggedIn(){
        
    }
    
    public void clearData() {
        loggedin=false;
    
        Username=null;
        firstName=null;
        lastName=null;
        email=null;
    }
    
    /**
     * Compares stored encoded password with new encoded password
     * @param compare The password, encoded the same way, to compare with
     * @return Whether they are the identical
     */
    public boolean comparePass(String compare) {
        return encodedPass.contentEquals(new StringBuffer(compare));
    }
    
    public void setUsername(String name){
        this.Username=name;
    }
    public String getUsername(){
        return Username;
    }
    public void setFirstName(String name){
        this.firstName=name;
    }
    public String getFirstName(){
        return firstName;
    }    
    public void setLastName(String name){
        this.lastName=name;
    }
    public String getLastName(){
        return lastName;
    }   
    public void setEmail(String name){
        this.email=name;
    }
    public String getEmail(){
        return email;
    }   
    public void setLogedin(){
        loggedin=true;
    }
    public void setLogedout(){
        loggedin=false;
    }
    public void setPassword(String newPass) {
        encodedPass = newPass;
    }
    
    public void setAddress(String[] newAddress) {
        try {
            setAddress(newAddress[0],newAddress[1],newAddress[2]);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array index out of bounds");
        }
    }
    public void setAddress(String line0, String line1, String line2) {
        address[0] = line0;
        address[1] = line1;
        address[2] = line2;
        
    }
    public String[] getAddress() {
        return address;
    }
    
    public void setLoginState(boolean loggedin){
        this.loggedin=loggedin;
    }
    public boolean getlogedin(){
        return loggedin;
    }
}
