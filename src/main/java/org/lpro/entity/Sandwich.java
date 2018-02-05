package org.lpro.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import javax.persistence.ManyToMany;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="Sandwich.findAll",query="SELECT s FROM Sandwich s")
public class Sandwich implements Serializable {

    @Id
    private String id;

    @NotNull
    private String nom;

    @NotNull
    private String type_pain;

    @NotNull
    private String description;

    private String img;
    @ManyToMany(mappedBy="sandwich")
    private Set<Categorie> categorie = new HashSet<Categorie>();
    @ManyToMany(mappedBy="sandwich")
    private List<Tailles> tailles;
    @ManyToMany(mappedBy="sandwich")
    private List<Commande> commandes;
    public Sandwich() { }

    public Sandwich(String id, String nom, String type_pain, String description, String img) {
        this.id = id;
        this.type_pain = type_pain;
        this.description = description;
        this.img = img;
        this.categorie=new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTypePain() {
        return type_pain;
    }

    public void setTypePain(String type_pain) {
        this.type_pain = type_pain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<Tailles> getTailles(){
        return this.tailles;
    }
    public void setTailles(List<Tailles> t) {
        this.tailles = t;
    }
    public Set<Categorie> getCategorie()
    {
        return categorie;
    }

    public void setCategorie(Set<Categorie> categorie)
    {
        this.categorie = categorie;
    }
}
