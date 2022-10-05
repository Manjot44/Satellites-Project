package unsw.blackout;

public class File {
    private String filename;
    private String content;
    private boolean hasTransferCompleted;

    public File(String filename, String content, boolean hasTransferCompleted) {
        this.filename = filename;
        this.content = content;
        this.hasTransferCompleted = hasTransferCompleted;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public boolean isHasTransferCompleted() {
        return hasTransferCompleted;
    }
}
