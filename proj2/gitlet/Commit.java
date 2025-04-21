package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

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
    private final Commit parent;
    private final Blob[] blobs;
    private String timeStamp;
    private final String id;
    /* TODO: fill in the rest of this class. */

    Commit(String message, Commit parent, Blob[] blobs) {
        this.message = message;
        this.parent = parent;
        Date currentTime;
        if (parent == null) currentTime = new Date(0);
        else currentTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        timeStamp = dateFormat.format(currentTime);
        this.blobs = blobs;
        id = generateId();
    }

    private String generateId() {
        if (parent == null && blobs == null) {
            return Utils.sha1(message, timeStamp);
        } else if (parent == null) {
            return Utils.sha1(message, timeStamp, Arrays.toString(blobs));
        } else if (blobs == null) {
            return Utils.sha1(message, parent.toString(), timeStamp);
        } else {
            return Utils.sha1(message, parent.toString(), timeStamp, Arrays.toString(blobs));
        }
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

}
