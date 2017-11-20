import java.io.File;
import java.io.Serializable;

public class FileRequestMessage extends AbstractMessage{

    File path;
    String fileName;

    public FileRequestMessage(File path, String fileName ) {
        this.name = "filereq";
        this.path = path;
        this.fileName = fileName;
    }

    public File getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

}
