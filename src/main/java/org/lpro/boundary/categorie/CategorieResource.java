package org.lpro.boundary.categorie;
import org.lpro.boundary.categorie.exception.CategorieNotFound;
import org.lpro.entity.Categorie;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.*;
import javax.json.JsonArrayBuilder;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;
import org.lpro.boundary.sandwich.SandwichManager;
import org.lpro.boundary.sandwich.SandwichResource;
import org.lpro.entity.Sandwich;
@Stateless
@Path("categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategorieResource {

    @Inject
    CategorieManager cm;
     @Inject
     SandwichManager sm;
    @Context
    UriInfo uriInfo;

    @GET
    public Response getCategories() {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("categories", this.getCategoriesList())
                .build();
        return Response.ok(json).build();
    }

    @POST
    public Response newCategorie(@Valid Categorie c) {
        Categorie cat = this.cm.save(c);
        long id = cat.getId();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + id).build();
        return Response.created(uri).build();
    }


    @DELETE
    @Path("{id}")
    public Response removeCategorie(@PathParam("id") long id) {
        this.cm.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Categorie update(@PathParam("id") long id, Categorie c) {
        c.setId(id);
        return this.cm.save(c);
    }

    private JsonArray getCategoriesList() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        this.cm.findAll().forEach((c) -> {
            jab.add(buildJson(c));
        });
        return jab.build();
    }

    private JsonObject buildJson(Categorie c) {
        return Json.createObjectBuilder()
                .add("id", c.getId())
                .add("nom", c.getNom())
                .add("description", c.getDescription())
                .build();
    }

    private JsonObject categorie2Json(Categorie c) {
        return Json.createObjectBuilder()
                .add("type", "resource")
                .add("categorie", this.buildJson(c))
                .build();
    }

    private JsonValue buildJsonSandwichUri(String uri)
    {
        return Json.createObjectBuilder()
                .add("href", uri)
                .add("rel", "sandwichs")
                .build();
    }

    @GET
    @Path("{id}")
    public Response getOneCategorie(@PathParam("id") long id)
    {
        return Optional.ofNullable(cm.findById(id))
                .map(c -> Response.ok(buildCategorieObject(c)).build())
                .orElseThrow(() -> new CategorieNotFound("Ressource indisponible "+ uriInfo.getPath()));
    }

    private JsonObject buildCategorieObject(Categorie c)
    {
        return Json.createObjectBuilder()
                .add("categorie", buildJsonForCategorie(c))
                .build();
    }

    private JsonValue buildJsonForCategorie(Categorie c)
    {
        String uriSelf = uriInfo.getBaseUriBuilder()
                .path(CategorieResource.class)
                .path(c.getId() + "")
                .build()
                .toString();

        String uriSandwichs = uriInfo.getBaseUriBuilder()
                .path(CategorieResource.class)
                .path(c.getId() + "")
                .path(SandwichResource.class)
                .build()
                .toString();

        JsonArrayBuilder links = Json.createArrayBuilder();
        links.add(buildJsonSelfUri(uriSelf));
        links.add(buildJsonSandwichUri(uriSandwichs));

        JsonArrayBuilder sandwichs = Json.createArrayBuilder();
        c.getSandwich().forEach( s ->
        {
            sandwichs.add(buildJsonForSandwich(s));
        });

        return Json.createObjectBuilder()
                .add("id", c.getId())
                .add("nom", c.getNom())
                .add("description", c.getDescription())
                .add("sandwichs", sandwichs.build())
                .add("links", links)
                .build();
    }
    private JsonValue buildJsonSelfUri(String uri) {
        return Json.createObjectBuilder()
                .add("href", uri)
                .add("rel", "self")
                .build();
    }

    private JsonValue buildJsonForSandwich(Sandwich s)
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
