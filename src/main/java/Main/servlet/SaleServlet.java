package Main.servlet;

import Main.NotFoundException;
import Main.ServletHelper;
import Main.domain.Sale;
import Main.domain.User;
import Main.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@WebServlet("/sale")
public class SaleServlet extends HttpServlet {
    private ISaleService saleService;
    private IUserService userService;
    private IMedicineService medicineService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        IServiceFactory serviceFactory = new ServiceFactory();
        this.saleService = serviceFactory.createSaleService();
        this.userService = serviceFactory.createUserService();
        this.medicineService = serviceFactory.createMedicineService();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    protected void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("clients", userService.getAllUsers());
            request.setAttribute("medicines", medicineService.getAllMedicines());
            request.getRequestDispatcher("/sale-form.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Ошибка при загрузке данных: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            User user = (User)session.getAttribute("sessionUser");

            if(user==null || Objects.equals(user.getRole(), "user")){
                response.sendError(401, "Для доступа к этой странице необходима авторизация");
                return;
            }

            String action = request.getParameter("action");
            if ("searchClients".equals(action)) {
                searchClients(request, response);
                return;
            } else if ("searchMedicines".equals(action)) {
                searchMedicines(request, response);
                return;
            } else if ("getMedicinePrice".equals(action)) {
                getMedicinePrice(request, response);
                return;
            }

            if (Objects.equals(request.getParameter("format"), "json")) {
                sendJson(request, response);
            } else {
                sendJSP(request, response);
            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
                response.sendError(500, "Ошибка: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            User user = (User)session.getAttribute("sessionUser");

            if(user==null || Objects.equals(user.getRole(), "user")){
                response.sendError(401, "Для доступа к этой странице необходима авторизация");
                return;
            }

            String clientIdStr = request.getParameter("clientId");
            String pharmacistIdStr = request.getParameter("pharmacistId");
            String medicineIdStr = request.getParameter("medicineId");
            String quantityStr = request.getParameter("quantity");
            String totalAmountStr = request.getParameter("totalAmount");

            if (clientIdStr == null || clientIdStr.trim().isEmpty()) {
                throw new RuntimeException("Не выбран клиент");
            }
            if (medicineIdStr == null || medicineIdStr.trim().isEmpty()) {
                throw new RuntimeException("Не выбран препарат");
            }

            // Создаём объект Sale
            Sale sale = new Sale();
            sale.setClientId(Integer.parseInt(clientIdStr));
            sale.setPharmacistId(Integer.parseInt(pharmacistIdStr));
            sale.setMedicineId(Integer.parseInt(medicineIdStr));
            sale.setQuantity(Integer.parseInt(quantityStr));
            sale.setTotalAmount(new BigDecimal(totalAmountStr));
            sale.setSaleDateTime(LocalDateTime.now());

            saleService.addSale(sale);

            response.sendRedirect("sale?format=html&success=1");

        } catch (NumberFormatException e) {
            try {
                response.sendError(400, "Некорректный формат числа: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
                response.sendError(500, "Ошибка при создании продажи: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // Метод для поиска клиентов
    private void searchClients(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchTerm = request.getParameter("term");
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                out.println(mapper.writeValueAsString(userService.getAllUsers()));
            } else {
                out.println(mapper.writeValueAsString(userService.getClientsByName(searchTerm)));
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.println("{\"error\":\"Ошибка при поиске клиентов: " + e.getMessage() + "\"}");
        }
    }

    // Метод для поиска препаратов
    private void searchMedicines(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchTerm = request.getParameter("term");
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                out.println(mapper.writeValueAsString(medicineService.getAllMedicines()));
            } else {
                out.println(mapper.writeValueAsString(medicineService.getMedicinesByName(searchTerm)));
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.println("{\"error\":\"Ошибка при поиске препаратов: " + e.getMessage() + "\"}");
        }
    }

    // Метод для получения цены препарата
    private void getMedicinePrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String medicineIdStr = request.getParameter("id");
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (medicineIdStr == null || medicineIdStr.trim().isEmpty()) {
                response.setStatus(400);
                out.println("{\"error\":\"Не указан ID препарата\"}");
                return;
            }

            int medicineId = Integer.parseInt(medicineIdStr);
            Main.domain.Medicine medicine = medicineService.getMedicineById(medicineId);

            out.println("{\"price\":\"" + medicine.getPrice() + "\", \"quantity\":\"" + medicine.getQuantityInStock() + "\"}");
        } catch (Exception e) {
            response.setStatus(500);
            out.println("{\"error\":\"Ошибка при получении цены: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = (User)request.getAttribute("sessionUser");

            if(user==null || Objects.equals(user.getRole(), "user")){
                response.sendError(401, "Для доступа к этой странице необходима авторизация");
                return;
            }
            String idParam = request.getParameter("id");
            System.out.println("DELETING SALE " + idParam);

            if (idParam == null) {
                System.out.println("Получен запрос на удаление продажи без id");
                return;
            }

            int id = Integer.parseInt(idParam);
            saleService.deleteSale(id);

            // Возвращаем успешный статус для AJAX или перенаправляем
            String format = request.getParameter("format");
            if ("html".equals(format)) {
                response.sendRedirect("sale?format=html&deleted=1");
            } else {
                response.setStatus(200);
                response.getWriter().write("{\"message\":\"Продажа удалена успешно\"}");
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
                response.sendError(500, "Ошибка при удалении продажи: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

//    @Override
//    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            // Парсим данные из request body для обновления продажи
//            Sale sale = mapper.readValue(request.getReader(), Sale.class);
//            saleService.updateSale(sale);
//
//            response.setStatus(200);
//            response.getWriter().write("{\"message\":\"Продажа обновлена успешно\"}");
//
//        } catch (NotFoundException e) {
//            try {
//                response.sendError(404, e.getMessage());
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        } catch (Exception e) {
//            try {
//                response.sendError(500, "Ошибка при обновлении продажи: " + e.getMessage());
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
                Sale sale = saleService.getSaleById(id);
                out.println(mapper.writeValueAsString(sale));
            }else{
                out.println(mapper.writeValueAsString(saleService.getAllSales()));
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
                Sale sale = saleService.getSaleById(id);
                request.setAttribute("sale", sale);
                request.getRequestDispatcher("/sale-details.jsp").forward(request, response);
            } else {
                // Получаем все продажи с дополнительной информацией
                request.setAttribute("sales", saleService.getAllSales());
                request.getRequestDispatcher("/sale-list.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Некорректный ID продажи");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (NotFoundException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}