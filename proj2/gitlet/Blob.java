package gitlet;

import java.io.File;
import java.io.Serializable;
import java.rmi.server.UID;

public class Blob implements Serializable {
    private final String fileName;
    private final byte[] fileByte;
    private final String filePath;
    private final String id;
    private final String fileContent;
    Blob(File file) {
        this.fileName = file.getName();
        filePath = file.getPath();
        fileByte = Utils.readContents(file);
        id = generateId();
        fileContent = Utils.readContentsAsString(file);
    }

    private String generateId() {
        //the Utils.sha1(fileName, filePath, fileContent, fileByte) is wrong
        //I don't know why
        return Utils.sha1(fileName, filePath, fileByte);
    }

    //store blob like hashMap
    public void saveBlob() {
        File dir = Utils.join(Repository.GITLET_BLOBS, id.substring(0, 2));
        if (!dir.exists()) dir.mkdir();
        File pos = Utils.join(dir, id.substring(2));
        Utils.writeObject(pos, this);
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return id;
    }

    public byte[] getFileByte() {
        return fileByte;
    }

    public String getFileContent() {
        return fileContent;
    }

}
