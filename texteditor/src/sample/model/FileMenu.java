package sample.model;

import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import sample.controller.Controller;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileMenu {
    Controller controller;
    Model model;
    private Map<TreeItem<String>, TreeItem<File>> directories;
    public FileMenu(Controller controller, Model model){
        this.controller = controller;
        this.model = model;
        directories = new HashMap<>();
    }

    public Map<TreeItem<String>, TreeItem<File>> getDirectories(){
        return directories;
    }

    public void openFile(Stage stage){
        String toStr = null;
        File file = null;
        try{
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File("."));
            fc.setTitle("Open Resource File");
            file = fc.showOpenDialog(stage);
        } catch(Exception e){
            return;
        }

        try{
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String text;
            while((text = br.readLine()) != null){
                sb.append(text);
                sb.append('\n');
            }
            toStr = sb.toString();
            model.currCard.setSaved(true);
            model.currCard.setFileName(file.getName());
            model.currCard.setFile(file);
            controller.modelOpenFile(file, toStr, model.currCard.getId());
        } catch(Exception e){

        }
    }

    public void openSelectFile(TreeItem<String> fileName){
        // dodatkowo potrzebuje nową karte na ten otwierany plik
        TreeItem<File> trf = directories.get(fileName);
        File f = trf.getValue();
        String toStr = null;
        try{
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String text;
            while((text = br.readLine()) != null){
                sb.append(text);
                sb.append('\n');
            }
            toStr = sb.toString();
        } catch(Exception e){

        }
        controller.modelOpenFile(f, toStr, model.currCard.getId());
    }

    public void openFolder(Stage stage){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File choice = dc.showDialog(stage);
        if(choice == null || ! choice.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not open directory");
            alert.setContentText("The file is invalid.");
            alert.showAndWait();
        } else {
            TreeItem<String> a = getNodesForDirectory(choice);
            controller.modelOpenFolder(a);
        }
    }

    public TreeItem<String> getNodesForDirectory(File directory) {
        TreeItem<File> root = new TreeItem<File>(directory);
        TreeItem<String> tmp = new TreeItem<String>(directory.getName());
        directories.put(tmp, root);
        for(File f : directory.listFiles()) {
            if(f.isDirectory()) {
                tmp.getChildren().add(getNodesForDirectory(f));
            } else {
                TreeItem<File> trf = new TreeItem<>(f);
                TreeItem<String> trs = new TreeItem<>(f.getName());
                root.getChildren().add(trf);
                tmp.getChildren().add(trs);
                directories.put(trs, trf);
            }
        }
        return tmp;
    }

    public void saveFile(String text, Stage stage) {
        try {
            model.setCurrentTab();
            if(model.getCard(model.currCard.getId()).getFile()!=null){
                replaceFile(model.currCard, text);
                model.currCard.setSaved(true);
                model.currCard.setFile(model.getCard(model.currCard.getId()).getFile());
                //model.currCard.getFileName().startsWith("*")
                model.currCard.setFileName(model.getCard(model.currCard.getId()).getFile().getName());
                controller.modelSaveFile(model.getCard(model.currCard.getId()).getFile(), model.currCard.getId());
            } else {
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File("."));
                fc.setTitle("Open Resource File");
                File file = fc.showOpenDialog(stage);
                writeToCurrentFile(model.currCard.getFile(), text);
                model.currCard.setFile(file);
                model.currCard.setFileName(file.getName());
                model.currCard.setSaved(true);
                controller.modelSaveFile(file, model.currCard.getId());
            }
        } catch(Exception e){

        }
    }

    public void saveFileAs(String text, Stage stage){
        try{
            model.setCurrentTab();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.setTitle("Save File");
            File file = fileChooser.showSaveDialog(stage);
            model.setCurrentTab();
            if(file != null){
                writeToFile(file, text);
            }
            model.currCard.setFile(file);
            model.currCard.setSaved(true);
            controller.modelSaveFileAs(file, model.currCard.getId());
        } catch(Exception e){

        }
    }
    public void createCard(CardModel card){
        card.setFileName("Unsaved Card");
        card.setOldName("");
        card.setNowI(true);
        model.setCurrentTab();
    }

    public void replaceFile(CardModel card, String text){
        try {
            FileOutputStream fooStream = new FileOutputStream(card.getFile(), false);
            byte[] bytes = text.getBytes();
            fooStream.write(bytes);
            fooStream.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Problem with overwrite file");
            alert.showAndWait();
        }
    }
    public void writeToCurrentFile(File file, String t){
        try{
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Problems with file!");
            alert.showAndWait();
        }
        writeToFile(file, t);
    }
    public void writeToFile(File file, String t){
        try{
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file);
            for (String line : t.split("\\n")) {
                fileWriter.write(line);
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Saving failed!");
            alert.showAndWait();
        }
    }
}
