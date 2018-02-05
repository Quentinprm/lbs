package org.lpro.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;


@Entity
public class Commande implements Serializable{
    @Id
    private String id;
    @NotNull
    private String nom;
    @Pattern(regexp="(?:[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    private String mail;
    private String heureLivraison;
    private String dateLivraison;
    private String token;
    private boolean payer;

    @ManyToOne(fetch=FetchType.LAZY)
    private Carte carte;

    @ManyToMany
    private List<Sandwich> sandwichs;

    public Commande(){

    };

    public Commande(String n,String m,String hl,String dl){
        this.nom=n;
        this.mail=mail;
        this.heureLivraison=hl;
        this.dateLivraison=dl;
        this.payer=false;
    }
    public boolean isPayer(){
        return this.payer;
    }
    public String getId(){
        return this.id;
    }
    public String getToken(){
        return this.token;
    }
    public String getNom(){
        return this.nom;
    }
    public String getMail(){
        return this.mail;
    }
    public Carte getCarte(){
        return this.carte;
    }
    public String getHeureLivraison(){
        return this.heureLivraison;
    }
    public String getDateLivraison(){
        return this.dateLivraison;
    }
    public List<Sandwich> getSandwich(){
        return this.sandwichs;
    }
    public void setId(String id){
        this.id=id;
    }
    public void setToken(String t){
        this.token=t;
    }
    public void setPayer(boolean p){
        this.payer=p;
    }
    public void setSandwich(List<Sandwich>s){
        this.sandwichs=s;
    }
    public void setCarte(Carte c){
        this.carte=c;
    }
    public void setNom(String n){
        this.nom=n;
    }
    public void setMail(String m){
        this.mail=m;
    }
    public void setHeureLivraison(String h){
        this.heureLivraison=h;
    }
    public void setDateLivraison(String d){
        this.dateLivraison=d;
    }

}
