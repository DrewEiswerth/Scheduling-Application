package org.SchedulingApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.SchedulingApplication.Model.ComboBoxItem;
import org.SchedulingApplication.Utilities.ComboBoxFiller;
import org.SchedulingApplication.Utilities.DBCustomerCounter;
import org.SchedulingApplication.Utilities.ReportsFiller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    String selectedYear;        // these allow each ComboBox selection to be saved independently, and whenever a new
    String selectedOffice;      // selection is made in any ComboBox, graph values can be tallied based on the
    String selectedContactID;   // selections made in all ComboBoxes

    @FXML
    private ComboBox<ComboBoxItem> countryComboBox;

    @FXML
    private ComboBox<ComboBoxItem> divisionComboBox;

    @FXML
    private Label customerCountLabel;

    @FXML
    private ComboBox<ComboBoxItem> yearComboBox;

    @FXML
    private ComboBox<ComboBoxItem> officeComboBox;

    @FXML
    private ComboBox<ComboBoxItem> contactComboBox;

    @FXML
    private StackedBarChart<String, Integer> reportsBarChart;

    @FXML
    private NumberAxis yAxis;

    XYChart.Series<String, Integer> phoneSeries = new XYChart.Series<>();
    XYChart.Series<String, Integer> zoomSeries = new XYChart.Series<>();
    XYChart.Series<String, Integer> inPersonSeries = new XYChart.Series<>();

    @FXML
    public void onActionCountryComboBox() {

        if(countryComboBox.getValue().getName().equals("ALL")) {
            divisionComboBox.setValue(null);
            divisionComboBox.setDisable(true);

            customerCountLabel.setText(DBCustomerCounter.getCount(null));
        }
        else {
            divisionComboBox.setDisable(false);

            String countryID = String.valueOf(countryComboBox.getValue().getId());

            divisionComboBox.setItems(ComboBoxFiller.requestGenericList("divisions", "division_name",
                    "division_id", countryID, true, false));

            divisionComboBox.getSelectionModel().selectFirst();  // selects "ALL", and allows all location filtering
        }                                                        // to be handled in onActionDivisionComboBox()
    }

    @FXML
    public void onActionDivisionComboBox() {

        if(divisionComboBox.getValue() != null) {  // this stops divisionComboBox.setItems from triggering this method

            String divisionID= String.valueOf(divisionComboBox.getValue().getId());
            String countryID = String.valueOf(countryComboBox.getValue().getId());

            if(divisionComboBox.getValue().getName().equals("ALL")) {
                customerCountLabel.setText(DBCustomerCounter.getCountByCountry(countryID));
            }
            else {
                customerCountLabel.setText(DBCustomerCounter.getCount(divisionID));
            }
        }
    }

    @FXML
    public void onActionYearComboBox() {

        String year = yearComboBox.getValue().getName();

        if(year.equals("ALL")) {
            selectedYear = null;
        }
        else {
            selectedYear = year;
        }

        int[][] graphValues = ReportsFiller.retrieveGraphValues(selectedYear, selectedOffice, selectedContactID);

        phoneSeries.getData().set(0, new XYChart.Data<>("January", graphValues[0][0]));
        phoneSeries.getData().set(1, new XYChart.Data<>("February", graphValues[0][1]));
        phoneSeries.getData().set(2, new XYChart.Data<>("March", graphValues[0][2]));
        phoneSeries.getData().set(3, new XYChart.Data<>("April", graphValues[0][3]));
        phoneSeries.getData().set(4, new XYChart.Data<>("May", graphValues[0][4]));
        phoneSeries.getData().set(5, new XYChart.Data<>("June", graphValues[0][5]));
        phoneSeries.getData().set(6, new XYChart.Data<>("July", graphValues[0][6]));
        phoneSeries.getData().set(7, new XYChart.Data<>("August", graphValues[0][7]));
        phoneSeries.getData().set(8, new XYChart.Data<>("September", graphValues[0][8]));
        phoneSeries.getData().set(9, new XYChart.Data<>("October", graphValues[0][9]));
        phoneSeries.getData().set(10, new XYChart.Data<>("November", graphValues[0][10]));
        phoneSeries.getData().set(11, new XYChart.Data<>("December", graphValues[0][11]));

        zoomSeries.getData().set(0, new XYChart.Data<>("January", graphValues[1][0]));
        zoomSeries.getData().set(1, new XYChart.Data<>("February", graphValues[1][1]));
        zoomSeries.getData().set(2, new XYChart.Data<>("March", graphValues[1][2]));
        zoomSeries.getData().set(3, new XYChart.Data<>("April", graphValues[1][3]));
        zoomSeries.getData().set(4, new XYChart.Data<>("May", graphValues[1][4]));
        zoomSeries.getData().set(5, new XYChart.Data<>("June", graphValues[1][5]));
        zoomSeries.getData().set(6, new XYChart.Data<>("July", graphValues[1][6]));
        zoomSeries.getData().set(7, new XYChart.Data<>("August", graphValues[1][7]));
        zoomSeries.getData().set(8, new XYChart.Data<>("September", graphValues[1][8]));
        zoomSeries.getData().set(9, new XYChart.Data<>("October", graphValues[1][9]));
        zoomSeries.getData().set(10, new XYChart.Data<>("November", graphValues[1][10]));
        zoomSeries.getData().set(11, new XYChart.Data<>("December", graphValues[1][11]));

        inPersonSeries.getData().set(0, new XYChart.Data<>("January", graphValues[2][0]));
        inPersonSeries.getData().set(1, new XYChart.Data<>("February", graphValues[2][1]));
        inPersonSeries.getData().set(2, new XYChart.Data<>("March", graphValues[2][2]));
        inPersonSeries.getData().set(3, new XYChart.Data<>("April", graphValues[2][3]));
        inPersonSeries.getData().set(4, new XYChart.Data<>("May", graphValues[2][4]));
        inPersonSeries.getData().set(5, new XYChart.Data<>("June", graphValues[2][5]));
        inPersonSeries.getData().set(6, new XYChart.Data<>("July", graphValues[2][6]));
        inPersonSeries.getData().set(7, new XYChart.Data<>("August", graphValues[2][7]));
        inPersonSeries.getData().set(8, new XYChart.Data<>("September", graphValues[2][8]));
        inPersonSeries.getData().set(9, new XYChart.Data<>("October", graphValues[2][9]));
        inPersonSeries.getData().set(10, new XYChart.Data<>("November", graphValues[2][10]));
        inPersonSeries.getData().set(11, new XYChart.Data<>("December", graphValues[2][11]));
    }

    @FXML
    public void onActionLocationComboBox() {

        String office = officeComboBox.getValue().getName();

        if(office.equals("ALL")) {
            selectedOffice = null;
        }
        else {
            selectedOffice = office;
        }

        int[][] graphValues = ReportsFiller.retrieveGraphValues(selectedYear, selectedOffice, selectedContactID);

        phoneSeries.getData().set(0, new XYChart.Data<>("January", graphValues[0][0]));
        phoneSeries.getData().set(1, new XYChart.Data<>("February", graphValues[0][1]));
        phoneSeries.getData().set(2, new XYChart.Data<>("March", graphValues[0][2]));
        phoneSeries.getData().set(3, new XYChart.Data<>("April", graphValues[0][3]));
        phoneSeries.getData().set(4, new XYChart.Data<>("May", graphValues[0][4]));
        phoneSeries.getData().set(5, new XYChart.Data<>("June", graphValues[0][5]));
        phoneSeries.getData().set(6, new XYChart.Data<>("July", graphValues[0][6]));
        phoneSeries.getData().set(7, new XYChart.Data<>("August", graphValues[0][7]));
        phoneSeries.getData().set(8, new XYChart.Data<>("September", graphValues[0][8]));
        phoneSeries.getData().set(9, new XYChart.Data<>("October", graphValues[0][9]));
        phoneSeries.getData().set(10, new XYChart.Data<>("November", graphValues[0][10]));
        phoneSeries.getData().set(11, new XYChart.Data<>("December", graphValues[0][11]));

        zoomSeries.getData().set(0, new XYChart.Data<>("January", graphValues[1][0]));
        zoomSeries.getData().set(1, new XYChart.Data<>("February", graphValues[1][1]));
        zoomSeries.getData().set(2, new XYChart.Data<>("March", graphValues[1][2]));
        zoomSeries.getData().set(3, new XYChart.Data<>("April", graphValues[1][3]));
        zoomSeries.getData().set(4, new XYChart.Data<>("May", graphValues[1][4]));
        zoomSeries.getData().set(5, new XYChart.Data<>("June", graphValues[1][5]));
        zoomSeries.getData().set(6, new XYChart.Data<>("July", graphValues[1][6]));
        zoomSeries.getData().set(7, new XYChart.Data<>("August", graphValues[1][7]));
        zoomSeries.getData().set(8, new XYChart.Data<>("September", graphValues[1][8]));
        zoomSeries.getData().set(9, new XYChart.Data<>("October", graphValues[1][9]));
        zoomSeries.getData().set(10, new XYChart.Data<>("November", graphValues[1][10]));
        zoomSeries.getData().set(11, new XYChart.Data<>("December", graphValues[1][11]));

        inPersonSeries.getData().set(0, new XYChart.Data<>("January", graphValues[2][0]));
        inPersonSeries.getData().set(1, new XYChart.Data<>("February", graphValues[2][1]));
        inPersonSeries.getData().set(2, new XYChart.Data<>("March", graphValues[2][2]));
        inPersonSeries.getData().set(3, new XYChart.Data<>("April", graphValues[2][3]));
        inPersonSeries.getData().set(4, new XYChart.Data<>("May", graphValues[2][4]));
        inPersonSeries.getData().set(5, new XYChart.Data<>("June", graphValues[2][5]));
        inPersonSeries.getData().set(6, new XYChart.Data<>("July", graphValues[2][6]));
        inPersonSeries.getData().set(7, new XYChart.Data<>("August", graphValues[2][7]));
        inPersonSeries.getData().set(8, new XYChart.Data<>("September", graphValues[2][8]));
        inPersonSeries.getData().set(9, new XYChart.Data<>("October", graphValues[2][9]));
        inPersonSeries.getData().set(10, new XYChart.Data<>("November", graphValues[2][10]));
        inPersonSeries.getData().set(11, new XYChart.Data<>("December", graphValues[2][11]));
    }

    @FXML
    public void onActionContactComboBox() {

        int contactID = contactComboBox.getValue().getId();

        if(contactID == -1) {   // "ALL"
            selectedContactID = null;
        }
        else {
            selectedContactID = String.valueOf(contactID);
        }

        int[][] graphValues = ReportsFiller.retrieveGraphValues(selectedYear, selectedOffice, selectedContactID);

        phoneSeries.getData().set(0, new XYChart.Data<>("January", graphValues[0][0]));
        phoneSeries.getData().set(1, new XYChart.Data<>("February", graphValues[0][1]));
        phoneSeries.getData().set(2, new XYChart.Data<>("March", graphValues[0][2]));
        phoneSeries.getData().set(3, new XYChart.Data<>("April", graphValues[0][3]));
        phoneSeries.getData().set(4, new XYChart.Data<>("May", graphValues[0][4]));
        phoneSeries.getData().set(5, new XYChart.Data<>("June", graphValues[0][5]));
        phoneSeries.getData().set(6, new XYChart.Data<>("July", graphValues[0][6]));
        phoneSeries.getData().set(7, new XYChart.Data<>("August", graphValues[0][7]));
        phoneSeries.getData().set(8, new XYChart.Data<>("September", graphValues[0][8]));
        phoneSeries.getData().set(9, new XYChart.Data<>("October", graphValues[0][9]));
        phoneSeries.getData().set(10, new XYChart.Data<>("November", graphValues[0][10]));
        phoneSeries.getData().set(11, new XYChart.Data<>("December", graphValues[0][11]));

        zoomSeries.getData().set(0, new XYChart.Data<>("January", graphValues[1][0]));
        zoomSeries.getData().set(1, new XYChart.Data<>("February", graphValues[1][1]));
        zoomSeries.getData().set(2, new XYChart.Data<>("March", graphValues[1][2]));
        zoomSeries.getData().set(3, new XYChart.Data<>("April", graphValues[1][3]));
        zoomSeries.getData().set(4, new XYChart.Data<>("May", graphValues[1][4]));
        zoomSeries.getData().set(5, new XYChart.Data<>("June", graphValues[1][5]));
        zoomSeries.getData().set(6, new XYChart.Data<>("July", graphValues[1][6]));
        zoomSeries.getData().set(7, new XYChart.Data<>("August", graphValues[1][7]));
        zoomSeries.getData().set(8, new XYChart.Data<>("September", graphValues[1][8]));
        zoomSeries.getData().set(9, new XYChart.Data<>("October", graphValues[1][9]));
        zoomSeries.getData().set(10, new XYChart.Data<>("November", graphValues[1][10]));
        zoomSeries.getData().set(11, new XYChart.Data<>("December", graphValues[1][11]));

        inPersonSeries.getData().set(0, new XYChart.Data<>("January", graphValues[2][0]));
        inPersonSeries.getData().set(1, new XYChart.Data<>("February", graphValues[2][1]));
        inPersonSeries.getData().set(2, new XYChart.Data<>("March", graphValues[2][2]));
        inPersonSeries.getData().set(3, new XYChart.Data<>("April", graphValues[2][3]));
        inPersonSeries.getData().set(4, new XYChart.Data<>("May", graphValues[2][4]));
        inPersonSeries.getData().set(5, new XYChart.Data<>("June", graphValues[2][5]));
        inPersonSeries.getData().set(6, new XYChart.Data<>("July", graphValues[2][6]));
        inPersonSeries.getData().set(7, new XYChart.Data<>("August", graphValues[2][7]));
        inPersonSeries.getData().set(8, new XYChart.Data<>("September", graphValues[2][8]));
        inPersonSeries.getData().set(9, new XYChart.Data<>("October", graphValues[2][9]));
        inPersonSeries.getData().set(10, new XYChart.Data<>("November", graphValues[2][10]));
        inPersonSeries.getData().set(11, new XYChart.Data<>("December", graphValues[2][11]));
    }

    @FXML
    public void onActionBack(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Main Screen");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        countryComboBox.setItems(ComboBoxFiller.requestGenericList("countries", "country_name",
                "country_id", null, true, false));

        divisionComboBox.setDisable(true);

        yearComboBox.setItems(ComboBoxFiller.requestYearList());
        officeComboBox.setItems(ComboBoxFiller.requestOfficeList(true));
        contactComboBox.setItems(ComboBoxFiller.requestGenericList("contacts", "contact_name",
                "contact_id", null, true, false));

        yearComboBox.getSelectionModel().selectFirst();    // selects "ALL" for each ComboBox
        officeComboBox.getSelectionModel().selectFirst();
        contactComboBox.getSelectionModel().selectFirst();

        int[][] graphValues = ReportsFiller.retrieveGraphValues(selectedYear, selectedOffice, selectedContactID);

        phoneSeries.setName("PHONE");
        phoneSeries.getData().add(new XYChart.Data<>("January", graphValues[0][0]));
        phoneSeries.getData().add(new XYChart.Data<>("February", graphValues[0][1]));
        phoneSeries.getData().add(new XYChart.Data<>("March", graphValues[0][2]));
        phoneSeries.getData().add(new XYChart.Data<>("April", graphValues[0][3]));
        phoneSeries.getData().add(new XYChart.Data<>("May", graphValues[0][4]));
        phoneSeries.getData().add(new XYChart.Data<>("June", graphValues[0][5]));
        phoneSeries.getData().add(new XYChart.Data<>("July", graphValues[0][6]));
        phoneSeries.getData().add(new XYChart.Data<>("August", graphValues[0][7]));
        phoneSeries.getData().add(new XYChart.Data<>("September", graphValues[0][8]));
        phoneSeries.getData().add(new XYChart.Data<>("October", graphValues[0][9]));
        phoneSeries.getData().add(new XYChart.Data<>("November", graphValues[0][10]));
        phoneSeries.getData().add(new XYChart.Data<>("December", graphValues[0][11]));

        zoomSeries.setName("ZOOM");
        zoomSeries.getData().add(new XYChart.Data<>("January", graphValues[1][0]));
        zoomSeries.getData().add(new XYChart.Data<>("February", graphValues[1][1]));
        zoomSeries.getData().add(new XYChart.Data<>("March", graphValues[1][2]));
        zoomSeries.getData().add(new XYChart.Data<>("April", graphValues[1][3]));
        zoomSeries.getData().add(new XYChart.Data<>("May", graphValues[1][4]));
        zoomSeries.getData().add(new XYChart.Data<>("June", graphValues[1][5]));
        zoomSeries.getData().add(new XYChart.Data<>("July", graphValues[1][6]));
        zoomSeries.getData().add(new XYChart.Data<>("August", graphValues[1][7]));
        zoomSeries.getData().add(new XYChart.Data<>("September", graphValues[1][8]));
        zoomSeries.getData().add(new XYChart.Data<>("October", graphValues[1][9]));
        zoomSeries.getData().add(new XYChart.Data<>("November", graphValues[1][10]));
        zoomSeries.getData().add(new XYChart.Data<>("December", graphValues[1][11]));

        inPersonSeries.setName("IN-PERSON");
        inPersonSeries.getData().add(new XYChart.Data<>("January", graphValues[2][0]));
        inPersonSeries.getData().add(new XYChart.Data<>("February", graphValues[2][1]));
        inPersonSeries.getData().add(new XYChart.Data<>("March", graphValues[2][2]));
        inPersonSeries.getData().add(new XYChart.Data<>("April", graphValues[2][3]));
        inPersonSeries.getData().add(new XYChart.Data<>("May", graphValues[2][4]));
        inPersonSeries.getData().add(new XYChart.Data<>("June", graphValues[2][5]));
        inPersonSeries.getData().add(new XYChart.Data<>("July", graphValues[2][6]));
        inPersonSeries.getData().add(new XYChart.Data<>("August", graphValues[2][7]));
        inPersonSeries.getData().add(new XYChart.Data<>("September", graphValues[2][8]));
        inPersonSeries.getData().add(new XYChart.Data<>("October", graphValues[2][9]));
        inPersonSeries.getData().add(new XYChart.Data<>("November", graphValues[2][10]));
        inPersonSeries.getData().add(new XYChart.Data<>("December", graphValues[2][11]));

        reportsBarChart.setAnimated(false);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(12);  // will need to be adjusted if daily appointments exceed 12
        yAxis.setTickUnit(1);

        reportsBarChart.getData().add(phoneSeries);
        reportsBarChart.getData().add(zoomSeries);
        reportsBarChart.getData().add(inPersonSeries);
    }
}
