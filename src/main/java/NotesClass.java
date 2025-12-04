public class NotesClass {
    private int UserID;
    private String text;

    public NotesClass(int UserID, String text) {
        this.UserID = UserID;
        this.text = text;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "NotesClass{" +
                "UserID=" + UserID +
                ", text='" + text + '\'' +
                '}';
    }
}
