package sample.model;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.BRACELEFT;
import static javafx.scene.input.KeyCode.OPEN_BRACKET;

public class CardModel{
    private final long id;
    private List<BoxAction> first;
    private List<BoxAction> second;
    private String fileName;
    private String oldName;
    private boolean nowI = false;
    private File file;
    private long lastTime = 0;
    private boolean saved = true;
    private final long tooLongTime = 2000;

    public CardModel(long id){
        this.id = id;
        first = new ArrayList<>();
        second = new ArrayList<>();
    }

    public BoxAction createBoxAction(String text){
        WholeTextAction action = new WholeTextAction(text);
        BoxAction boxAction = new BoxAction();
        boxAction.addAction(action);
        return boxAction;
    }

    public void addBoxAction(String text){
        BoxAction next = createBoxAction(text);
        first.add(next);
    }

    public void addCharacter(KeyEvent event, String text){
        long currTime = System.currentTimeMillis();
        if(event.getCode()== KeyCode.BACK_SPACE || event.getCode()==KeyCode.DELETE){
            addBoxAction(text);
        } else {
            //System.out.println(event.getCode());
            if(event.getCode()==OPEN_BRACKET){
                event.consume();
               // codeArea.insertText(0, "\n}\n"); \\ \t
               // codeArea.insertText(0, "{\n");
            }
            if(lastTime!=0 && currTime-lastTime>tooLongTime){
                addBoxAction(text);
            }
        }
        lastTime = currTime;
        if(saved){
            saved = false;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.fileName = file.getName();
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public List<BoxAction> getSecond() {
        return second;
    }

    public List<BoxAction> getFirst() {
        return first;
    }

    public boolean isNowI() {
        return nowI;
    }

    public void setNowI(boolean nowI) {
        this.nowI = nowI;
    }

    public long getId() {
        return id;
    }

    public void setSaved(boolean saved){
        this.saved = saved;
    }

    public boolean isSaved(){return saved;}
}
