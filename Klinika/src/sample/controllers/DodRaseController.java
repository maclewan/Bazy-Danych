package sample.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.sql.*;

public class DodRaseController {

    public DodRaseController(Connection connection, Stage stage, PanelController pc){
        conn=connection;
        this.stage=stage;
        this.pc=pc;
    }

    PanelController pc;
    private Connection conn;
    private Statement stmt;
    private String query;
    private ResultSet res;
    private Stage stage;

    @FXML
    private TextField fldRasa;

    @FXML
    private TextField fldGatunek;

    @FXML
    private Button btnZatwierdz;


    @FXML
    void btnZatwierdzOnAction(ActionEvent event) {

        try {
            if(fldGatunek.getText().equals("")){
                btnZatwierdz.setText("Podaj gatunek!");
                return;
            }

            query = "SELECT count(*) as c FROM rasy WHERE rasa='"+fldRasa.getText()+"' AND gatunek='"+fldGatunek.getText()+"';";
            stmt = conn.createStatement();
            res = stmt.executeQuery(query);
            res.next();
            if(!res.getString("c").equals("0")){
                btnZatwierdz.setText("Rasa istnieje!");
                return;
            }
            query = "INSERT INTO rasy (rasa,gatunek) " +
                    "VALUES ('"+fldRasa.getText()+"','"+fldGatunek.getText()+"') ;";
            stmt.executeUpdate(query);
            pc.refresh();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }








}

