import java.io.Serializable;

public class RenameMessage extends AbstractMessage{
    String newName;
    String oldName;

    public RenameMessage(String newName, String oldName) {
        this.name = "rename";
        this.newName = newName;
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public String getOldName() {
        return oldName;
    }
}
