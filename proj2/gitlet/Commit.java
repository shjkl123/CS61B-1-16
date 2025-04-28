package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    private final List<Commit> parent = new ArrayList<>();
    private final List<Blob> blobs = new LinkedList<>();
    private final Map<String, String> pathToBlob = new HashMap<>();
    //fileName to blobId
    private final String timeStamp;
    private final String id;
    /* TODO: fill in the rest of this class. */

    Commit(String message, Commit[] parent, Blob[] blobs) {
        this.message = message;
        if (parent != null) this.parent.addAll(List.of(parent));
        Date currentTime;
        if (parent == null) currentTime = new Date(0);
        else currentTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        timeStamp = dateFormat.format(currentTime);
        if (blobs != null) {
            this.blobs.addAll(Arrays.asList(blobs));
            for (Blob b : blobs) {
                pathToBlob.put(b.getFile().getName(), b.toString());
            }
        }
        id = generateId();
    }


    private String generateId() {
        return Utils.sha1(message, timeStamp, parent.toString(), pathToBlob.toString());
    }

    @Override
    public String toString() {
         return id;
    }


    public void saveCommit() {
        String id = generateId();
        File file = Utils.join(Repository.GITLET_COMMITS, id);
        Utils.writeObject(file, this);
    }

    public boolean isFileInCommit(File file) {
        String blobId = pathToBlob.get(file.getName());
        Blob b = new Blob(file);
        return b.toString().equals(blobId);
    }

    public void removeFile(File file) {
        Blob b = new Blob(file);
        blobs.remove(b);
    }

    public boolean isInitCommit() {
        return parent.isEmpty();
    }

    public List<Commit> getParent() {
        return parent;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getId() {
        return id;
    }
}
