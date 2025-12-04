public class NotesClass {
    private int noteID;
    private int userID;
    private String text;

     public NotesClass(int noteID, int userID, String text) {
        this.noteID = noteID;
        this.userID = userID;
        this.text = text;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return noteID + ": " + text;
    }
}
