package sample.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class DodWlascicielaController {

    private int kod1,kod2;
    private long pesel;
    private boolean isOkPesel;
    private boolean isActualisation=false;
    private String idWlasciciela;

    private Connection conn;
    private PanelController pc;
    private Stage stage;

    @FXML
    private void initialize(){
        lblTerminal.setText("");
        if(isActualisation){
            setData();
        }
        else {
            btnZatwierdz.setDisable(true);
        }
        addListeners();
    }

    public DodWlascicielaController(Connection conn,PanelController pc,Stage s){
        this.conn=conn;
        this.pc=pc;
        stage=s;
    }
    public DodWlascicielaController(String idWlasciciela,Connection conn,PanelController pc,Stage s){
        this.idWlasciciela=idWlasciciela;
        this.conn=conn;
        this.pc=pc;
        stage=s;
        isActualisation=true;
    }



    @FXML
    private TextField fldImie;

    @FXML
    private Label lblTerminal;

    @FXML
    private TextField fldNazwisko;

    @FXML
    private TextField fldPesel;

    @FXML
    private Button btnZatwierdz;

    @FXML
    private TextField fldUlica;

    @FXML
    private TextField fldNrDomu;

    @FXML
    private TextField fldNumerMieszkania;

    @FXML
    private TextField fldKod1;

    @FXML
    private TextField fldKod2;

    @FXML
    void btnZatwierdzOnAction(ActionEvent event) {
        try {
            String imie,nazwisko,ulica,nrDomu,nrMieszkania,pesel,kod1,kod2;

            imie=fldImie.getText();
            nazwisko=fldNazwisko.getText();
            pesel=fldPesel.getText();
            ulica=fldUlica.getText();
            nrDomu=fldNrDomu.getText();
            nrMieszkania=fldNumerMieszkania.getText();
            kod1=fldKod1.getText();
            kod2=fldKod2.getText();
            String kod=kod1+"-"+kod2;



            if(!isActualisation) {       //dodawanie nowego wlasciciela
                String query = "INSERT INTO wlasciciele (imie,nazwisko,pesel,ulica,nr_domu,nr_mieszkania,kod_pocztowy) " +
                        "VALUES ('" + imie + "','" + nazwisko + "','" + pesel + "','" + ulica + "','" + nrDomu + "','" + nrMieszkania + "','" + kod + "');";
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
                String query = "UPDATE wlasciciele  " +
                        "SET imie='"+fldImie.getText()+"',nazwisko='"+fldNazwisko.getText()+"'," +
                        "pesel='"+fldPesel.getText()+"',ulica='"+fldUlica.getText()+"'," +
                        "nr_domu='"+fldNrDomu.getText()+"', nr_mieszkania='"+fldNumerMieszkania.getText()+"',kod_pocztowy='"+(fldKod1.getText()+"-"+fldKod2.getText())+"' " +
                        "WHERE w_id='"+idWlasciciela+"'";

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

    private void setData(){
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT imie,nazwisko,pesel,ulica,nr_domu,nr_mieszkania,kod_pocztowy FROM wlasciciele WHERE w_id='" + idWlasciciela + "';";
            ResultSet res = stmt.executeQuery(query);
            res.next();

            fldImie.setText(res.getString(1));
            fldNazwisko.setText(res.getString(2));
            fldPesel.setText(res.getString(3));
            fldUlica.setText(res.getString(4));
            fldNrDomu.setText(res.getString(5));
            fldNumerMieszkania.setText(res.getString(6));
            String temp=res.getString(7);
            fldKod1.setText(temp.substring(0,2));
            fldKod2.setText(temp.substring(3,6));

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void addListeners(){
        fldKod1.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                if(newValue.length()>2)
                    throw new Exception();
                if(newValue.length()!=0)
                    kod1=Integer.parseInt(newValue);

            } catch (Exception e) {
                fldKod1.setText(oldValue);
            }
        });

        fldKod2.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                if(newValue.length()>3)
                    throw new Exception();
                if(newValue.length()!=0)
                    kod1=Integer.parseInt(newValue);

            } catch (Exception e) {
                fldKod2.setText(oldValue);
            }
        });

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

    /**
     * Funkcja sprawdza tylko dla ciągu 11 znaków, inaczej zwraca true
     * @param pesel
     * @return
     */
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

    private void refresh(){
        pc.refresh();
    }

}
