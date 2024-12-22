package mop.app.client.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import mop.app.client.Client;
import mop.app.client.controller.admin.AdminController;
import mop.app.client.controller.admin.GroupDetailsController;
import mop.app.client.controller.admin.UserRelationController;
import mop.app.client.controller.admin.UserDetailsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewFactory {
    private static final Logger logger = LoggerFactory.getLogger(ViewFactory.class);

    private FXMLLoader groupDetailsLoader;
    private FXMLLoader userDetailsLoader;
    private FXMLLoader userRelationLoader;
    @Getter private final StringProperty selectedView;
    private AnchorPane dashboardView;
    private AnchorPane userView;
    private AnchorPane createUserView;
    private AnchorPane userDetailsView;
    private AnchorPane userRelationView;
    private AnchorPane userLoginView;
    private AnchorPane userActivityView;
    private AnchorPane newUserView;
    private AnchorPane friendView;
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
        try {
            dashboardView = ViewHelper.getView(ViewPath.DASHBOARD.getPath());
        } catch (Exception e) {
            logger.error("Could not load dashboard view: {}", e.getMessage());
        }
        return dashboardView;
    }

    public AnchorPane getUserView() {
        logger.info("User view requested");
        try {
            userView = ViewHelper.getView(ViewPath.USER.getPath());
        } catch (Exception e) {
            logger.error("Could not load user view: {}", e.getMessage());
        }
        return userView;
    }

    public AnchorPane getFriendView() {
        logger.info("Friend view requested");
        try {
            friendView = ViewHelper.getView(ViewPath.FRIEND.getPath());
        } catch (Exception e) {
            logger.error("Could not load friend view: {}", e.getMessage());
        }
        return friendView;
    }

    public AnchorPane getGroupView() {
        logger.info("Group view requested");
        try {
            groupView = ViewHelper.getView(ViewPath.GROUP.getPath());
        } catch (Exception e) {
            logger.error("Could not load group view: {}", e.getMessage());
        }
        return groupView;
    }

    public AnchorPane getStatisticView() {
        logger.info("New Statistic view requested");
        try {
            statisticView = ViewHelper.getView(ViewPath.STATISTIC.getPath());
        } catch (Exception e) {
            logger.error("Could not load statistic view: {}", e.getMessage());
        }
        return statisticView;
    }

    public AnchorPane getSpamView() {
        logger.info("Spam view requested");
        try {
            spamView = ViewHelper.getView(ViewPath.SPAM.getPath());
        } catch (Exception e) {
            logger.error("Could not load spam view: {}", e.getMessage());
        }
        return spamView;
    }

    public AnchorPane getGroupDetailsView(long groupId, String groupName) {
        try {
            groupDetailsLoader = new FXMLLoader(Client.class.getResource(ViewPath.GROUP_DETAILS.getPath()));
            groupDetailView = groupDetailsLoader.load();
            logger.info("GroupDetailController initialized for group {}, {}", groupId, groupName);
            GroupDetailsController controller = groupDetailsLoader.getController();
            if (controller != null) {
                controller.setGroupId(groupId);
                controller.setGroupName(groupName);
                logger.info("Updated GroupDetailController");
            } else {
                logger.error("Could not retrieve GroupDetailController from existing view.");
            }
        } catch (Exception e) {
            logger.error("Could not load group detail view: {}", e.getMessage());
        }
        return groupDetailView;
    }

    public AnchorPane getUserDetailsView(long userId, String userName) {
        try {
            userDetailsLoader = new FXMLLoader(Client.class.getResource(ViewPath.USER_DETAILS.getPath()));
            userDetailsView = userDetailsLoader.load();
            logger.info("UserDetailsController initialized for user {}, {}", userId, userName);
            UserDetailsController controller = userDetailsLoader.getController();
            if (controller != null) {
                controller.setUserId(userId);
                controller.setUserName(userName);
                logger.info("Updated UserDetailsController");
            } else {
                logger.error("Could not retrieve UserDetailsController from existing view.");
            }
        } catch (Exception e) {
            logger.error("Could not load user detail view: {}", e.getMessage());
        }
        return userDetailsView;
    }

    public AnchorPane getUserRelationView(long userId, String userName) {
        logger.info("User Activity view requested");
        try {
            userRelationLoader = new FXMLLoader(Client.class.getResource(ViewPath.USER_RELATION.getPath()));
            userRelationView = userRelationLoader.load();
            UserRelationController controller = userRelationLoader.getController();
            if (controller != null) {
                controller.setUserId(userId);
                controller.setUsername(userName);
                logger.info("Updated UserActivityController");
            } else {
                logger.error("Could not retrieve UserActivityController from existing view.");
            }
        } catch (Exception e) {
            logger.error("Could not load user activity view: {}", e.getMessage());
        }
        return userRelationView;
    }

    public AnchorPane gerUserActivityView() {
        logger.info("User Activity view requested");
        try {
            userActivityView = ViewHelper.getView(ViewPath.USER_ACTIVITY.getPath());
        } catch (Exception e) {
            logger.error("Could not load user activity view: {}", e.getMessage());
        }
        return userActivityView;
    }

    public AnchorPane getUserLoginView() {
        logger.info("User Login view requested");
        try {
            userLoginView = ViewHelper.getView(ViewPath.USER_LOGIN.getPath());
        } catch (Exception e) {
            logger.error("Could not load user login view: {}", e.getMessage());
        }
        return userLoginView;
    }

    public AnchorPane getNewUserView() {
        logger.info("New User view requested");
        try {
            newUserView = ViewHelper.getView(ViewPath.NEW_USER.getPath());
        } catch (Exception e) {
            logger.error("Could not load new user view: {}", e.getMessage());
        }
        return newUserView;
    }

    public AnchorPane getCreateUserView() {
        logger.info("Create User view requested");
        try {
            createUserView = ViewHelper.getView(ViewPath.CREATE_USER.getPath());
        } catch (Exception e) {
            logger.error("Could not load create user view: {}", e.getMessage());
        }
        return createUserView;
    }
}
