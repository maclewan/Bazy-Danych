package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controllers.*;

import java.sql.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/logowanie.fxml"));
        LogowanieController lc = new LogowanieController();
        loader.setController(lc);

        primaryStage.setTitle("Wirtualny pupil");
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setResizable(false);
        primaryStage.show();



    }


    public static void main(String[] args) {
        launch(args);

    }
}
