package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String sha1;
    private File file;

    public Blob(File file) {
        this.file = file;
        this.sha1 = sha1(file);
    }

    public String getSha1() {
        return this.sha1;
    }

    public File getFile() {
        return this.file;
    }

    public void saveBlob() {
        File file = join(Repository.GITLET_Blobs, sha1);
        writeObject(file, this);
    }

    public void saveBlob(File mk) {
        File f = join(mk, sha1);
        writeObject(f, this);
    }
}
