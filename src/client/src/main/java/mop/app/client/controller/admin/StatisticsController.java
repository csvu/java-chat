package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.util.Locale;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

import java.time.Year;

public class StatisticsController {
    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis monthAxis;

    @FXML
    private NumberAxis userAxis;

    @FXML
    public void applyFilter(ActionEvent event) {
        String selectedYear = yearComboBox.getValue();
        String selectedFilter = filterComboBox.getValue();

        if (selectedFilter.equals("Monthly Active Users")) {
            userAxis.setLabel("Active Users");
            barChart.setTitle("Monthly Active Users");
        } else {
            userAxis.setLabel("New Registrations");
            barChart.setTitle("Monthly Registrations");
        }

        int[] data = selectedFilter.equals("Monthly Active Users") ?
            getMonthlyActiveUsers(selectedYear) :
            getMonthlyRegistrations(selectedYear);

        barChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        if (selectedFilter.equals("Monthly Active Users")) {
            series.setName("Active Users");
        } else {
            series.setName("New Registrations");
        }

        for (int month = 0; month < data.length; month++) {
            series.getData().add(new XYChart.Data<>(getMonthName(month), data[month]));
        }

        barChart.getData().add(series);
    }

    @FXML
    public void initialize() {
        filterComboBox.getItems().addAll("Monthly Registrations", "Monthly Active Users");
        filterComboBox.getSelectionModel().select("Monthly Registrations");

        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            applyFilter(null);
        });

        int currentYear = Year.now().getValue();
        populateYearComboBox(currentYear);
        yearComboBox.setValue(String.valueOf(currentYear));

//        yearComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            applyFilter(null);
//        });

        applyFilter(null);
    }

    private void populateYearComboBox(int currentYear) {
        yearComboBox.getItems().clear();
        for (int i = 0; i < 10; i++) {
            yearComboBox.getItems().add(String.valueOf(currentYear - i));
        }
    }

    private int[] getMonthlyRegistrations(String year) {
        Faker faker = new Faker(Locale.ENGLISH);
        int[] registrations = new int[12];
        for (int i = 0; i < registrations.length; i++) {
            registrations[i] = faker.number().numberBetween(5, 70);
        }
        return registrations;
    }

    private int[] getMonthlyActiveUsers(String year) {
        Faker faker = new Faker(Locale.ENGLISH);
        int[] activeUsers = new int[12];
        for (int i = 0; i < activeUsers.length; i++) {
            activeUsers[i] = faker.number().numberBetween(50, 200);
        }
        return activeUsers;
    }

    private String getMonthName(int month) {
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return months[month];
    }
}
