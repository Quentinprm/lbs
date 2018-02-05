package org.lpro.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
@Entity
@NamedQuery(name="Tailles.findAll",query="SELECT t FROM Tailles t")
public class Tailles implements Serializable{
    @Id
    @GeneratedValue
    private long id;
    @NotNull
    private String nom;
    @NotNull
    private float prix;
    @ManyToMany
    private List<Sandwich> sandwichs;

    public Tailles(){

    };

    public Tailles(long id,String n,float p){
        this.id=id;
        this.nom=n;
        this.prix=p;
    }
    public long getId(){
        return this.id;
    }
    public String getNom(){
        return this.nom;
    }
    public float getPrix(){
        return this.prix;
    }
    public List<Sandwich> getSandwichs(){
        return sandwichs;
    }

    public void setSandwichs(List<Sandwich> s){
        this.sandwichs=s;
    }
    public void setId(long id){
        this.id=id;
    }
    public void setNom(String n){
        this.nom=n;
    }
    public void setPrix(long p){
        this.prix=p;
    }

}
