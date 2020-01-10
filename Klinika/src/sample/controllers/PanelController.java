package sample.controllers;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
        import javafx.scene.control.Button;
        import javafx.scene.control.Tab;
        import javafx.scene.control.TextField;
        import javafx.scene.layout.GridPane;

public class PanelController {

    /**
     * Tab Zwierzęta
     */
    @FXML
    private Button btnZwierzDodajZw;

    @FXML
    private GridPane gridZwierz;

    @FXML
    private TextField fldZwierzWlasc;

    @FXML
    private TextField fldZwierzImie;

    @FXML
    private TextField fldZwierzGatunek;

    @FXML
    private Button btnZwierzFiltruj;

    @FXML
    private Button btnZwierzUsunFiltr;

    /**
     * Tab Umówione Wizyty
     */

    @FXML
    private GridPane girdWizyty;

    @FXML
    private TextField fldWizWlasciciel;

    @FXML
    private TextField fldWizZwierz;

    @FXML
    private Button btnUsunFiltr;

    @FXML
    private Button btnWizFiltruj;

    @FXML
    private Button btnWizEdytuj;


    /**
     * Tab Właściciele
     */

    @FXML
    private Button btnWlDodaj;

    @FXML
    private GridPane gridWlasciciel;

    @FXML
    private Button btnWlUsunFiltr;

    @FXML
    private Button btnWlFiltruj;

    @FXML
    private TextField lblWlImie;

    @FXML
    private TextField lblWlNazwisko;

    @FXML
    private TextField lblWlMiasto;

    @FXML
    private TextField lblWlKod;

    /**
     * Tab Pracownicy
     */

    @FXML
    private Tab tabPracownicy;

    @FXML
    private Button btnPracDodajPrac;

    @FXML
    private GridPane gridPracownicy;

    @FXML
    private Button btnPracUsunFIltr;

    @FXML
    private Button btnPracFiltr;

    @FXML
    private TextField lblPracImie;

    @FXML
    private TextField lblPracNazwisko;

    @FXML
    private TextField lblPracZawod;

    /**
     * Tab Pracownicy
     */

    @FXML
    void btnPracDodajPracOnAction(ActionEvent event) {

    }

    @FXML
    void btnPracFiltrOnAction(ActionEvent event) {

    }

    @FXML
    void btnPracUsunFIltrOnAction(ActionEvent event) {

    }

    @FXML
    void btnUsunFiltrOnAction(ActionEvent event) {

    }

    /**
     * Tab Umówione Wizyty
     */


    @FXML
    void btnWizEdytujOnAction(ActionEvent event) {

    }

    @FXML
    void btnWizFiltrujOnAction(ActionEvent event) {

    }

    /**
     * Tab Właściciele
     */

    @FXML
    void btnWlDodajOnAction(ActionEvent event) {

    }

    @FXML
    void btnWlFiltrujOnAction(ActionEvent event) {

    }

    @FXML
    void btnWlUsunFiltrOnAction(ActionEvent event) {

    }

    /**
     * Tab Zwierzęta
     */

    @FXML
    void btnZwierzDodajZwOnAction(ActionEvent event) {

    }

    @FXML
    void btnZwierzFiltrujOnAction(ActionEvent event) {

    }

    @FXML
    void btnZwierzUsunFiltrOnAction(ActionEvent event) {

    }

}
