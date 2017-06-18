package sample.model;

import sample.model.WholeTextAction;

public class BoxAction {
    public WholeTextAction a;
    public BoxAction(){
    }
    public void addAction(WholeTextAction a) {
        this.a = a;
    }
}
