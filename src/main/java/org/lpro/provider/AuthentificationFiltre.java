package org.lpro.provider;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.annotation.Priority;
import javax.inject.Inject;
import org.lpro.control.KeyManagement;
import io.jsonwebtoken.Jwts;
import java.security.Key;
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)

public class AuthentificationFiltre implements ContainerRequestFilter {
    @Inject
    private KeyManagement km;
    @Override
    public void filter(ContainerRequestContext rc){
        String h=rc.getHeaderString(HttpHeaders.AUTHORIZATION);
        if(h==null || !h.startsWith("Bearer ")){
            throw new NotAuthorizedException("Probleme header autorisation");

        }else{
            String t=h.substring("Bearer".length()).trim();
            try{
                Key k = km.generateKey();
                Jwts.parser().setSigningKey(k).parseClaimsJws(t);
                System.out.println(">>>> token valide : " + t);
            }catch(Exception e){
                System.out.println(">>>> token invalide : " + t);
                rc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }
    }
}
