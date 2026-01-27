package Backend;

public class Lesson {

    private String lessonId;
    private String title;
    private String content;

    public Lesson(String lessonId, String title, String content){
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
    }

    public String getLessonId() {
        return lessonId;
    }
    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
