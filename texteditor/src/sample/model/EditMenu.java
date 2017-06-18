package sample.model;

import sample.controller.Controller;

public class EditMenu {
    Controller controller;
    Model model;
    public EditMenu(Controller controller, Model model){
        this.controller = controller;
        this.model = model;
    }
    public void undo(String text){
        model.setCurrentTab();
        BoxAction currState = new BoxAction();
        currState.addAction(new WholeTextAction(text));
        long tmpId = model.currCard.getId();
        if(model.getCard(tmpId).getFirst().size()==0){
            controller.modelClearText(tmpId);
            model.getCard(tmpId).getSecond().add(currState);
        } else {
            BoxAction undo = model.getCard(tmpId).getFirst().get(model.getCard(tmpId).getFirst().size()-1);
            if(undo.a.text.equals(text)){
                model.getCard(tmpId).getFirst().remove(model.getCard(tmpId).getFirst().size()-1);
                if(model.getCard(tmpId).getFirst().isEmpty()){
                    controller.modelClearText(tmpId);
                    return;
                }
                undo = model.getCard(tmpId).getFirst().get(model.getCard(tmpId).getFirst().size()-1);
            }
            model.getCard(tmpId).getSecond().add(currState);
            if(model.getCard(tmpId).getSecond().size()==30){
                model.getCard(tmpId).getSecond().remove(0);
            }
            controller.modelInsertText(undo.a.text, tmpId);
            model.currCard.setSaved(false);
            controller.modelSetFileName();
        }
    }
    public void redo(String text){
        model.setCurrentTab();
        long tmpId = model.currCard.getId();
        if(model.getCard(tmpId).getSecond().isEmpty()) return;
        BoxAction redo = model.getCard(tmpId).getSecond().get(model.getCard(tmpId).getSecond().size()-1);
        if(redo.a.text.equals(text)){
            model.getCard(tmpId).getSecond().remove(model.getCard(tmpId).getSecond().size()-1);
            if(model.getCard(tmpId).getSecond().isEmpty())
                return;
            redo = model.getCard(tmpId).getSecond().get(model.getCard(tmpId).getSecond().size()-1);
        }
        BoxAction currState = new BoxAction();
        currState.addAction(new WholeTextAction(text));
        model.getCard(tmpId).getFirst().add(currState);
        if(model.getCard(tmpId).getFirst().size()==30){
            model.getCard(tmpId).getFirst().remove(0);
        }
        controller.modelInsertText(redo.a.text, tmpId);
        model.currCard.setSaved(false);
        controller.modelSetFileName();
    }
}
