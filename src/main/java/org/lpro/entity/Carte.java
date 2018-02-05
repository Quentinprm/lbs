package org.lpro.entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)


public class Carte implements Serializable{
    @Id
    private String id;
    private float reduc;
    private float montant;

    @OneToMany(mappedBy = "carte")
    private List<Commande> commandes;

    public Carte(){

    }

    public Carte (String id,float r,float m){
        this.id=id;
        this.reduc=r;
        this.montant=m;
    }
    public String getUid(){
        return this.id;
    }
    public float getMontant(){
        return this.montant;
    }
    public float getReduction(){
        return this.reduc;
    }
    public void setUid(String id){
        this.id=id;
    }
    public void setMontant(float m){
        this.montant=m;
    }
    public void setReduction(float r){
        this.reduc=r;
    }
}
