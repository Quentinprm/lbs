package org.lpro.boundary.taille.mapper;
import org.lpro.boundary.taille.exception.TailleNotFound;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;
import javax.json.Json;
import javax.ws.rs.ext.Provider;

@Provider
public class TailleNotFoundMapper implements ExceptionMapper<TailleNotFound> {
    public Response toResponse(TailleNotFound exception)
    {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorJson(exception))
                .build();
    }

    private Object errorJson(TailleNotFound exception)
    {
        return Json.createObjectBuilder()
                .add("error", Response.Status.NOT_FOUND.getStatusCode())
                .add("message", exception.getMessage())
                .build();
    }
}
