package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.Year;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis monthAxis;
    @FXML
    private NumberAxis userAxis;
    @FXML
    private Label todayRegistrationsLabel;
    @FXML
    private Label activeUsersLabel;
    @FXML
    private StackPane activeUsersCard;
    @FXML
    private StackPane todayRegistrationsCard;

    private final Faker faker = new Faker(Locale.ENGLISH);

    @FXML
    public void initialize() {
        logger.info("Initializing DashboardController");
        setupRegistrationChart();
        updateMetricCards();
    }

    private void setupRegistrationChart() {
        int currentYear = Year.now().getValue();
        userAxis.setLabel("New Registrations");
        barChart.setTitle("New Registrations in " + currentYear);

        int[] data = getMonthlyRegistrations(String.valueOf(currentYear));

        barChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("New Registrations");

        for (int month = 0; month < data.length; month++) {
            series.getData().add(new XYChart.Data<>(getMonthName(month), data[month]));
        }

        barChart.getData().add(series);
    }

    private void updateMetricCards() {
        // In a real application, these would come from your service layer
        int todayRegistrations = faker.number().numberBetween(0, 20);
        int activeUsers = faker.number().numberBetween(10, 100);

        todayRegistrationsLabel.setText(String.valueOf(todayRegistrations));
        activeUsersLabel.setText(String.valueOf(activeUsers));
    }

    @FXML
    public void showActiveUsers(MouseEvent mouseEvent) {
        logger.info("Showing active users list");
        ViewModel.getInstance().getViewFactory().getSelectedView().set("UserLogin");
    }

    public void showTodayRegistrations(MouseEvent mouseEvent) {
        logger.info("Showing today's registrations list");
        ViewModel.getInstance().getViewFactory().getSelectedView().set("NewUser");
    }

    private int[] getMonthlyRegistrations(String year) {
        int[] registrations = new int[12];
        int currentMonth = LocalDate.now().getMonthValue();

        for (int i = 0; i < currentMonth; i++) {
            registrations[i] = faker.number().numberBetween(5, 70);
        }

        for (int i = currentMonth; i < registrations.length; i++) {
            registrations[i] = 0;
        }

        return registrations;
    }

    private String getMonthName(int month) {
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return months[month];
    }
}