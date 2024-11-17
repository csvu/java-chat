package mop.app.client.controller.admin;

import com.github.javafaker.Faker;
import java.time.Year;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML
    public BarChart<String, Number> barChart;
    @FXML
    public CategoryAxis monthAxis;
    @FXML
    public NumberAxis userAxis;

    @FXML
    public void initialize() {
        logger.info("Initializing DashboardController");
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

    private int[] getMonthlyRegistrations(String year) {
        Faker faker = new Faker(Locale.ENGLISH);
        int[] registrations = new int[12];
        for (int i = 0; i < registrations.length; i++) {
            registrations[i] = faker.number().numberBetween(5, 70);
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
