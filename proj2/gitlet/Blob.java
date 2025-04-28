package gitlet;

import java.io.File;
import java.io.Serializable;


public class Blob implements Serializable {

    private File file;
    private byte[] fileByte;
    private String id;

    Blob(File file) {
        this.file = file;
        fileByte = Utils.readContents(file);
        id = generateId();
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return file.getName();
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
