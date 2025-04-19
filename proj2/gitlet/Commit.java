package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.SimpleTimeZone;
import static gitlet.Utils.*;
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
    private String message;
    /* TODO: fill in the rest of this class. */
    private Commit parent;
    private String timestamp;

    public Commit(String message, Commit parent) {
        this.message = message;
        this.parent = parent;
        if (parent == null) {
            timestamp = "00:00:00 UTC, Thursday, 1 January 1970";
        } else {
            ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss 'UTC' EEEE, d MMMM yyyy");
            timestamp = utcTime.format(formatter);
        }

    }

    public String getMessage() {
        return message;
    }

    public Commit getParent() {
        return parent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void saveCommit() {
        File file = Utils.join(Repository.GITLET_COMMIT, sha1(this));
        writeObject(file, this);
    }

    public static Commit getCommit(String sha1) {
        File file = Utils.join(Repository.GITLET_COMMIT, sha1);
        return readObject(file, Commit.class);
    }


}
