package sample.model;

public class WholeTextAction implements Action{
    public String text;
    public String fileName;
    public WholeTextAction(String t){
        text = t;
    }
    public void setFileName(String fn){
        fileName = fn;
    }
    @Override
    public void undo() {

    }
    @Override
    public void redo() {

    }
}
