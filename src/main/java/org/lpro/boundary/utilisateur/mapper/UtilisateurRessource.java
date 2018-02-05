package org.lpro.boundary.utilisateur.mapper;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import org.lpro.control.PasswordManagement;

import org.lpro.entity.Utilisateur;
@Stateless
@Path("User")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class UtilisateurRessource {
    @Inject
    UtilisateurManager um;
    @Context
    UriInfo uriInfo;

    @POST
    public Response newUser(@Valid Utilisateur u){
        u.setPassword(PasswordManagement.digestPassword(u.getPassword()));
        Utilisateur user=um.save(u);
        URI uri =uriInfo.getAbsolutePathBuilder().path("/").build();
        return Response.created(uri).build();
    }
}
