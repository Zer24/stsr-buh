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
import java.util.Objects;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        this.userService = new UserService();
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (Objects.equals(request.getParameter("format"), "json")) {
                sendJson(request, response);
            } else {
                sendJSP(request, response);
            }
        } catch (Exception e) {
            try {
                response.sendError(500, "Ошибка: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Получаем данные из формы
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String role = request.getParameter("role");

            // Валидация
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Пароли не совпадают");
                request.getRequestDispatcher("/user-add.jsp").forward(request, response);
                return;
            }

            if (password.length() < 6) {
                request.setAttribute("error", "Пароль должен содержать минимум 6 символов");
                request.getRequestDispatcher("/user-add.jsp").forward(request, response);
                return;
            }

            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.trim());
            user.setPassword(password);
            user.setRole(role);

            // Сохраняем через сервис
            userService.addUser(user);

            // Перенаправляем на список с сообщением об успехе
            response.sendRedirect("user?format=html&success=1");

        } catch (Exception e) {
            try {
                e.printStackTrace();
                request.setAttribute("error", "Ошибка при создании пользователя: " + e.getMessage());
                request.getRequestDispatcher("/user-add.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String idParam = request.getParameter("id");
            System.out.println("DELETING USER " + idParam);

            if (idParam == null) {
                System.out.println("Получен запрос на удаление пользователя без id");
                return;
            }

            int id = Integer.parseInt(idParam);
            User currentUser = (User) request.getSession().getAttribute("user");

            // Проверяем, не пытается ли пользователь удалить сам себя
            if (currentUser != null && currentUser.getId() == id) {
                response.sendError(400, "Нельзя удалить самого себя");
                return;
            }

            userService.deleteUser(id);

            // Возвращаем успешный статус
            String format = request.getParameter("format");
            if ("html".equals(format)) {
                response.sendRedirect("user?format=html&deleted=1");
            } else {
                response.setStatus(200);
                response.getWriter().write("{\"message\":\"Пользователь удален успешно\"}");
            }

        } catch (NumberFormatException e) {
            try {
                response.sendError(400, "ID должен быть числом");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (NotFoundException e) {
            try {
                response.sendError(404, e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            try {
                response.sendError(500, "Ошибка при удалении пользователя: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

//    @Override
//    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            // Для обновления пользователя
//            User user = mapper.readValue(request.getReader(), User.class);
//
//            // Если пароль не указан, сохраняем старый
//            if (user.getPassword() == null || user.getPassword().isEmpty()) {
//                User existingUser = userService.getUserById(user.getId());
//                user.setPassword(existingUser.getPassword());
//            }
//
//            userService.updateUser(user);
//
//            response.setStatus(200);
//            response.getWriter().write("{\"message\":\"Пользователь обновлен успешно\"}");
//
//        } catch (NotFoundException e) {
//            try {
//                response.sendError(404, e.getMessage());
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        } catch (Exception e) {
//            try {
//                response.sendError(500, "Ошибка при обновлении пользователя: " + e.getMessage());
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
//    }

    protected void sendJson(HttpServletRequest request, HttpServletResponse response)
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
            response.setStatus(400);
            out.println("{\"error\":\"ID должен быть числом!\"}");
        } catch (NotFoundException e) {
            response.setStatus(404);
            out.println("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(500);
            out.println("{\"error\":\"Ошибка сервера: " + e.getMessage() + "\"}");
        }

        out.close();
    }

    protected void sendJSP(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                User user = userService.getUserById(id);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/user-details.jsp").forward(request, response);
            } else {
                request.setAttribute("users", userService.getAllUsers());
                request.getRequestDispatcher("/user-list.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Некорректный ID пользователя");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (NotFoundException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}