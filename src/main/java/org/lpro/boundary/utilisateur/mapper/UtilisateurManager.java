package org.lpro.boundary.utilisateur.mapper;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.lpro.entity.Utilisateur;
@Stateless
@Transactional

public class UtilisateurManager {
    @PersistenceContext
    EntityManager em;
    public Utilisateur findByUsername(String n){
        return this.em.find(Utilisateur.class,n);
    }

    public Utilisateur save(Utilisateur u){
        return this.em.merge(u);
    }
}
