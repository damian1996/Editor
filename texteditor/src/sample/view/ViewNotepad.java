package sample.view;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sample.controller.Controller;

import java.util.*;

public class ViewNotepad {
    TabPane tabPane;
    TreeView dir;
    private Map<Long, Card> cards;
    Controller controller;

    public ViewNotepad(){
        cards = new HashMap<>();
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    public void createNewCard(Stage stage, long id){
        Card card = new Card(id);
        setListeners(card);
        cards.put(id, card);
        tabPane.getTabs().add(card);
        tabPane.getSelectionModel().select(card);
    }
    public Map<Long, Card> getCards() {
        return cards;
    }

    public void setSyntaxColoring(long id){
        if(cards.get(id).getFileName().endsWith(".java")){
            cards.get(id).subscribeAddJava();
        } else if(cards.get(id).getFileName().endsWith(".cpp")){
            cards.get(id).subscribeAddCpp();
        } else {
            cards.get(id).subscribeRemove();
        }
    }

    public void setTabPane(TabPane tabPane){
        this.tabPane = tabPane;
    }

    public void setDir(TreeView dir){
        this.dir = dir;
    }

    public void clearCard(long id){
        getCards().get(id).getArea().clear();
    }

    public void fillCard(String text, long id){
        clearCard(id);
        getCards().get(id).getArea().insertText(0, text);
    }

    public void changeFont(long id, boolean type){
        if(type){
            cards.get(id).increaseFont();
        } else {
            cards.get(id).decreaseFont();
        }
    }

    public void changeStyle(long id, Stage stage){
            cards.get(id).changeColors(stage);
            for(Map.Entry<Long, Card> x : cards.entrySet()){
                if(x.getKey()!=id)
                    x.getValue().changeColorsOtherCards(cards.get(id).getColorBackRGB(), cards.get(id).getColorFontRGB());
            }
    }

    public void showKeyMap(long id, Stage stage){
        cards.get(id).cardKeyMap(stage);
    }

    public void openFolder(TreeItem<String> treeItem){
        dir.setRoot(treeItem);
    }

    public void viewCopy(long id){
        getCards().get(id).copyInCard();
    }

    public void viewPaste(long id){
        getCards().get(id).pasteInCard();
    }

    public void viewCut(long id){
        getCards().get(id).cutInCard();
    }

    public void selectAll(long id){
        getCards().get(id).selectAllinCard();
    }

    public void ExitAndSave(Stage stage, long id){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Window");
        alert.setContentText("Do you want save current card?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            controller.clickSaveFile();
        }
    }

    public void setListeners(Card card){
        card.getArea().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode()== KeyCode.Z && e.isShortcutDown() && e.isShiftDown()){
                e.consume();
                controller.clickRedo();
            } else if(e.getCode() == KeyCode.I && e.isShortcutDown()){
                e.consume();
                controller.clickZoomIn();
            } else if(e.getCode() == KeyCode.C && e.isShortcutDown()){
                e.consume();
                controller.clickCopy();
            } else if(e.getCode() == KeyCode.V && e.isShortcutDown()){
                e.consume();
                controller.clickPaste();
            } else if(e.getCode() == KeyCode.F && e.isShortcutDown()){
                e.consume();
                controller.clickFind();
            } else if(e.getCode() == KeyCode.R && e.isShortcutDown()){
                e.consume();
                controller.clickReplace();
            } else if(e.getCode() == KeyCode.A && e.isShortcutDown()){
                e.consume();
                controller.clickSelectAll();
            } else if(e.getCode() == KeyCode.O && e.isShortcutDown() && e.isShiftDown()){
                e.consume();
                controller.clickOpenFile();
            } else if(e.getCode() == KeyCode.DELETE){
                e.consume();
                controller.clickDelete();
            } else if(e.getCode() == KeyCode.F && e.isShortcutDown() && e.isShiftDown()){
                e.consume();
                controller.clickOpenFolder();
            } else if(e.getCode() == KeyCode.S && e.isShortcutDown() && e.isShiftDown()){
                e.consume();
                controller.clickSaveFileAs();
            } else if(e.getCode() == KeyCode.S && e.isShortcutDown()){
                e.consume();
                controller.clickSaveFile();
            }else if(e.getCode() == KeyCode.MINUS && e.isShortcutDown()){
                e.consume();
                controller.clickZoomOut();
            } else if(e.getCode() == KeyCode.W && e.isShortcutDown()){
                e.consume();
                controller.clickChangeStyle();
            } else if(e.getCode() == KeyCode.K && e.isShortcutDown()){
                e.consume();
                controller.clickKeyMap();
            } else if(e.getCode() == KeyCode.Z && e.isShortcutDown()){
                e.consume();
                controller.clickUndo();
            } else if(e.getCode() == KeyCode.Q && e.isShortcutDown()){
                e.consume();
                controller.clickExitWindow();
            } else if(e.getCode()==KeyCode.X && e.isShortcutDown()){
                e.consume();
                controller.clickCut();
            }
        });
    }
}
