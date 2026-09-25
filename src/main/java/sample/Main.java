package sample;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;
    private final double MINIMUM_WINDOW_WIDTH = 503.0;
    private final double MINIMUM_WINDOW_HEIGHT = 543.0;
    private static ScreenController screenController = null;
    private static KeywordGrabber kg = null;
    private static Controller controller = null;
    private static Snovio snovio = null;
    private static SnovioDisplay snovioDisplay = null;
    
    public static void main(String[] args) {
        Application.launch(Main.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.setTitle("Gimme Dem Keywords");
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);

            loaddAllScenes();
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loaddAllScenes() throws Exception {

        FXMLLoader loader = new FXMLLoader();
        Pane kgRoot = loader.load(getClass().getClassLoader().getResourceAsStream("KeywordGrabber.fxml"));
        kg = loader.getController();
        kg.setApp(this);

        loader = new FXMLLoader();
        Pane mainRoot = loader.load(getClass().getClassLoader().getResourceAsStream("sample.fxml"));
        controller = loader.getController();
        controller.setApp(this);
        
        loader = new FXMLLoader();
        Pane snovioRoot = loader.load(getClass().getClassLoader().getResourceAsStream("Snovio.fxml"));
        snovio = loader.getController();
        snovio.setApp(this);
        
        loader = new FXMLLoader();
        Pane snovioDisplayRoot = loader.load(getClass().getClassLoader().getResourceAsStream("SnovioDisplay.fxml"));
        snovioDisplay = loader.getController();
        snovioDisplay.setApp(this);
        

        Scene scene = new Scene(kgRoot, 800, 600);
        screenController = new ScreenController(scene);

        stage.setTitle("Get Keywords");
        stage.setScene(scene);
        stage.sizeToScene();

        screenController = new ScreenController(scene);
        screenController.addScreen("KeywordGrabber", kgRoot);
        screenController.addScreen("sample", mainRoot);
        screenController.addScreen("Snovio", snovioRoot);
        screenController.addScreen("SnovioDisplay", snovioDisplayRoot);
    }

    public void gotoKeywordGrabber() {
        stage.setTitle("Get Keywords");
        screenController.activate("KeywordGrabber");
    }

    public void gotoMain() {
        try {
            stage.setTitle("Get Domains From Search Results");
            screenController.activate("sample");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gotoMainParam(String keyword) { //(ObservableList keyword) {
        try {
            stage.setTitle("Get Domains From Search Results");
            controller.prepare(keyword);
            controller.setApp(this);
            screenController.activate("sample");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gotoSnovio() {
        try {
            stage.setTitle("Get Data From Domain");
            snovio.setApp(this);
            screenController.activate("Snovio");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void gotoSnovio(ObservableList selectedDomains) {
        try {
            stage.setTitle("Get Data From Domain");
            snovio.prepare(selectedDomains);
            snovio.setApp(this);
            screenController.activate("Snovio");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gotoSnovioDisplay(String countOfEmailsForSelectedDomains,ObservableList emailAddresses) {
        try {
            stage.setTitle("This is Data Returned From Snovio");
            snovioDisplay.setApp(this);
            snovioDisplay.prepare(countOfEmailsForSelectedDomains, emailAddresses);
            screenController.activate("SnovioDisplay");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable replaceSceneContent(String fxml, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getClassLoader().getResourceAsStream(fxml));

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();

    }

}
