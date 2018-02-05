package org.lpro.boundary.commande;
import javax.ejb.Stateless;
import java.net.URI;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import org.lpro.boundary.sandwich.SandwichManager;
import org.lpro.boundary.carte.CarteManager;
import org.lpro.boundary.taille.TailleManager;
import org.lpro.control.KeyManagement;
import org.lpro.entity.Carte;
import org.lpro.entity.Commande;
import org.lpro.entity.Sandwich;
import org.lpro.entity.Tailles;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommandeManager {
    @Inject
    CommandeResource cr;
    @Inject
    SandwichManager sm;
    @Inject
    CarteManager cm;
    @Inject
    TailleManager tm;
    @Inject
    KeyManagement km;
    @Context
    UriInfo uriInfo;

    @GET
    @Path("/{commandeId}")
    public Response getOneCommand(@PathParam("commandeId") String id, @DefaultValue("")@QueryParam("token") String param,@DefaultValue(("")) @HeaderParam("X-lbs-token") String header){
        Commande cm= cr.findById(id);
        if(cm==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if(param.isEmpty() && header.isEmpty()){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        String token=(param.isEmpty()) ? header : param;
        boolean isValid=cm.getToken().equals(token);
        if(isValid){
            return Response.ok(buildCommandObj(cm)).build();
        }else{
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private JsonObject buildCommandObj(Commande c){
        return Json.createObjectBuilder().add("commande",buildJsonCommand(c)).build();
    }

    private JsonObject buildJsonCommand(Commande c){
        return Json.createObjectBuilder().add("id",c.getId()).add("nom_client",c.getNom()).add("mail_client",c.getMail()).add("livraison",buildJsonLivraison(c)).add("token",c.getToken()).build();
    }
    private JsonObject buildJsonLivraison(Commande c){
        return Json.createObjectBuilder()
                .add("date", c.getDateLivraison())
                .add("heure", c.getHeureLivraison()).build();
    }
    @POST
    public Response addCommande(@Valid Commande c,@DefaultValue("") @QueryParam("card") String id,@DefaultValue("") @HeaderParam("Authorization") String h){
        String date= c.getDateLivraison()+" "+c.getDateLivraison();
        TimeZone t=TimeZone.getTimeZone("Europe/Paris");
        DateFormat format=new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setTimeZone(t);
        Date dateCommande;
        try{
            dateCommande=format.parse(date);
        }catch(ParseException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Date ajd=new Date();
        if(dateCommande.compareTo(ajd)<=0){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(!h.isEmpty()&&h.startsWith("Bearer ")){
            String token=h.substring("Bearer ".length()).trim();
            try{
                Key k= KeyManagement.generateKey();
                Jws<Claims> claims = Jwts.parser().setSigningKey(k).parseClaimsJws(token);
                String tokenCardId = (String) claims.getBody().get("carte");
                if(tokenCardId.equals(id))
                {
                    Carte carte = cm.findById(tokenCardId);
                    c.setCarte(carte);
                }

            }catch(Exception e){
            }
        }
        Commande newCom = this.cr.save(c);
        URI uri = uriInfo.getAbsolutePathBuilder().path(newCom.getId()).build();
        return Response.created(uri)
                .entity(newCom)
                .build();
    }
    @PUT
    @Path("{id}")
    public Response changeCommandeDate(@PathParam("id") String id, @DefaultValue("") @HeaderParam("date") String d, @DefaultValue("") @HeaderParam("heure") String h)
    {
        Commande c = cr.findById(id);
        if(c == null) return Response.status(Response.Status.BAD_REQUEST).build();

        TimeZone tz = TimeZone.getTimeZone("Europe/Paris");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setTimeZone(tz);

        String newDateRaw = d + " " + h;

        Date newDateParsed;

        try
        {
            newDateParsed = format.parse(newDateRaw);
        }
        catch (ParseException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Date currentDate = new Date();

        if(newDateParsed.compareTo(currentDate) <= 0)
        {
            // Si la date précisée précède la date courante
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        c.setDateLivraison(d);
        c.setHeureLivraison(h);

        URI uri = uriInfo.getAbsolutePathBuilder().build();

        return Response.ok().location(uri).build();
    }
    @POST
    @Path("{id}")
    public Response payerCommande(@PathParam("id") String id,
                                  @DefaultValue("") @HeaderParam("numCarte") String num,
                                  @DefaultValue("") @HeaderParam("dateExpiration") String d)
    {
        Commande c = cr.findById(id);
        if(c == null)
        {
            JsonObject json = Json.createObjectBuilder()
                    .add("error", "The specified UID doesn't exist")
                    .build();

            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        if(c.isPayer())
        {
            JsonObject json = Json.createObjectBuilder()
                    .add("error", "This command has already been payed")
                    .build();

            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        Pattern patternNumCarte = Pattern.compile("\\d{16}");
        Pattern patternDateExpi = Pattern.compile("\\d{2}/\\d{2}");

        Matcher matcherNumCarte = patternNumCarte.matcher(num);
        Matcher matcherDateExpi = patternDateExpi.matcher(d);

        if(matcherNumCarte.find() && matcherDateExpi.find())
        {
            c.setPayer(true);

            URI uri = uriInfo.getAbsolutePathBuilder().build();

            return Response.ok().location(uri).build();
        }
        else
        {
            JsonObject json = Json.createObjectBuilder()
                    .add("error", "numCarte or dateExpiration isn't valid")
                    .build();

            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }
    }






    @GET
    @Path("private")
    public Response getListCommandes()
    {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("commandes", buildCommandes())
                .build();

        return Response.ok(json).build();
    }

    private JsonValue buildCommandes()
    {
        JsonArrayBuilder jb = Json.createArrayBuilder();

        List<Commande> commandes = cr.findAll();

        for(Commande c : commandes)
        {
            JsonObject json = Json.createObjectBuilder()
                    .add("id", c.getId())
                    .add("dateLivraison", c.getDateLivraison())
                    .add("heureLivraison", c.getHeureLivraison())
                    .add("links", buildLink(c))
                    .build();

            jb.add(json);
        }

        return jb.build();
    }

    private JsonValue buildLink(Commande c)
    {
        URI uri = uriInfo.getBaseUriBuilder().path("/commandes/" + c.getId() + "/private").build();

        JsonObject json = Json.createObjectBuilder()
                .add("self", uri.toString())
                .build();

        return json;
    }





    @POST
    @Path("{uid}/sandwich")
    public Response addSandwich(@PathParam("uid") String id,
                                @DefaultValue("") @QueryParam("uidSandwich") String idSandwich,
                                @QueryParam("uidTaille") long idTaille,
                                @QueryParam("nbSandwich") int nbSandwich)
    {
        // On vérifie que les uid sont bons
        boolean valide = !idSandwich.isEmpty() && idTaille > 0 && nbSandwich > 0;
        if(!valide)
            return Response.status(Response.Status.BAD_REQUEST).build();

        // On vérifie que les uid correspondent à quelque chose qui existe
        Commande c = cr.findById(id);
        Sandwich s = sm.findById(idSandwich);
        Tailles taille = tm.findById(idTaille);

        if(c == null || s == null || taille == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        // On ajoute la taille voulu au sandwich
        s.getTailles().add(taille);

        // On ajoute le sandwich a la commande
        for(int i = 0 ; i < nbSandwich ; i++)
        {
            c.getSandwich().add(s);
        }

        URI uri = uriInfo.getBaseUriBuilder().path("/" + id).build();

        return Response.ok().location(uri).build();
    }







    @PUT
    @Path("{uid}/private")
    public Response changeCommandeState(@PathParam("uid") String id)
    {
        Commande c = cr.findById(id);

        if(c.getSandwich() == null || c.getSandwich().isEmpty() || c.isPayer())
            return Response.status(Response.Status.BAD_REQUEST).build();

        c.setPayer(true);
        URI uri = uriInfo.getBaseUriBuilder().path("/commandes/" + id + "/private").build();

        return Response.ok().location(uri).build();
    }









    @GET
    @Path("{uid}/private")
    public Response getDetailsCommande(@PathParam("uid") String id)
    {
        // On vérifie que uid correspond à une commande existante
        Commande c = cr.findById(id);

        if(c == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        // On construit le json
        JsonObject json = Json.createObjectBuilder()
                .add("type", "ressource")
                .add("commande", jsonCommande(c))
                .build();

        return Response.ok(json).build();
    }

    private JsonValue jsonCommande(Commande c)
    {
        JsonObject json = Json.createObjectBuilder()
                .add("id", c.getId())
                .add("nom", c.getNom())
                .add("mail", c.getMail())
                .add("date", c.getDateLivraison())
                .add("heure", c.getHeureLivraison())
                .add("payé", c.isPayer())
                .add("sandwichs", jsonListSandwich(c))
                .add("links", buildLink(c))
                .build();

        return json;
    }

    private JsonValue jsonListSandwich(Commande c)
    {
        List<Sandwich> sandwichs = c.getSandwich();

        JsonArrayBuilder jb = Json.createArrayBuilder();

        for(Sandwich s : sandwichs)
        {
            JsonObject json = Json.createObjectBuilder()
                    .add("id", s.getId())
                    .add("nom", s.getNom())
                    .add("description", s.getDescription())
                    .add("type", s.getTypePain())
                    .add("tailles", jsonListTailles(s))
                    .build();

            jb.add(json);
        }

        return jb.build();
    }

    private JsonValue jsonListTailles(Sandwich s)
    {
        List<Tailles> tailles = s.getTailles();

        JsonArrayBuilder jab = Json.createArrayBuilder();

        for(Tailles t : tailles)
        {
            JsonObject json = Json.createObjectBuilder()
                    .add("nom", t.getNom())
                    .add("prix", t.getPrix())
                    .build();

            jab.add(json);
        }

        return jab.build();
    }







}
