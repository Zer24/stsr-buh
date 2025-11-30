package Main.servlet;

import Main.NotFoundException;
import Main.ServletHelper;
import Main.domain.User;
import Main.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    UserService userService;
    ObjectMapper mapper;
    @Override
    public void init() {
        this.userService = new UserService();
        mapper = new ObjectMapper();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            if (Objects.equals(request.getParameter("format"), "json")) {
                sendJSON(request, response);
            } else {
                sendJSP(request, response);
            }
        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        }
    }
    protected void sendJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = ServletHelper.start(request, response);
        String idParam = request.getParameter("id");
        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                User user = userService.getUserById(id);
                out.println(mapper.writeValueAsString(user));
            } else {
                out.println(mapper.writeValueAsString(userService.getAllUsers()));
            }
        } catch (NumberFormatException e) {
            response.sendError(400, "Ошибка: ID должен быть числом!");
        } catch (NotFoundException e) {
            response.sendError(404, e.getMessage());
        }
        out.close();
    }
    protected void sendJSP(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String idParam = request.getParameter("id");
        if(idParam!=null){
            int id =Integer.parseInt(idParam);
            User user = userService.getUserById(id);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/user-details.jsp").forward(request, response);
        }else{
            List<User> users = userService.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/user-list.jsp").forward(request, response);
        }
    }
}