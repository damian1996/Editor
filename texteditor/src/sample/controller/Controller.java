package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.model.*;
import sample.view.*;

import java.io.*;
import java.util.*;

public class Controller {
    private Stage stage;
    private Model model;
    private ViewNotepad view;
    @FXML
    TabPane tabPane;
    @FXML
    TreeView<String> dir;

    public Controller() {

    }

    public ViewNotepad getView(){
        return view;
    }

    public void setStage(Stage s){
        stage = s;
    }

    public void setModel(Model model){
        this.model = model;
    }

    public void setView(ViewNotepad view){
        this.view = view;
        view.setTabPane(tabPane);
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if(oldSelection==null)
                    return;
                else{
                    ((Card) oldSelection).setNowI(false);
                    ((Card) newSelection).setNowI(true);
                    long idOld = ((Card) oldSelection).getCardId();
                    long idNew = ((Card) newSelection).getCardId();
                    model.getCard(idOld).setNowI(false);
                    model.getCard(idNew).setNowI(true);
                    model.setCurrentTab();
                }
                view.setSyntaxColoring(model.currCard.getId());
            }
        });
        model.setCurrentTab();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        view.setDir(dir);
    }

    public void clickSelectAll() {
        modelSelectAll();
    }

    public void clickCopy() {
        modelCopy();
    }

    public void clickPaste() {
        modelPaste();
    }

    public void clickCut() {
        modelCut();
    }

    public void clickDelete(){}

    public void clickUndo() {
        model.getEditMenu().undo(view.getCards().get(model.currCard.getId()).getTextInArea());
    }

    public void clickRedo() {
        model.getEditMenu().redo(view.getCards().get(model.currCard.getId()).getTextInArea());
    }

    public void clickOpenFolder(){
        model.getFileMenu().openFolder(stage);
    }

    public void clickExitWindow() {
        modelExitWindow();
    }

    public void clickOpenFile() {
        model.getFileMenu().openFile(stage);
    }

    public void clickSaveFile() {
        long id = model.currCard.getId();
        model.getFileMenu().saveFile(view.getCards().get(id).getTextInArea(), stage);
    }

    public void clickSaveFileAs() {
        long id = model.currCard.getId();
        model.getFileMenu().saveFileAs(view.getCards().get(id).getTextInArea(), stage);
    }

    public void createNewCard(){
        model.createNewCard();
    }

    public void clickZoomIn() {
        modelIncreaseFont();
    }

    public void clickZoomOut() {
        modelDecreaseFont();
    }

    public void clickChangeStyle(){
        modelChangeStyle();
    }

    public void clickKeyMap(){modelKeyMap();}

    public void clickFind(){}

    public void clickReplace(){}

    public void modelSaveFileAs(File file, long id){
        String x = view.getCards().get(id).getFileName();
        String y = null;
        if(x.startsWith("*"))
            y = x.substring(1);
        view.getCards().get(id).setFileName(y);
        view.getCards().get(id).setFile(file);
        view.setSyntaxColoring(model.currCard.getId());
    }

    public void modelSaveFile(File file, long id){
        String x = view.getCards().get(id).getFileName();
        String y = null;
        if(x.startsWith("*"))
            y = x.substring(1);
        else y = x;
        view.getCards().get(id).setExtendFileName(y);
        view.getCards().get(id).setFile(file);
        view.setSyntaxColoring(model.currCard.getId());
    }

    public void modelOpenFolder(TreeItem<String> treeItem){
        treeItem.setExpanded(true);
        dir.setRoot(treeItem);
        dir.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dir.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<String> x = newValue;
            TreeItem<File> y =  model.getFileMenu().getDirectories().get(x);
            if(!y.getValue().isDirectory()){
                createNewCard();
                model.getFileMenu().openSelectFile(newValue);
                //view.getCards().get(model.currCard.getId()).setFileName());
            }
        });
        view.openFolder(treeItem);
    }

    public void modelSelectAll() {
        model.setCurrentTab();
        view.selectAll(model.currCard.getId());
    }

    public void modelCopy() {
        model.setCurrentTab();
        view.viewCopy(model.currCard.getId());
    }

    public void modelPaste() {
        model.setCurrentTab();
        model.currCard.addBoxAction(view.getCards().get(model.currCard.getId()).getTextInArea());
        view.viewPaste(model.currCard.getId());
        model.currCard.setSaved(false);
        modelSetFileName();
    }

    public void modelCut() {
        model.setCurrentTab();
        model.currCard.addBoxAction(view.getCards().get(model.currCard.getId()).getTextInArea());
        view.viewCut(model.currCard.getId());
        model.currCard.setSaved(false);
        modelSetFileName();
    }

    public void modelClearText(long id){
        view.clearCard(id);
    }

    public void modelInsertText(String text, long id){
        view.fillCard(text, id);
    }

    public void modelOpenFile(File file, String str, long id) {
        view.getCards().get(id).setTextInArea(str);
        view.getCards().get(id).setFileName(file.getName());
        view.getCards().get(id).setFile(file);
        view.getCards().get(id).setNowI(true);
        view.setSyntaxColoring(model.currCard.getId());
    }

    public void modelCreateCard(long id) {
        view.createNewCard(stage, id);
        view.setSyntaxColoring(model.currCard.getId());
        view.getCards().get(id).setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                System.out.println("Closed!");
            }
        });
        view.getCards().get(id).getArea().addEventFilter(KeyEvent.KEY_TYPED, (keyEvent) -> {
            if(keyEvent.getCharacter().equals("{")){
                keyEvent.consume();
                int x = view.getCards().get(id).getArea().getCurrentParagraph();
                String str = (view.getCards().get(id).getArea().getParagraph(x).getText());
                int pos = view.getCards().get(id).getArea().getCaretPosition();
                int cnt = 0;
                for(int i=0; i<str.length(); i++){
                    if(str.charAt(i)==' ') cnt++;
                    else break;
                }
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                for(int i=0; i<cnt+4; i++){
                    sb.append(" ");
                    if(i<cnt) sb2.append(" ");
                }
                String spacje = sb.toString();
                String wciecie = sb2.toString();
                view.getCards().get(id).getArea().insertText(pos, "\n"+ wciecie + "}");
                view.getCards().get(id).getArea().insertText(pos, "{\n" + spacje);
            } else if(keyEvent.getCharacter().equals("\r")){ //\n nie działa, wtf
                keyEvent.consume();
                int x = view.getCards().get(id).getArea().getCurrentParagraph();
                String str = (view.getCards().get(id).getArea().getParagraph(x-1).getText());
                int pos = view.getCards().get(id).getArea().getCaretPosition();
                int cnt = 0;
                for(int i=0; i<str.length(); i++){
                    if(str.charAt(i)==' ') cnt++;
                    else break;
                }
                StringBuilder sb2 = new StringBuilder();
                if(str.endsWith("{")){
                    for(int i=0; i<cnt+4; i++)
                        sb2.append(" ");
                } else {
                    for(int i=0; i<cnt; i++)
                        sb2.append(" ");
                }
                String wciecie = sb2.toString();
                view.getCards().get(id).getArea().insertText(pos, wciecie);
            } else {
                model.currCard.addCharacter(keyEvent, view.getCards().get(id).getTextInArea());
                if(!model.currCard.isSaved())
                    modelSetFileName();
            }
        });
    }

    public void modelSetFileName(){
        if(model.currCard.isSaved()){
            if(view.getCards().get(model.currCard.getId()).getFileName().startsWith("*")){
                String x = view.getCards().get(model.currCard.getId()).getFileName();
                x.substring(1);
                model.currCard.setFileName(x);
                view.getCards().get(model.currCard.getId()).setFileName(x);
            } else {
                view.getCards().get(model.currCard.getId()).setExtendFileName(model.currCard.getFile().getName());
            }
        }
        else{
            String nju = "*" + model.currCard.getFileName();
            view.getCards().get(model.currCard.getId()).setExtendFileName(nju);
        }
    }

    public void modelExitWindow() {
        view.ExitAndSave(stage, model.currCard.getId());
        stage.close();
    }

    public void modelChangeStyle() {
        view.changeStyle(model.currCard.getId(), stage);
    }

    public void modelIncreaseFont() {
        view.changeFont(model.currCard.getId(), true);
    }

    public void modelDecreaseFont() {
        view.changeFont(model.currCard.getId(), false);
    }

    public void modelKeyMap(){
        view.showKeyMap(model.currCard.getId(), stage);
    }
}