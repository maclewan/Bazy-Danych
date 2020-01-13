package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;

public class LogowanieController {

    @FXML
    private void initialize(){
        lblTerminal.setText("");

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
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/klinika", "log@localhost", "pas");
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);

            res.next();
            if(res.getString("count(*)").equals("0")){
                lblTerminal.setText("Błędne dane logowania");
            }
            else
                lblTerminal.setText("Logowanie");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
