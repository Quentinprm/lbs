package org.lpro.boundary.sandwich;

import org.lpro.entity.Sandwich;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.*;
import java.util.List;

@Stateless
public class SandwichManager {

    @PersistenceContext
    EntityManager em;

    public Sandwich findById(String id) {
        return this.em.find(Sandwich.class, id);
    }


    public List<Sandwich> findAll() {
        Query q = this.em.createNamedQuery("Sandwich.findAll", Sandwich.class);
        q.setHint("javax.persistance.cache.storeMode", CacheStoreMode.REFRESH);
        return q.getResultList();
    }



    public Query createQuery(String type_pain, int img) {
        String sql = "SELECT s FROM Sandwich s";

        if (type_pain != null) {
            if (img == 1) {
                sql += " WHERE s.type_pain = '" + type_pain + "' AND s.img != ''";
            } else {
                sql += " WHERE s.type_pain = '" + type_pain + "'";
            }
        } else {
            if (img == 1) {
                sql += " WHERE s.img != ''";
            }
        }

        return this.em.createQuery(sql);
    }

    public List<Sandwich> findWithParam(Query query, int p, int nbPerPage) {
        double nbSandwichs = query.getResultList().size();

        if (p <= 0) {
            p=1;
        }
        else if (p>Math.ceil(nbSandwichs / (double) nbPerPage)) {
            p=(int) Math.ceil(nbSandwichs / (double) nbPerPage);
        }

        query.setFirstResult((p-1) * nbPerPage);
        query.setMaxResults(nbPerPage);
        return query.getResultList();
    }

    public Sandwich save(Sandwich s) {
        s.setId(this.findAll().get(this.findAll().size() - 1).getId() + 1);
        return this.em.merge(s);
    }

    public void delete(long id) {
        try {
            Sandwich ref = this.em.getReference(Sandwich.class, id);
            this.em.remove(ref);
        } catch (EntityNotFoundException e) { }
    }

    public JsonObject getMeta(long size) {
        return Json.createObjectBuilder()
                .add("count", ((size == -1) ? this.findAll().size() : size))
                .add("date",  "05-12-2017")
                .build();
    }

    public JsonObject getMetaPerPage(long size, String ptype, int img, int p, int nbPerPage) {
        return Json.createObjectBuilder()
                .add("count", ((size == -1) ? this.createQuery(ptype, img).getResultList().size() : size))
                .add("size", this.findWithParam(this.createQuery(ptype, img),p,nbPerPage).size())
                .build();
    }


    public long count()
    {
        Query nb = em.createQuery("SELECT count(s.id) from Sandwich s");

        return (long) nb.getSingleResult();
    }
}
