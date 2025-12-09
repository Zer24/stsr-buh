package Main.servlet;

import Main.domain.User;
import Main.service.IUserService;
import Main.service.ServiceFactory;
import Main.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Objects;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private IUserService userService;

    @Override
    public void init() {
        this.userService = new ServiceFactory().createUserService();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("login doGet()");
        String action = request.getParameter("action");
        if(action==null || action.equals("login")) {
            response.sendRedirect("login.jsp");
        }else{
            HttpSession session = request.getSession();
            session.removeAttribute("sessionUser");
            response.sendRedirect("index.jsp");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println("login doPost() "+email+" "+password);

        User user = userService.getUserByEmail(email);
        if (!Objects.equals(user.getPassword(), password)) {
            response.sendError(400, "Пароль не совпадает");
            return;
        }
        HttpSession session = request.getSession();
        session.setAttribute("sessionUser", user);
        session.setMaxInactiveInterval(30 * 60);

        response.sendRedirect("index.jsp");
    }
}
