package mop.app.client.controller.admin;

import java.time.Year;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import mop.app.client.dao.UserManagementDAO;

import java.util.List;

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

    private final UserManagementDAO userManagementDAO;

    public StatisticsController() {
        userManagementDAO = new UserManagementDAO();
    }

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

        userAxis.setLowerBound(0);
        userAxis.setUpperBound(10);
        userAxis.setTickUnit(1);

        Task<int[]> dataTask = new Task<>() {
            @Override
            protected int[] call() throws Exception {
                if (selectedFilter.equals("Monthly Active Users")) {
                    return loadMonthlyActiveUsers(selectedYear);
                } else {
                    return loadMonthlyRegistrations(selectedYear);
                }
            }
        };

        dataTask.setOnSucceeded(event1 -> {
            int[] data = dataTask.getValue();
            updateChart(data, selectedFilter);
        });

        new Thread(dataTask).start();
    }

    private int[] loadMonthlyRegistrations(String year) {
        int[] registrations = new int[12];

        List<Object[]> registrationData = userManagementDAO.getNewRegistrationsByMonth(Integer.parseInt(year));
        for (Object[] entry : registrationData) {
            Integer month = (Integer) entry[0];
            Long count = (Long) entry[1];
            registrations[month - 1] = count.intValue();
        }
        return registrations;
    }

    private int[] loadMonthlyActiveUsers(String year) {
        int[] activeUsers = new int[12];

        List<Object[]> activeUserData = userManagementDAO.getActiveUsersByMonth(Integer.parseInt(year));
        for (Object[] entry : activeUserData) {
            Integer month = (Integer) entry[0];
            Long count = (Long) entry[1];
            activeUsers[month - 1] = count.intValue();
        }
        return activeUsers;
    }

    private void updateChart(int[] data, String selectedFilter) {
        barChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedFilter.equals("Monthly Active Users") ? "Active Users" : "New Registrations");

        for (int month = 0; month < data.length; month++) {
            series.getData().add(new XYChart.Data<>(getMonthName(month), data[month]));
        }

        barChart.getData().add(series);

        monthAxis.setCategories(FXCollections.observableArrayList(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        ));

        barChart.layout();
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

        applyFilter(null);
    }

    private void populateYearComboBox(int currentYear) {
        yearComboBox.getItems().clear();
        for (int i = 0; i < 10; i++) {
            yearComboBox.getItems().add(String.valueOf(currentYear - i));
        }
    }

    private String getMonthName(int month) {
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return months[month];
    }
}
