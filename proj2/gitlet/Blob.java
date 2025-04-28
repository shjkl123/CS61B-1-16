package gitlet;

import java.io.File;
import java.io.Serializable;

//wait
public class Blob implements Serializable {

    private File file;
    private byte[] fileByte;
    private final String id;
    private final String fileName;
    private final String fileContent;
    Blob(File file) {
        this.file = file;
        fileByte = Utils.readContents(file);
        id = generateId();
        fileName = file.getName();
        fileContent = Utils.readContentsAsString(file);
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return file.getName();
    }

    public String getFileContent() {
        return fileContent;
    }

    private String generateId() {
        return Utils.sha1(fileByte, file.getName());
    }

    @Override
    public String toString() {
        return id;
    }

    public void saveBlob() {
        String id = generateId();
        File dir = Utils.join(Repository.GITLET_BLOBS, id.substring(0, 2));
        if (!dir.exists()) dir.mkdir();
        File file = Utils.join(dir, id.substring(2));
        Utils.writeObject(file, this);
    }
}
