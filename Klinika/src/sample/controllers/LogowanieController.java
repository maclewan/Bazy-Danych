package sample.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.*;

public class LogowanieController {

    private Stage thisStage;
    Connection conn;

    @FXML
    private void initialize(){
        lblTerminal.setText("");
        thisStage.setResizable(false);
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/klinika", "log@localhost", "pas");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private TextField fldLogin;

    @FXML
    private PasswordField fldHaslo;

    @FXML
    private Button btnZaloguj;

    @FXML
    private Label lblTerminal;

    @FXML
    void btnZalogujOnAction(ActionEvent event) {
        String login = fldLogin.getText();
        String haslo = fldHaslo.getText();
        String query = "SELECT count(*) FROM uzytkownicy WHERE login='"+login+"' AND haslo=sha1('"+haslo+"');";
        try {

            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);


            res.next();
            if(res.getString("count(*)").equals("0")){
                lblTerminal.setText("Błędne dane logowania");
            }
            else {
                stmt = conn.createStatement();
                query="SELECT id_u FROM uzytkownicy WHERE login='"+login+"';";
                res = stmt.executeQuery(query);
                res.next();
                int tempId=Integer.parseInt(res.getString("id_u"));
                char tempTyp=login.charAt(login.length()-1);

                zaloguj(tempId,tempTyp);
                lblTerminal.setText("Logowanie");
                stmt.close();
                res.close();
                conn.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LogowanieController(Stage thisStage){
        this.thisStage=thisStage;
    }




    private void zaloguj(int userId, char userTyp){

        try {

            Stage stageF = new Stage();
            PanelController panelController = new PanelController(userId,userTyp,stageF);

            stageF.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    panelController.stopThreads();
                }
            });


            FXMLLoader loader =new FXMLLoader(getClass().getClassLoader().getResource("resources/panel.fxml"));
            loader.setController(panelController);

            stageF.setTitle("Wirtualny Pupil");
            stageF.setScene(new Scene(loader.load()));
            stageF.show();
            stageF.setResizable(false);
            thisStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
