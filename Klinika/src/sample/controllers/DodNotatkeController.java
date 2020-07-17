package sample.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DodNotatkeController {

    private Stage stage;
    private boolean isActualisation;
    private char userType;
    private Connection conn;
    private Statement stmt;
    private String query;
    private ResultSet res;
    private String idNotatki;
    private String idWizyty;
    private String idPacjenta;


    public DodNotatkeController(Stage stage, boolean isActualisation, char userType, Connection conn) {
        this.stage = stage;
        this.isActualisation = isActualisation;
        this.userType = userType;
        this.conn = conn;
    }

    @FXML
    private void initialize(){
        if(userType!='l'){
            txtaraTrescNotatki.setEditable(false);
            btnZatwiedz.setVisible(false);
        }
        if(isActualisation)
            updateData();
        else
            updateData2();

    }

    public void updateData(){
        try {
            query = "SELECT dzien,godzina,nazwa,komentarz " +
                    "FROM ((notatki JOIN pacjenci ON id_pacjenta=p_id) JOIN wizyty ON id_wizyty=w_id) JOIN terminy ON id_terminu = g_id " +
                    "WHERE n_id='"+idNotatki+"';";
            stmt = conn.createStatement();
            res = stmt.executeQuery(query);
            res.next();
            String temp ="Notatka do wizyty z dnia "+res.getString(1)+" "+res.getString(2)+"; Zwierze: "+ res.getString(3);
            lblTytul.setText(temp);
            txtaraTrescNotatki.setText(res.getString(4));
            txtaraTrescNotatki.positionCaret(txtaraTrescNotatki.getText().length());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateData2(){
        try {
            query = "SELECT dzien, godzina, nazwa, p_id " +
                    "FROM wizyty JOIN terminy ON id_terminu=g_id JOIN pacjenci ON id_pacjenta=p_id " +
                    "WHERE w_id = '"+idWizyty+"';";
            stmt = conn.createStatement();
            res = stmt.executeQuery(query);
            res.next();
            String temp ="Nowa notatka do wizyty z dnia "+res.getString(1)+" "+res.getString(2)+"; Zwierze: "+ res.getString(3);
            lblTytul.setText(temp);
            idPacjenta=res.getString(4);



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Label lblTytul;

    @FXML
    private TextArea txtaraTrescNotatki;

    @FXML
    private Button btnZatwiedz;

    @FXML
    void btnZatwiedzOnAction(ActionEvent event) {
        try {
            /**
             * aktualizacja notatki
             */
            if (isActualisation) {
                query = "UPDATE notatki SET komentarz= '" + txtaraTrescNotatki.getText() + "' " +
                        "WHERE n_id='" + idNotatki + "';";
                stmt = conn.createStatement();
                stmt.executeUpdate(query);
            }

            /**
             * dodawanie nowej notatki
             */
            else{
                query="INSERT INTO notatki (id_pacjenta,id_wizyty,komentarz) " +
                        "VALUES ('"+idPacjenta+"','"+idWizyty+"','"+txtaraTrescNotatki.getText()+"')";
                stmt = conn.createStatement();
                stmt.executeUpdate(query);
            }
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setIdNotatki(String idNotatki) {
        this.idNotatki = idNotatki;
    }

    public void setIdWizyty(String idWizyty) {
        this.idWizyty = idWizyty;
    }
}

