import java.io.*;

public class FileMessage extends AbstractMessage{

    String fileName;
    byte fileBody[];

    public FileMessage(File file) {
        this.fileName = file.getName();
        this.name = "file";
        try {
            FileInputStream fis = new FileInputStream(file);
            if(fis.available() <= 5242880) {
                fileBody = new byte[fis.available()];
                fis.read(fileBody);
                fis.close();
            }

        } catch (FileNotFoundException e) {
            System.out.println("файл не найден");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("Ошибка ввода-вывода");
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileBody() {
        return fileBody;
    }

}
