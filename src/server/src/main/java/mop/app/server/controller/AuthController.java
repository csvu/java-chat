package mop.app.server.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import mop.app.server.dao.AuthDAO;
import mop.app.server.dto.Response;
import mop.app.server.dto.UserDTO;
import mop.app.server.util.ObjectMapperConfig;

public class AuthController {
    private final AuthDAO authDAO;

    public AuthController() {
        this.authDAO = new AuthDAO();
    }

    public Response login(Object data) {
        UserDTO loginData = ObjectMapperConfig.getObjectMapper().convertValue(data, UserDTO.class);

        UserDTO user = authDAO.login(loginData.getEmail(), loginData.getPassword());

        if (user == null) {
            return new Response(false, "Invalid email or password");
        }

        return new Response(true, user);
    }

}
