package mop.app.client.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import mop.app.client.Client;
import mop.app.client.controller.admin.AdminController;
import mop.app.client.controller.admin.GroupDetailsController;
import mop.app.client.controller.admin.UserActivityController;
import mop.app.client.controller.admin.UserDetailsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewFactory {
    private static final Logger logger = LoggerFactory.getLogger(ViewFactory.class);

    private FXMLLoader groupDetailsLoader;
    private FXMLLoader userDetailsLoader;
    private FXMLLoader userActivityLoader;
    @Getter private final StringProperty selectedView;
    private AnchorPane dashboardView;
    private AnchorPane userView;
    private AnchorPane userDetailsView;
    private AnchorPane userActivityView;
    private AnchorPane userLoginView;
    private AnchorPane newUserView;
    private AnchorPane groupView;
    private AnchorPane groupDetailView;
    private AnchorPane statisticView;
    private AnchorPane spamView;

    public ViewFactory() {
        this.selectedView = new SimpleStringProperty("");
    }

    public void getAdminView() {
        FXMLLoader loader = new FXMLLoader(Client.class.getResource(ViewPath.ADMIN.getPath()));
        AdminController controller = new AdminController();
        loader.setController(controller);
        ViewHelper.getStage(loader, true);
    }

    public AnchorPane getDashboardView() {
        logger.info("Dashboard view requested");
        if (dashboardView == null) {
            try {
                dashboardView = ViewHelper.getView(ViewPath.DASHBOARD.getPath());
            } catch (Exception e) {
                logger.error("Could not load dashboard view: {}", e.getMessage());
            }
        }
        return dashboardView;
    }

    public AnchorPane getUserView() {
        logger.info("User view requested");
        if (userView == null) {
            try {
                userView = ViewHelper.getView(ViewPath.USER.getPath());
            } catch (Exception e) {
                logger.error("Could not load user view: {}", e.getMessage());
            }
        }
        try {
            return ViewHelper.getView(ViewPath.USER.getPath());
        } catch (Exception e) {
            logger.error("Could not load user view: {}", e.getMessage());
        }
        return userView;
    }

    public AnchorPane getGroupView() {
        logger.info("Group view requested");
        if (groupView == null) {
            try {
                groupView = ViewHelper.getView(ViewPath.GROUP.getPath());
            } catch (Exception e) {
                logger.error("Could not load group view: {}", e.getMessage());
            }
        }
        return groupView;
    }

    public AnchorPane getStatisticView() {
        logger.info("Statistic view requested");
        if (statisticView == null) {
            try {
                statisticView = ViewHelper.getView(ViewPath.STATISTIC.getPath());
            } catch (Exception e) {
                logger.error("Could not load statistic view: {}", e.getMessage());
            }
        }
        return statisticView;
    }

    public AnchorPane getSpamView() {
        logger.info("Spam view requested");
        if (spamView == null) {
            try {
                spamView = ViewHelper.getView(ViewPath.SPAM.getPath());
            } catch (Exception e) {
                logger.error("Could not load spam view: {}", e.getMessage());
            }
        }
        return spamView;
    }

    public AnchorPane getGroupDetailsView(long groupId, String groupName) {
        if (groupDetailView == null) {
            try {
                groupDetailsLoader = new FXMLLoader(Client.class.getResource(ViewPath.GROUP_DETAILS.getPath()));
                groupDetailView = groupDetailsLoader.load();
                logger.info("GroupDetailController initialized for group {}, {}", groupId, groupName);
                GroupDetailsController controller = groupDetailsLoader.getController();
                controller.setGroupId(groupId);
                controller.setGroupName(groupName);
            } catch (Exception e) {
                logger.error("Could not load group detail view: {}", e.getMessage());
            }
        } else {
            GroupDetailsController controller = groupDetailsLoader.getController();
            if (controller != null) {
                controller.setGroupId(groupId);
                controller.setGroupName(groupName);
                logger.info("Updated GroupDetailController for group {}, {}", groupId, groupName);
            } else {
                logger.error("Could not retrieve GroupDetailController from existing view.");
            }
        }

        return groupDetailView;
    }

    public AnchorPane getUserDetailsView(long userId, String userName) {
        if (userDetailsView == null) {
            try {
                userDetailsLoader = new FXMLLoader(Client.class.getResource(ViewPath.USER_DETAILS.getPath()));
                userDetailsView = userDetailsLoader.load();
                logger.info("UserDetailsController initialized for user {}, {}", userId, userName);
                UserDetailsController controller = userDetailsLoader.getController();
//                controller.setUserId(userId);
//                controller.setUserName(userName);
            } catch (Exception e) {
                logger.error("Could not load user detail view: {}", e.getMessage());
            }
        } else {
            UserDetailsController controller = userDetailsLoader.getController();
            if (controller != null) {
//                controller.setUserId(userId);
//                controller.setUserName(userName);
                logger.info("Updated UserDetailsController for user {}, {}", userId, userName);
            } else {
                logger.error("Could not retrieve UserDetailsController from existing view.");
            }
        }
        return userDetailsView;
    }

    public AnchorPane getUserActivityView(long userId, String userName) {
        logger.info("User Activity view requested");
        if (userActivityView == null) {
            try {
                userActivityLoader = new FXMLLoader(Client.class.getResource(ViewPath.USER_ACTIVITY.getPath()));
                userActivityView = userActivityLoader.load();
                logger.info("UserActivityController initialized");
                UserActivityController controller = userActivityLoader.getController();
                controller.setUserId(userId);
                controller.setUsername(userName);
            } catch (Exception e) {
                logger.error("Could not load user activity view: {}", e.getMessage());
            }
        }
        return userActivityView;
    }

    public AnchorPane getUserLoginView() {
        logger.info("User Login view requested");
        if (userLoginView == null) {
            try {
                userLoginView = ViewHelper.getView(ViewPath.USER_LOGIN.getPath());
            } catch (Exception e) {
                logger.error("Could not load user login view: {}", e.getMessage());
            }
        }
        return userLoginView;
    }

    public AnchorPane getNewUserView() {
        logger.info("New User view requested");
        if (newUserView == null) {
            try {
                newUserView = ViewHelper.getView(ViewPath.NEW_USER.getPath());
            } catch (Exception e) {
                logger.error("Could not load new user view: {}", e.getMessage());
            }
        }
        return newUserView;
    }
}
