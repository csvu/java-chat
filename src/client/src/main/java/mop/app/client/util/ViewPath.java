package mop.app.client.util;

import java.io.File;
import lombok.Getter;

@Getter
public enum ViewPath {
    INDEX(String.format("%s/index.fxml", Constant.viewFolder)),
    LOGIN(String.format("%s/auth/login.fxml", Constant.viewFolder)),
    REGISTER(String.format("%s/auth/register.fxml", Constant.viewFolder)),
    FORGOT_PASSWORD(String.format("%s/auth/forgot-password.fxml", Constant.viewFolder)),
    RESET_PASSWORD(String.format("%s/auth/reset-password.fxml", Constant.viewFolder)),

    // ADMIN
    ADMIN(String.format("%s/admin/admin.fxml", Constant.viewFolder)),
    DASHBOARD(String.format("%s/admin/dashboard.fxml", Constant.viewFolder)),
    USER(String.format("%s/admin/user.fxml", Constant.viewFolder)),
    CREATE_USER(String.format("%s/admin/create-user.fxml", Constant.viewFolder)),
    NEW_USER(String.format("%s/admin/new-user.fxml", Constant.viewFolder)),
    USER_LOGIN(String.format("%s/admin/user-login.fxml", Constant.viewFolder)),
    USER_DETAILS(String.format("%s/admin/user-details.fxml", Constant.viewFolder)),
    USER_ACTIVITY(String.format("%s/admin/user-activity.fxml", Constant.viewFolder)),
    GROUP(String.format("%s/admin/group.fxml", Constant.viewFolder)),
    GROUP_DETAILS(String.format("%s/admin/group-details.fxml", Constant.viewFolder)),
    STATISTIC(String.format("%s/admin/statistic.fxml", Constant.viewFolder)),
    SPAM(String.format("%s/admin/spam.fxml", Constant.viewFolder)),
    ;

    private final String path;

    ViewPath(String path) {
        this.path = path;
    }
}
