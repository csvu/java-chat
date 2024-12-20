package mop.app.client.controller.admin;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import mop.app.client.util.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ViewModel.getInstance().getViewFactory().getSelectedView().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Dashboard":
                    // 5 => New User Registration (Number of Users) => Detailed New User Registration
                    // 2 => Login Activity => Detailed Login Activity
                    // 6 => Statistic View User Registration This Year
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getDashboardView());
                    break;
                case "UserLogin":
                    // 2 => Login Activity => Detailed Login Activity
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getUserLoginView());
                    break;
                case "NewUser":
                    // 5 => New User Registration (Number of Users) => Detailed New User Registration
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getNewUserView());
                    break;
                case "User":
                    // 1 => User View (Add User) => Detailed User View (Update User, Delete User)
                    // 7 => Friend List (Number of Friends) => Detailed Friend List
                    // 8 => Active Users (Number of Users) => Detailed Active Users
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getUserView());
                    break;
                case "CreateUser":
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getCreateUserView());
                    break;
                case "UserActivity":
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().gerUserActivityView());
                    break;
                case "Friend":
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getFriendView());
                    break;
                case "Group":
                    // 3 => Group View
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getGroupView());
                    break;
                case "Statistic":
                    // 6 + 9 => Chart View
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getStatisticView());
                    break;
                case "Spam":
                    // 4 => Spam View
                    borderPane.setCenter(ViewModel.getInstance().getViewFactory().getSpamView());
                    break;
                default:
                    if (newValue.startsWith("Group-")) {
                        // Group-1-Group1 => Group ID: 1, Group Name: Group1
                        long groupId = Integer.parseInt(newValue.split("-")[1]);
                        String groupName = newValue.split("-")[2];
                        logger.info("Group detail view requested for group id: {}, group name: {}", groupId, groupName);
                        borderPane.setCenter(
                            ViewModel.getInstance().getViewFactory().getGroupDetailsView(groupId, groupName));
                    } else if (newValue.startsWith("UserRelation-")) {
                        // User-1-Nam => User ID: 1, User Name: Nam
                        long userId = Integer.parseInt(newValue.split("-")[1]);
                        String userName = newValue.split("-")[2];
                        logger.info("User detail view requested for user id: {}, user name: {}", userId, userName);
                        borderPane.setCenter(
                            ViewModel.getInstance().getViewFactory().getUserRelationView(userId, userName));
                    } else if (newValue.startsWith("User-")) {
                        // User-1-Nam => User ID: 1, User Name: Nam
                        long userId = Integer.parseInt(newValue.split("-")[1]);
                        String userName = newValue.split("-")[2];
                        logger.info("User detail view requested for user id: {}, user name: {}", userId, userName);
                        borderPane.setCenter(
                            ViewModel.getInstance().getViewFactory().getUserDetailsView(userId, userName));
                    }
            }
        });
    }
}