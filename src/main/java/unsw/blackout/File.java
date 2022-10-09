package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

public class File {
    private String filename;
    private String content;
    private int fullSize;
    private boolean hasTransferCompleted;
    private String fromId;
    private List<String> toIds = new ArrayList<String>();

    public File(String filename, String content, int fullSize, boolean hasTransferCompleted, String fromId) {
        this.filename = filename;
        this.content = content;
        this.fullSize = fullSize;
        this.hasTransferCompleted = hasTransferCompleted;
        this.fromId = fromId;
    }

    public void addToIds(String id) {
        toIds.add(id);
    }

    public void removeToIds(String id) {
        toIds.remove(id);
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public int getCurrentSize() {
        return content.length();
    }

    public int getFullSize() {
        return fullSize;
    }

    public boolean isHasTransferCompleted() {
        return hasTransferCompleted;
    }

    public String getFromId() {
        return fromId;
    }

    public List<String> getToIds() {
        return toIds;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFullSize(int fullSize) {
        this.fullSize = fullSize;
    }

    public void setHasTransferCompleted(boolean hasTransferCompleted) {
        this.hasTransferCompleted = hasTransferCompleted;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
}
