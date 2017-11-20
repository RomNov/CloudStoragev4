import java.io.Serializable;
import java.util.ArrayList;

public class FileListMessage extends AbstractMessage{

    private ArrayList<String> fileList;

    public FileListMessage(ArrayList<String> fileList) {
        this.name = "filelist";
        this.fileList = fileList;

    }

    public ArrayList<String> getFileList() {
        return fileList;
    }

}
