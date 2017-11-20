import java.io.File;
import java.io.Serializable;

public class FileAnswerMessage extends AbstractMessage{

    File path;
    byte fileBody[];

    public FileAnswerMessage(File path) {
        this.name = "fileanswer";
        this.path = path;
    }

    public byte[] getFileBody() {
        return fileBody;
    }

    public void setFileBody(byte[] fileBody) {
        this.fileBody = fileBody;
    }

    public File getPath() {
        return path;
    }
}
