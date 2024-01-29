package AdminPage.resource;

import Login_Register.model.User;
import Login_Register.resources.AuthResource;
import jakarta.enterprise.context.RequestScoped;
import org.jboss.logging.Logger;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.util.Set;

@Path("/admin")
@RequestScoped
public class AdminResource {

    @Context
    SecurityContext securityContext;

    private static final Logger LOG = Logger.getLogger(AuthResource.class);
    @GET
    @Path("/all_userinfo")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return User.listAll();
    }

    @GET
    @Path("/findByUsername")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserByUsername(@QueryParam("username") String username) {
        User user = User.findByUsername(username);
        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
    }

    @GET
    @Path("/GetUserBy{userId}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("userId") Long userId) {
        // Retrieve user information by userId
        User user = User.findById(userId);

        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
    }
    @Transactional
    @PUT
    @Path("/UpdateUserBy{userId}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("userId") Long userId, User updatedUser) {
        // Retrieve the existing user from the database
        User existingUser = User.findById(userId);

        if (!(existingUser == null)) {
            // Update the user information
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setRole(updatedUser.getRole());

            // Persist the updated user
            existingUser.persist();
            return Response.ok(existingUser).build();

        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
    }
    @Transactional
    @DELETE
    @Path("/DeleteUserBy{userId}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("userId") Long userId) {
        // Retrieve the existing user from the database
        User existingUser = User.findById(userId);

        if (existingUser != null) {
            // Store details of the user before deletion
            String deletedUsername = existingUser.getUsername();
            String deletedEmail = existingUser.getEmail();
            String deletedRole = existingUser.getRole();

            // Delete the user from the database
            existingUser.delete();

            // Create a response with details about the deleted user
            String responseMessage = String.format(
                    "User with ID %d deleted successfully. Details: Username=%s, Email=%s, Role=%s",
                    userId, deletedUsername, deletedEmail, deletedRole);

            return Response.ok(responseMessage).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
    }
}
