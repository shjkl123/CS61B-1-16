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
    private final Map<String, String> pathToBlobID = new HashMap<>();
    private final List<Commit> parents = new ArrayList<>();
    private final Date date;
    private String timeStamp;
    private String id;
    /* TODO: fill in the rest of this class. */

    //initial commit
    Commit() {
        message = "initial commit";
        date = new Date(0);
    }

    //expect initial commit
    Commit(String message) {
        this.message = message;
        this.date = new Date();
        parents.add(Repository.getHeadCommit());
    }

    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy z", Locale.US);
        return dateFormat.format(date);
    }

    private String generateId() {
        return Utils.sha1(message, parents.toString(), timeStamp, pathToBlobID.toString());
    }

    @Override
    public String toString() {
        return id;
    }

    //compare parent and save addStageFile
    public void saveCommit() {
        timeStamp = dateToTimeStamp(date);
        id = generateId();
        if (!parents.isEmpty()) {
            //wait merge to change it
            Map<String, String> parentMap = parents.get(0).pathToBlobID;
            for (String fileNameSha1 : parentMap.keySet()) {
                if (!Repository.isFileInAddStage(fileNameSha1) &&
                    !Repository.isFileInRemoveStage(fileNameSha1)) {
                    pathToBlobID.put(fileNameSha1, parentMap.get(fileNameSha1));
                }
            }
            Repository.saveAllAddStageFile(pathToBlobID);
            Repository.deleteAllRemoveStageFile();
        }
        File file = Utils.join(Repository.GITLET_COMMITS, id);
        Utils.writeObject(file, this);
    }

    //return true if the fileContent is changed
    public boolean isStoredFile(File file) {
        String value = pathToBlobID.get(Utils.sha1(file.getName()));
        if (value == null) return false;
        Blob b = new Blob(file);
        return value.equals(b.toString());
    }

    //return true if the file is saved
    public boolean isStoredFile(String fileName) {
        return pathToBlobID.containsKey(Utils.sha1(fileName));
    }

    public Map<String, String> getPathToBlobID() {
        return pathToBlobID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public Commit getParent(int index) {
        if (parents.isEmpty()) return null;
        return parents.get(index);
    }

    public List<Commit> getParents() {
        return parents;
    }
}
