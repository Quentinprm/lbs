package org.lpro.provider;
import com.sun.xml.internal.ws.api.server.ContainerResolver;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@PreMatching

public class CORSResponseFilter implements ContainerResponseFilter {
    private final static Logger log=Logger.getLogger(CORSResponseFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext cr, ContainerResponseContext crc) throws IOException{
        log.log(Level.INFO,"Excution filtre Response: {0}",cr.getRequest().getMethod());
        crc.getHeaders().add("Access-Control-Allow-Origin", "*");
        crc.getHeaders().add("Access-Control-Allow-Credentials", "true");
        crc.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
    }
}
