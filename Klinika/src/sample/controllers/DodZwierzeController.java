package sample.controllers;


import com.mysql.cj.jdbc.SuspendableXAConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;


public class DodZwierzeController {

    /**
     * Konstruktor do dodawania zawierzecia
     * @param panelController
     * @param tStage
     * @param conn
     */
    public DodZwierzeController(PanelController panelController, Stage tStage, Connection conn){
        this.panelController=panelController;
        this.tStage=tStage;
        this.conn=conn;
    }

    /**
     * Konstruktor do edytowania zwierzęcia
     * @param zwierzeController
     * @param tStage
     * @param conn
     * @param idZwierza
     */
    public DodZwierzeController(ZwierzeController zwierzeController,Stage tStage, Connection conn, String idZwierza){
        isActualisation=true;
        this.zwierzeController=zwierzeController;
        this.tStage=tStage;
        this.conn=conn;
        this.idZwierza=idZwierza;
    }

    private int rok;
    private PanelController panelController;
    private ZwierzeController zwierzeController;
    private Connection conn;
    private ObservableList<String> rasyList;
    private Stage tStage;
    private Statement stmt;
    private String query;
    private ResultSet res;
    private boolean isActualisation=false;
    private String idZwierza;


    @FXML
    private void initialize(){
        try {
            stmt = conn.createStatement();
            query="SELECT gatunek, rasa FROM rasy ORDER BY r_id";
            res = stmt.executeQuery(query);
            rasyList= FXCollections.observableArrayList();

            String list=new String();
            while (res.next()) {
                String temp = res.getString("gatunek")+" - "+ res.getString("rasa");
                rasyList.add(temp);
            }



            pickerGatunek.setItems(rasyList);
            pickerGatunek.setValue("Inna - Inna");


            fldRok.textProperty().addListener((observable, oldValue, newValue) -> {

                btnZatwierdz.setDisable(false);

                if (newValue.length() > 4)
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        fldRok.setText(oldValue);
                    }
                if (newValue.length() != 0)
                    try {
                        rok = Integer.parseInt(newValue);
                    }catch (NumberFormatException e){
                        Platform.runLater(() -> {
                            fldRok.setText(oldValue);

                        });
                    }

                if (rok > LocalDate.now().getYear())
                    btnZatwierdz.setDisable(true);

            });
            if(isActualisation){
                setActData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private TextField fldNazwa;

    @FXML
    private TextField fldUmaszczenie;

    @FXML
    private TextField fldRok;

    @FXML
    private TextField fldId;

    @FXML
    private ChoiceBox<String> pickerGatunek;


    @FXML
    private Button btnZatwierdz;

    @FXML
    void btnZatwierdzOnAction(ActionEvent event) {

        String imie=fldNazwa.getText();
        String umaszczenie=fldUmaszczenie.getText();
        String rok=fldRok.getText();
        String idRasy="";
        String idWlasciciela=fldId.getText();


        try {

            stmt = conn.createStatement();
            String query = "SELECT r_id FROM rasy WHERE CONCAT(gatunek,' - ',rasa)= '"+pickerGatunek.getValue()+"';";
            ResultSet res = stmt.executeQuery(query);
            if(res.next())
                idRasy=res.getString("r_id");

            //czy taki wlasciciel istnieje
            stmt = conn.createStatement();
            query = "SELECT count(*) AS c FROM wlasciciele WHERE w_id='"+idWlasciciela+"';";
            res = stmt.executeQuery(query);
            res.next();
            if(res.getString("c").equals("0")){
                fldId.setText("");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błędne dane");
                alert.setHeaderText("Złe id Właściciela");
                alert.setContentText("Popraw dane");
                alert.showAndWait();

                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(imie.equals("")){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błędne dane");
            alert.setHeaderText("Brak danych");
            alert.setContentText("Popraw dane");
            alert.showAndWait();

            return;
        }

        try {
            stmt = conn.createStatement();
            if(!isActualisation) {
                query = "INSERT INTO pacjenci(nazwa,id_wlasciciela,id_rasy,rok_urodzenia,umaszczenie) " +
                        "VALUES('" + imie + "','" + idWlasciciela + "','" + idRasy + "','" + rok + "','" + umaszczenie + "');";
                stmt.executeUpdate(query);
                panelController.refresh();
            }
            else{
                query="UPDATE pacjenci " +
                        "SET nazwa= '"+imie+"',id_wlasciciela = '"+idWlasciciela+"',id_rasy='"+idRasy+"',rok_urodzenia='"+rok+"',umaszczenie='"+umaszczenie+"' WHERE p_id='"+idZwierza+"';";
                stmt.executeUpdate(query);
                zwierzeController.refresh();
            }


            tStage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void setActData(){
        try {
            stmt = conn.createStatement();
            query = "SELECT nazwa, umaszczenie, rok_urodzenia, id_wlasciciela,rasa,gatunek FROM pacjenci JOIN rasy on id_rasy=r_id WHERE p_id='" + idZwierza + "';";
            res = stmt.executeQuery(query);
            res.next();

            String nazwa = res.getString(1);
            String umaszczenie = res.getString(2);
            String Rok = res.getString(3);
            String IDwlasciciela = res.getString(4);
            String Rasa = res.getString(5);
            String Gatunek = res.getString(6);


            fldNazwa.setText(nazwa);
            fldUmaszczenie.setText(umaszczenie);
            fldRok.setText(Rok);
            fldId.setText(IDwlasciciela);
            String temp = Gatunek + " - " + Rasa;
            pickerGatunek.setValue(temp);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}

