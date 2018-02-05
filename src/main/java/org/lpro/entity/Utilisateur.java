package org.lpro.entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

public class Utilisateur implements Serializable{
    @Id
    @NotNull
    private String username;
    @NotNull
    private String password;
    public Utilisateur(){

    }
    public Utilisateur(String u,String p){
        this.username=u;
        this.password=p;
    }

    public String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }
    public void setUsername(String u){
        this.username=u;
    }
    public void setPassword(String p){
        this.password=p;
    }
}
