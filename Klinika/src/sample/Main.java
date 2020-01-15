package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controllers.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/logowanie.fxml"));
        LogowanieController lc = new LogowanieController(primaryStage);
        loader.setController(lc);

        primaryStage.setTitle("Wirtualny pupil");
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setResizable(false);
        primaryStage.show();



    }


    public static void main(String[] args) {
        /*
        try {

            String executeCmd = "mysqldump -u" + "root" + " -p" + "Zapisy124567" + " " + "hobby" + " -r " + "hobby12334.sql";  //działa!
            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();
            if (processComplete == 0) {
                System.out.println("Backup Complete");
            } else {
                System.out.println("Backup Failure");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/ // todo: kod do robienia backupu



/*
        try{
            String[] executeCmd = new String[]{"mysql", "hobby", "-u" + "root", "-p" + "Zapisy124567", "-e", " source " + "hobby12334.sql"};

            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();


            if (processComplete == 0) {
                System.out.println("Successfully restored from SQL : " + "hobby12334.sql");
            } else {
                System.out.println("Error at restoring");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

*/  //todo: kod do wczytywania backupu
        launch(args);

    }
}
