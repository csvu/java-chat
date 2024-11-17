package mop.app.client.controller.admin;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import mop.app.client.util.ViewHelper;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(NavigationController.class);

    @FXML
    private Button dashboardButton;
    @FXML
    private Button userButton;
    @FXML
    private Button groupButton;
    @FXML
    private Button statisticButton;
    @FXML
    private Button spamButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardButton.setOnAction(event -> onDashboard());
        userButton.setOnAction(event -> onUser());
        groupButton.setOnAction(event -> onGroup());
        statisticButton.setOnAction(event -> onStatistic());
        spamButton.setOnAction(event -> onSpam());
    }

    private void onDashboard() {
        logger.info("Dashboard button clicked");
        ViewModel.getInstance().getViewFactory().getSelectedView().set("Dashboard");
    }

    private void onUser() {
        logger.info("User button clicked");
        ViewModel.getInstance().getViewFactory().getSelectedView().set("User");
    }

    private void onGroup() {
        logger.info("Group button clicked");
        ViewModel.getInstance().getViewFactory().getSelectedView().set("Group");
    }

    private void onStatistic() {
        logger.info("Statistic button clicked");
        ViewModel.getInstance().getViewFactory().getSelectedView().set("Statistic");
    }

    private void onSpam() {
        logger.info("Spam button clicked");
        ViewModel.getInstance().getViewFactory().getSelectedView().set("Spam");
    }

    public void handleLogout(ActionEvent event) throws IOException {
        ViewHelper.getIndexScene(event);
    }
}
