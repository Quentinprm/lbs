package org.lpro.boundary.sandwich;

import org.lpro.boundary.categorie.CategorieManager;
import org.lpro.boundary.sandwich.exception.SandwichNotFound;
import org.lpro.entity.Sandwich;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import org.lpro.entity.Tailles;
import org.lpro.entity.Categorie;
import org.lpro.boundary.taille.mapper.TailleRessource;
import org.lpro.boundary.categorie.CategorieResource;

@Stateless
@Path("sandwichs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SandwichResource {

    @Inject
    SandwichManager sm;
    @Inject
    CategorieManager cm;
    @Context
    UriInfo uriInfo;

    @GET
    public Response getSandwichs(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int nbPerPage,
            @QueryParam("t") String type_pain,
            @DefaultValue("0") @QueryParam("img") int img
    ) {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("meta", this.sm.getMetaPerPage(-1, type_pain, img, page, nbPerPage))
                .add("sandwichs", this.getSandwichsList(this.sm.findWithParam(this.sm.createQuery(type_pain, img), page, nbPerPage)))
                .build();
        return Response.ok(json).build();
    }


    @GET
    @Path("{id}")
    public Response getOneSandwich(@PathParam("id") String id, @Context UriInfo uriInfo) {
        return Optional.ofNullable(sm.findById(id))
                .map(s -> Response.ok(sandwich2Json(s)).build())
                .orElseThrow(() -> new SandwichNotFound("Ressource non disponible" + uriInfo.getPath()));
    }

    @POST
    public Response newSandwich(@Valid Sandwich s, @Context UriInfo uriInfo) {
        Sandwich sand = this.sm.save(s);
        String id = sand.getId();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + id).build();
        return Response.created(uri).build();
    }


    @DELETE
    @Path("{id}")
    public Response removeSandwich(@PathParam("id") long id) {
        this.sm.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Sandwich update(@PathParam("id") String id, Sandwich s) {
        s.setId(id);
        return this.sm.save(s);
    }

    private JsonArray getSandwichsList(List<Sandwich> sandwichs) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        sandwichs.forEach((s) -> {
            jab.add(buildJson(s));
        });
        return jab.build();
    }

    private JsonObject buildJson(Sandwich s) {
        JsonObject details = Json.createObjectBuilder()
                .add("id", s.getId())
                .add("nom", s.getNom())
                .add("description", s.getDescription())
                .add("type_pain", s.getTypePain())
                .build();

        JsonObject href = Json.createObjectBuilder()
                .add("href", ((s.getImg() == null) ? "" : s.getImg()))
                .build();

        JsonObject self = Json.createObjectBuilder()
                .add("self", href)
                .build();

        return Json.createObjectBuilder()
                .add("sandwich", details)
                .add("links", self)
                .build();
    }

    private JsonObject sandwich2Json(Sandwich s) {
        return Json.createObjectBuilder()
                .add("type", "resource")
                .add("sandwich", Json.createObjectBuilder()
                        .add("id", s.getId())
                        .add("nom", s.getNom())
                        .add("description", s.getDescription())
                        .add("type_pain", s.getTypePain())
                        .build())
                .add("links", Json.createObjectBuilder()
                        .add("self", Json.createObjectBuilder()
                                .add("href", ((s.getImg() == null) ? "" : s.getImg()))
                                .build())
                        .build())
                .build();
    }






    @GET
    @Path("{id}/tailles")
    public Response getTaillesBySandwich(@PathParam("id") String id)
    {
        return Optional.ofNullable(sm.findById(id))
                .map(s -> Response.ok(buildTailleBySandwich(s)).build())
                .orElseThrow(() -> new SandwichNotFound("Ressource indisponible " + uriInfo.getPath()));
    }

    private JsonObject buildTailleBySandwich(Sandwich s)
    {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        s.getTailles().forEach(t ->
        {
            jab.add(buildJsonForTaille(t));
        });

        return Json.createObjectBuilder()
                .add("tailles", jab.build())
                .build();
    }

    private JsonValue buildJsonForTaille(Tailles t)
    {
        String uriTailles = uriInfo.getBaseUriBuilder()
                .path(TailleRessource.class)
                .path(t.getId() + "")
                .build()
                .toString();

        JsonObject job = Json.createObjectBuilder()
                .add("href", uriTailles)
                .add("rel", "self")
                .build();

        JsonArrayBuilder links = Json.createArrayBuilder();
        links.add(job);

        return Json.createObjectBuilder()
                .add("id", t.getId())
                .add("nom", t.getNom())
                .add("prix", t.getPrix())
                .add("links", links)
                .build();
    }




    @GET
    @Path("{id}/categories")
    public Response getCategoriesBySandwich(@PathParam("id") String id)
    {
        return Optional.ofNullable(sm.findById(id))
                .map(s -> Response.ok(buildCategoryToSandwich(s)).build())
                .orElseThrow( () -> new SandwichNotFound("Ressource indisponible " + uriInfo.getPath()));
    }

    private JsonObject buildCategoryToSandwich(Sandwich s)
    {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        s.getCategorie().forEach( c ->
        {
            jab.add(buildJsonForCategory(c));
        });

        return Json.createObjectBuilder()
                .add("categories", jab.build())
                .build();
    }

    private JsonValue buildJsonForCategory(Categorie c)
    {
        String uriCategory = uriInfo.getBaseUriBuilder()
                .path(CategorieResource.class)
                .path(c.getId() + "")
                .build()
                .toString();

        JsonObject job = Json.createObjectBuilder()
                .add("href", uriCategory)
                .add("rel", "self")
                .build();

        JsonArrayBuilder links = Json.createArrayBuilder();
        links.add(job);

        return Json.createObjectBuilder()
                .add("id", c.getId())
                .add("nom", c.getNom())
                .add("desc", c.getDescription())
                .add("links", links)
                .build();
    }


    @DELETE
    @Path("{id}")
    public Response suppression(@PathParam("id") long id)
    {
        sm.delete(id);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
