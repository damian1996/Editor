package sample;
import javafx.application.Application;
import javafx.event.EventHandler ;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.*;
import sample.controller.Controller;
import sample.model.*;
import sample.view.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Model model = new Model();
        ViewNotepad view = new ViewNotepad();
        final Controller controller = loader.getController();
        controller.setModel(model);
        controller.setView(view);
        model.setController(controller);
        view.setController(controller);
        stage.setTitle("Editor");
        Scene scene = new Scene(root, 950, 800);
        scene.getStylesheets().add(getClass().getResource("java-keywords.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("modena_dark.css").toExternalForm());
        stage.setScene(scene);
        controller.setStage(stage);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                controller.modelExitWindow();
            }
        });
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

/*
dodac * przy niezapisanych plikach // poprawic
// kolor czcionki
// ustawic nazwe plikowi wybranemu z katalogow
Find // https://gist.github.com/zinch84/9186176
undo
nazwy plikow...
*/

