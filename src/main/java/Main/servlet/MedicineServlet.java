package Main.servlet;

import Main.NotFoundException;
import Main.ServletHelper;
import Main.domain.Medicine;
import Main.service.MedicineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Objects;

@WebServlet("/medicine")
public class MedicineServlet extends HttpServlet {
    MedicineService medicineService;
    ObjectMapper mapper;
    //вызовется один раз при запуске
    @Override
    public void init() {
        this.medicineService = new MedicineService();
        mapper = new ObjectMapper();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            if(Objects.equals(request.getParameter("format"), "json")){
                sendJson(request, response);
            }else{
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
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String dosageForm = request.getParameter("dosageForm");
            String quantityStr = request.getParameter("quantityInStock");
            String requiresPrescription = request.getParameter("requiresPrescription");

            // Создаём объект Medicine
            Medicine medicine = new Medicine();
            medicine.setName(name.trim());
            medicine.setDescription(description != null ? description.trim() : "");
            medicine.setPrice(new BigDecimal(priceStr));
            medicine.setDosageForm(dosageForm != null ? dosageForm.trim() : "");
            medicine.setQuantityInStock(Integer.parseInt(quantityStr));
            medicine.setRequiresPrescription(requiresPrescription!=null && requiresPrescription.equals("on"));

            // Сохраняем через сервис
            medicineService.addMedicine(medicine);

            // Перенаправляем на список с сообщением об успехе
            response.sendRedirect("medicine?format=html&success=1");
        } catch (Exception e) {
            try {
                e.printStackTrace();
                response.sendError(500, e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String idParam = request.getParameter("id");
            System.out.println("DELETING "+idParam);
            if (idParam == null) {
                System.out.println("Получен запрос на удаление без id");
                return;
            }

            int id = Integer.parseInt(idParam);
            medicineService.deleteMedicine(id);

            // Перенаправляем обратно на список с сообщением об успехе
            response.sendRedirect("medicine?format=html&deleted=1");

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
                response.sendError(500, "Ошибка при удалении: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    protected void sendJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = ServletHelper.start(request, response);
        String idParam = request.getParameter("id");
        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Medicine medicine = medicineService.getMedicineById(id);
                out.println(mapper.writeValueAsString(medicine));
            } else {
                out.println(mapper.writeValueAsString(medicineService.getAllMedicines()));
            }
        } catch (NumberFormatException e) {
            response.setStatus(400);
            out.println("Ошибка: ID должен быть числом!");
        } catch (NotFoundException e) {
            response.setStatus(404);
            out.println(e.getMessage());
        }
        out.close();
    }
    protected void sendJSP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Medicine medicine = medicineService.getMedicineById(id);
            request.setAttribute("medicine", medicine);
            request.getRequestDispatcher("/medicine-details.jsp").forward(request, response);
        } else {
            request.setAttribute("medicines", medicineService.getAllMedicines());
            request.getRequestDispatcher("/medicine-list.jsp").forward(request, response);
        }
    }
}