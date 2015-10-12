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
    
    public void LoggedIn(){
        
    }
    
    public void clearData() {
        loggedin=false;
    
        Username=null;
        firstName=null;
        lastName=null;
        email=null;
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
    
    public void setLoginState(boolean loggedin){
        this.loggedin=loggedin;
    }
    public boolean getlogedin(){
        return loggedin;
    }
}
