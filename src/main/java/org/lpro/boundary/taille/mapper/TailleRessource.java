package org.lpro.boundary.taille.mapper;



import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.lpro.entity.Sandwich;
import org.lpro.boundary.taille.exception.TailleNotFound;
import org.lpro.entity.Tailles;
import org.lpro.boundary.sandwich.*;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.net.URI;
import javax.validation.Valid;
@Stateless
@Path("tailles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TailleRessource {
    @Inject
    TailleManager tm;
    @Context
    UriInfo uriInfo;
    @GET
    public Response getTailles()
    {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("tailles", getTaillesList())
                .build();

        return Response.ok(json).build();
    }

    private JsonArray getTaillesList()
    {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        this.tm.findAll().forEach( t ->
        {
            jab.add(buildJson(t));
        });

        return jab.build();
    }

    private JsonValue buildJson(Tailles t)
    {
        return Json.createObjectBuilder()
                .add("id", t.getId())
                .add("nom", t.getNom())
                .add("prix", t.getPrix())
                .build();
    }



    @POST
    public Response newTaille(@Valid Tailles t)
    {
        Tailles newOne = tm.save(t);
        long id = newOne.getId();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + id).build();

        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    public Response suppression(@PathParam("id") long id)
    {
        tm.delete(id);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Tailles update(@PathParam("id") long id, Tailles t)
    {
        t.setId(id);

        return tm.save(t);
    }


    @GET
    @Path("{id}/sandwichs")
    public Response getSandwichByTaille(@PathParam("id") long id)
    {
        return Optional.ofNullable(tm.findById(id))
                .map(t -> Response.ok(buildSandwichByTaille(t)).build())
                .orElseThrow(() -> new TailleNotFound("Ressource indisponible" + uriInfo.getPath()));
    }

    private JsonObject buildSandwichByTaille(Tailles t)
    {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        t.getSandwichs().forEach(s ->
        {
            jab.add(buildJsonForSandwichs(s));
        });

        return Json.createObjectBuilder()
                .add("sandwichs", jab.build())
                .build();
    }




    @GET
    @Path("{id}")
    public Response getOneTaille(@PathParam("id") long id )
    {
        return Optional.ofNullable(tm.findById(id))
                .map(t -> Response.ok(buildTailleObject(t)).build())
                .orElseThrow(() -> new TailleNotFound("Ressource indisponible " + uriInfo.getPath()));
    }

    private Object buildTailleObject(Tailles t)
    {
        return Json.createObjectBuilder()
                .add("taille", buildJsonForTaille(t))
                .build();
    }

    private JsonValue buildJsonForTaille(Tailles t)
    {
        String uriSelf = uriInfo.getBaseUriBuilder()
                .path(TailleRessource.class)
                .path(t.getId() + "")
                .build()
                .toString();

        String uriSandwichs = uriInfo.getBaseUriBuilder()
                .path(TailleRessource.class)
                .path(t.getId() + "")
                .path(SandwichResource.class)
                .build()
                .toString();

        JsonArrayBuilder links = Json.createArrayBuilder();
        links.add(buildJsonSelfUri(uriSelf));
        links.add(buildJsonSandwichUri(uriSandwichs));

        JsonArrayBuilder sandwichs = Json.createArrayBuilder();
        t.getSandwichs().forEach( s ->
        {
            sandwichs.add(buildJsonForSandwichs(s));
        });

        return Json.createObjectBuilder()
                .add("id", t.getId())
                .add("nom", t.getNom())
                .add("prix", t.getPrix())
                .add("sandwichs", sandwichs)
                .add("links", links)
                .build();
    }

    private JsonValue buildJsonSandwichUri(String uriSandwichs)
    {
        return Json.createObjectBuilder()
                .add("href", uriSandwichs)
                .add("rel", "sandwichs")
                .build();
    }

    private JsonValue buildJsonSelfUri(String uriSelf)
    {
        return Json.createObjectBuilder()
                .add("href", uriSelf)
                .add("rel", "self")
                .build();
    }

    private JsonValue buildJsonForSandwichs(Sandwich s)
    {
        String uriSandwich = uriInfo.getBaseUriBuilder()
                .path(SandwichResource.class)
                .path(s.getId() + "")
                .build()
                .toString();

        JsonObject job = Json.createObjectBuilder()
                .add("href", uriSandwich)
                .add("rel", "self")
                .build();

        JsonArrayBuilder links = Json.createArrayBuilder();
        links.add(job);

        return Json.createObjectBuilder()
                .add("id", s.getId())
                .add("nom", s.getNom())
                .add("desc", s.getDescription())
                .add("type_pain", s.getTypePain())
                .add("img", s.getImg())
                .add("links", links)
                .build();
    }

}
