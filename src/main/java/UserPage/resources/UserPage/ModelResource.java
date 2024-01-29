package UserPage.resources.UserPage;



import UserPage.model.DataDTO;
import UserPage.model.DataEntity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;


import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;


@Path("/UploadFile")
public class ModelResource {
    private static final Logger LOGGER = Logger.getLogger(ModelResource.class.getName());

    @ConfigProperty(name = "file.upload.dir")
    String uploadDir;

    @Inject
    SecurityContext securityContext;


    @POST
    @Transactional
    @Path("/add")
    @RolesAllowed("User")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDataEntity(@MultipartForm DataDTO dataUploadForm) {
        String username = securityContext.getUserPrincipal().getName();

        // Handle File Upload
        String userUploadDir = Paths.get(uploadDir, username, "upload").toString();
        File userDir = new File(userUploadDir);
        if (!userDir.exists() && !userDir.mkdirs()) {
            LOGGER.warning("Failed to create directory: " + userUploadDir);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create directory").build();
        }

        String filename = Paths.get(userUploadDir, dataUploadForm.getFileName()).toString();
        try (InputStream inputStream = dataUploadForm.getFileData();
             OutputStream outputStream = new FileOutputStream(filename)) {
            inputStream.transferTo(outputStream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving file for user: " + username, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving file").build();
        }

        // Create DataEntity and Persist
        DataEntity dataEntity = new DataEntity();
        dataEntity.setDataName(dataUploadForm.getDataName());
        dataEntity.setFileName(dataUploadForm.getFileName()); // Set the file name here
        dataEntity.persist();

        LOGGER.info("File uploaded and data entity created successfully for user: " + username);
        return Response.status(Response.Status.CREATED).entity(dataEntity).build();
    }

    @GET
    @Path("/get/{id}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataEntity(@PathParam("id") long id) {
        DataEntity dataEntity = DataEntity.findById(id);
        if (dataEntity == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("DataEntity not found").build();
        }
        return Response.ok(dataEntity).build();
    }


    @DELETE
    @Transactional
    @Path("/deleteDataBy{id}")
    @RolesAllowed({"User","Admin"})
    public Response deleteDataEntity(@PathParam("id") long id) {
        DataEntity entity = DataEntity.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("DataEntity not found").build();
        }

        entity.delete();
        return Response.noContent().build();
    }



    @POST
    @RolesAllowed("User")
    @Path("/predict")
    public Response predict(String fileName) {
        String username = securityContext.getUserPrincipal().getName();
        try {
            String inputPath = Paths.get(uploadDir, username, "upload", fileName).toString();
            String resultDir = Paths.get(uploadDir, username, "result").toString();
            String pngDir = Paths.get(uploadDir, username, "png").toString();

            new File(resultDir).mkdirs();
            new File(pngDir).mkdirs();

            runPredictionCommand(inputPath, resultDir, pngDir);

            return Response.ok("Prediction started").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    @GET
    @Path("/all")
    @RolesAllowed({"User", "Admin"}) 
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDataEntities() {
        List<DataEntity> allDataEntities = DataEntity.listAll();
        return Response.ok(allDataEntities).build();
    }

    @GET
    @RolesAllowed("User")
    @Path("/png/{fileName}")
    @Produces("image/png")
    public Response getPngFile(@PathParam("fileName") String fileName) {
        String username = securityContext.getUserPrincipal().getName();
        try {
            String pngFilePath = Paths.get(uploadDir, username, "png", fileName).toString();
            File file = new File(pngFilePath);

            if (!file.exists()) {
                return Response.status(Response.Status.NOT_FOUND).entity("File not found").build();
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            byte[] imageData = outputStream.toByteArray();
            return Response.ok(imageData).build();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading PNG file: " + fileName, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving file").build();
        }
    }



    private void runPredictionCommand(String inputPath, String outputPath, String pngOutputPath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "Predict.py", inputPath, outputPath, pngOutputPath);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Prediction script exited with error code: " + exitCode);
        }
    }
}

