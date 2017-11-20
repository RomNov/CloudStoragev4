import java.io.Serializable;

public class DeleteMessage extends AbstractMessage{
    String fileName;

    public DeleteMessage(String fileName) {
        this.name = "delete";

        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }


}
