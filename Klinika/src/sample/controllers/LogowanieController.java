package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LogowanieController {

    private Stage thisStage;

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
        System.out.println(login+";"+haslo+";"+query);
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/klinika", "log@localhost", "pas");

            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);


            res.next();
            if(res.getString("count(*)").equals("0")){
                System.out.println(res.getString("count(*)"));
                lblTerminal.setText("Błędne dane logowania");
            }
            else {
                zaloguj(login.charAt(login.length() - 1));
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


    @FXML
    private void initialize(){
        lblTerminal.setText("");

    }

    private void zaloguj(Character userType){

        try {

            Stage stageF = new Stage();
            PanelController panelController = new PanelController(userType);


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
