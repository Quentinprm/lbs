package org.lpro.boundary.carte;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import org.lpro.entity.Carte;
@Stateless
@Transactional

public class CarteManager {
    @PersistenceContext
    EntityManager em;

    public Carte findById(String id){
        return this.em.find(Carte.class,id);
    }

    public Carte save(Carte c){
        return this.em.merge(c);
    }
}
