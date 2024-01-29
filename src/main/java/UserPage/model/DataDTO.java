package UserPage.model;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

public class DataDTO {
    @FormParam("DataName")
    @PartType("text/plain")
    private String dataName;

    @FormParam("file")
    @PartType("application/octet-stream")
    private InputStream fileData;

    @FormParam("fileName")
    @PartType("text/file")
    private String fileName;


    // Getters and Setters
    public InputStream getFileData() {
        return fileData;
    }

    public void setFileData(InputStream fileData) {
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    // The setFileName method is not needed anymore
    // as the filename is set from the Content-Disposition header

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
