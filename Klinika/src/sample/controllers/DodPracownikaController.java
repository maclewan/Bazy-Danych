package sample.controllers;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class DodPracownikaController {

    private long pesel;
    private boolean isOkPesel;
    private ObservableList<String> zawodList = FXCollections.observableArrayList("lekarz", "sekretariat","admin");
    private boolean isActualisation=false;
    private String idPracownika;

    private Connection conn;
    private PanelController pc;
    private Stage stage;

    public DodPracownikaController(Connection conn, PanelController pc, Stage stage) {
        this.conn = conn;
        this.pc = pc;
        this.stage = stage;
        isActualisation=false;
    }

    public DodPracownikaController(String idPracownika, Connection conn, PanelController pc, Stage stage) {
        this.idPracownika = idPracownika;
        this.conn = conn;
        this.pc = pc;
        this.stage = stage;
        isActualisation=true;
    }

    @FXML
    private void initialize(){
        addListener();
        pickerZawod.setItems(zawodList);
        if(isActualisation){
            setData();
        }

    }

    @FXML
    private TextField txtFldImie;

    @FXML
    private TextField txtFldNazwisko;

    @FXML
    private TextField fldPesel;

    @FXML
    private Label lblTerminal;

    @FXML
    private Button btnZatwierdz;

    @FXML
    private ChoiceBox<String> pickerZawod;

    @FXML
    void btnZatwierdzOnAction(ActionEvent event) {
        try {
            String imie,nazwisko,pesel,typ;

            imie=txtFldImie.getText();
            nazwisko=txtFldNazwisko.getText();
            pesel=fldPesel.getText();
            typ=pickerZawod.getValue();




            if(!isActualisation) {       //dodawanie nowego pracownika
                String query = "INSERT INTO pracownicy (imie,nazwisko,pesel,typ) " +
                        "VALUES ('" + imie + "','" + nazwisko + "','" + pesel + "','" + typ + "');";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(query);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Dodawanie wlasciciela");
                alert.setHeaderText("Poprawnie dodano wlasciciela");
                alert.setContentText("Właściciel został dodany");

                alert.showAndWait();
                refresh();
                stage.close();
            }
            else{
                String query = "UPDATE pracownicy  " +
                        "SET imie='"+imie+"',nazwisko='"+nazwisko+"'," +
                        "pesel='"+pesel+"',typ='"+typ+"' " +
                        "WHERE staff_id='"+idPracownika+"'";

                Statement stmt = conn.createStatement();
                stmt.executeUpdate(query);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Aktualizowanie wlasciciela");
                alert.setHeaderText("Poprawnie zaktualizowano dane wlasciciela");
                alert.setContentText("Właściciel został zaktualizowany");

                alert.showAndWait();
                refresh();
                stage.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private boolean checkPesel(String pesel){
        if(pesel.length()!=11)
            return true;
        /**
         * sprawdzanie liczby kontrolnej
         */
        int temp;
        temp=pesel.charAt(0)+pesel.charAt(1)*3+pesel.charAt(2)*7+pesel.charAt(3)*9+pesel.charAt(4)+pesel.charAt(5)*3+pesel.charAt(6)*7+pesel.charAt(7)*9+pesel.charAt(8)+pesel.charAt(9)*3+pesel.charAt(10);
        if(temp%10!=0)
            return false;

        /**
         * sprawdzanie sensu peselu
         * rzuca wyjątek gdy stworzenie LocalData sie nie udało
         */
        int day,month,year,century;
        day=Integer.parseInt(pesel.charAt(4)+""+pesel.charAt(5));
        month=Integer.parseInt(pesel.charAt(2)+""+pesel.charAt(3));
        year=Integer.parseInt(pesel.charAt(0)+""+pesel.charAt(1));
        century=month/20;
        month=month%20;

        switch(century) {
            case 0:
                century=19;
                break;
            case 1:
                century=20;
                break;
            case 2:
                century=21;
                break;
            case 3:
                century=22;
                break;
            case 4:
                century=18;
                break;
            default:
                break;
        }
        year=century*100+year;
        try {
            LocalDate.of(year, month, day);
        }
        catch(Exception e){
            return false;
        }

        isOkPesel=true;
        return true;
    }

    private void setData(){
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT imie,nazwisko,pesel,typ FROM pracownicy WHERE staff_id='" + idPracownika + "';";
            ResultSet res = stmt.executeQuery(query);
            res.next();

            txtFldImie.setText(res.getString(1));
            txtFldNazwisko.setText(res.getString(2));
            fldPesel.setText(res.getString(3));
            pickerZawod.setValue(res.getString(4));


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void refresh(){
        pc.refresh();
    }

    private void addListener(){

        fldPesel.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                isOkPesel=false;

                if(newValue.length()>11)
                    throw new ArithmeticException();
                if(newValue.length()!=0)
                    pesel=Long.parseLong(newValue);
                if(checkPesel(newValue))
                    lblTerminal.setText("");
                else
                    lblTerminal.setText("Niepoprawny pesel");

            } catch (ArithmeticException e) {
                fldPesel.setText(oldValue);
                lblTerminal.setText("");
            }
            catch (NumberFormatException e){
                fldPesel.setText(oldValue);
                lblTerminal.setText("Niepoprawne znaki");
            }

            if(isOkPesel)
                btnZatwierdz.setDisable(false);
            else
                btnZatwierdz.setDisable(true);
        });
    }

}
