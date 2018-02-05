package org.lpro.provider;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
@Provider
@PreMatching
public class CORSRequestFilter implements ContainerRequestFilter{
    private final static Logger log= Logger.getLogger(CORSRequestFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext rc) throws IOException{
        log.log(Level.INFO,"Excution filtre Request: {0}",rc.getRequest().getMethod());
        if(rc.getRequest().getMethod().equals("OPTIONS")){
            log.info("Detection HTTP Method(OPTIONS)");
            rc.abortWith(Response.status(Response.Status.OK).build());
        }
    }
}
