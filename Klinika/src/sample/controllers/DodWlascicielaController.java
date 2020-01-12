package sample.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DodWlascicielaController {

    private int kod1;
    private int kod2;
    private long pesel;
    private boolean isOkPesel;

    @FXML
    private void initialize(){
        lblTerminal.setText("");
        btnZatwierdz.setDisable(true);
        addListeners();
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
                    throw new Exception();
                if(newValue.length()!=0)
                    pesel=Long.parseLong(newValue);
                if(checkPesel(newValue))
                    lblTerminal.setText("");
                else
                    lblTerminal.setText("Niepoprawny pesel");

            } catch (Exception e) {
                fldPesel.setText(oldValue);
                lblTerminal.setText("Niepoprawny pesel");

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

}
