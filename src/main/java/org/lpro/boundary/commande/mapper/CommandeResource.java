package org.lpro.boundary.commande.mapper;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.UUID;
import org.lpro.control.RandomToken;
import org.lpro.entity.Commande;
@Stateless
@Transactional
public class CommandeResource {
    @PersistenceContext
        EntityManager em;

    public Commande findById(String id){
        return this.em.find(Commande.class,id);
    }

    public List<Commande> findAll(){
            Query query=this.em.createQuery("SELECT c From Commande c");
            query.setHint("javax.persistance.cache.storeMode",CacheStoreMode.REFRESH);
            return query.getResultList();
    }

    public Commande save(Commande c){
        RandomToken token=new RandomToken();
        String t=token.randomString(64);

        c.setToken(t);
        c.setId(UUID.randomUUID().toString());
        return this.em.merge(c);
    }
}
