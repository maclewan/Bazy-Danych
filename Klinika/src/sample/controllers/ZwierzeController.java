package sample.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.Classes.SuperArrayList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ZwierzeController {

    private Stage stage;
    private Connection conn;
    private Statement stmt;
    private String query;
    private ResultSet res;
    private PanelController pc;
    private String idPacjenta;
    private char userTyp;

    public ZwierzeController(Stage s,Connection c, PanelController p, String idPacjenta, char userTyp){
        stage=s;
        conn=c;
        pc=p;
        this.idPacjenta=idPacjenta;
        this.userTyp= userTyp;
    }

    @FXML
    private void initialize(){
        if((userTyp=='w'||userTyp=='s')){
            btnUsunZwierze.setVisible(false);
        }
        if(userTyp=='w'){
            btnEdytuj.setVisible(false);
        }

        try {
            query = "SELECT nazwa,rasa,gatunek,rok_urodzenia AS rok, umaszczenie FROM pacjenci JOIN rasy ON id_rasy=r_id WHERE p_id='" + idPacjenta + "';";
            stmt = conn.createStatement();
            res = stmt.executeQuery(query);
            res.next();
            String temp="Id: "+ idPacjenta+", imie: " +res.getString(1)+", rasa: "+res.getString(2)+", gatunek: "+res.getString(3);
            lblZwierz1.setText(temp);
            temp="rok urodzenia: "+res.getString(4)+", umaszczenie: "+res.getString(5);
            lblZwierz2.setText(temp);
            updateData();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private void updateData(){
        try {
            query = "SELECT n_id, godzina,dzien FROM (terminy JOIN wizyty ON g_id=id_terminu ) JOIN notatki ON id_wizyty=w_id " +
                    "WHERE notatki.id_pacjenta ='"+idPacjenta+ "' " +
                    "ORDER BY dzien, godzina, n_id;";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);

            SuperArrayList<String> listaNotId = new SuperArrayList<>();
            SuperArrayList<Label> listaLabel = new SuperArrayList<>();
            SuperArrayList<Button> listaButton = new SuperArrayList<>();

            int counter=0;
            while (res.next()) {
                int j = gridNotatki.getRowCount();
                listaNotId.add(res.getString(1));

                String temp=res.getString(2)+" "+res.getString(3);
                listaLabel.add(new Label(temp));
                gridNotatki.add(listaLabel.getLast(), 0, j);


                listaButton.add(new Button("Więcej"));
                gridNotatki.add(listaButton.getLast(), 1, j);
                int finalCounter = counter;
                listaButton.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //todo: wyswietl wiecej info z notatki korzystajac z listaNOtid i counter
                    }
                });
                counter++;

            }




        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private Button btnEdytuj;

    @FXML
    private Button btnUmowWizyte;

    @FXML
    private Button btnUsunZwierze;

    @FXML
    private Label lblZwierz1;

    @FXML
    private GridPane gridNotatki;

    @FXML
    private Label lblZwierz2;

    @FXML
    void btnEdytujOnAction(ActionEvent event) {
        //todo: JAKOŚ edytuj

    }

    @FXML
    void btnUmowWizyteOnAction(ActionEvent event) {
        //todo:
    }

    @FXML
    void btnUsunZwierzeOnAction(ActionEvent event) {
        //todo:
    }

}
