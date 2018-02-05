package org.lpro.boundary.taille.mapper;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.lpro.entity.Tailles;

@Stateless
@Transactional
public class TailleManager
{
    @PersistenceContext
    EntityManager em;

    public Tailles findById(long id)
    {
        return this.em.find(Tailles.class, id);
    }

    public List<Tailles> findAll()
    {
        Query q = this.em.createNamedQuery("Tailles.findAll", Tailles.class);
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);

        return q.getResultList();
    }

    public Tailles save(Tailles t)
    {
        return this.em.merge(t);
    }

    public void delete(long id)
    {
        try
        {
            Tailles ref = em.getReference(Tailles.class, id);
            em.remove(ref);
        }
        catch(EntityNotFoundException e) {}
    }
}