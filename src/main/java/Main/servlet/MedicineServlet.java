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
import java.net.URLEncoder;
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
            String action = request.getParameter("action");
            String idParam = request.getParameter("id");

            if(action==null){
                if (Objects.equals(request.getParameter("format"), "json")) {
                    PrintWriter out = ServletHelper.start(request, response);
                    try {
                        if (idParam == null) {
                            out.println(mapper.writeValueAsString(medicineService.getAllMedicines()));
                        } else {
                            int id = Integer.parseInt(idParam);
                            Medicine medicine = medicineService.getMedicineById(id);
                            out.println(mapper.writeValueAsString(medicine));
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(400);
                        out.println("Ошибка: ID должен быть числом!");
                    } catch (NotFoundException e) {
                        response.setStatus(404);
                        out.println(e.getMessage());
                    }
                    out.close();
                } else {
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
                return;
            }

            switch (action){
                default -> request.getRequestDispatcher("/medicine-list.jsp").forward(request, response);
                case "edit" -> {
                    if (idParam != null && !idParam.trim().isEmpty()) {
                        Integer id = Integer.parseInt(idParam);
                        Medicine medicine = medicineService.getMedicineById(id);

                        if (medicine == null) {
                            response.sendRedirect("medicine?format=html&error=Препарат с ID=" + id + " не найден");
                        }
                        request.setAttribute("medicine", medicine);

                        request.getRequestDispatcher("/medicine-change.jsp").forward(request, response);
                    } else {
                        response.sendRedirect("medicine?format=html&error=Не указан ID препарата");
                    }
                }
                case "add" -> request.getRequestDispatcher("/add-medicine.jsp").forward(request, response);
            }
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Определяем действие: добавление или обновление
            String action = request.getParameter("action");
            String idParam = request.getParameter("id");

            if (idParam == null) {
                System.out.println("Получен запрос на добавление без id");
                return;
            }

            // Получаем данные из формы
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String dosageForm = request.getParameter("dosageForm");
            String quantityStr = request.getParameter("quantityInStock");
            String requiresPrescription = request.getParameter("requiresPrescription");

            // Валидация общих полей
            if (name == null || name.trim().isEmpty()) {
                response.sendError(400, "Название обязательно для заполнения");
                return;
            }

            if (priceStr == null || priceStr.trim().isEmpty()) {
                response.sendError(400, "Цена обязательна для заполнения");
                return;
            }

            try {
                BigDecimal price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    response.sendError(400, "Цена не может быть отрицательной");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(400, "Некорректный формат цены");
                return;
            }

            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                response.sendError(400, "medicine?format=html&error=Количество обязательно для заполнения");
                return;
            }

            // Проверяем, это обновление существующего препарата
            if ("update".equals(action) && idParam != null && !idParam.trim().isEmpty()) {
                // ОБНОВЛЕНИЕ существующего препарата
                Integer id = Integer.parseInt(idParam);

                // Создаём объект Medicine с ID
                Medicine medicine = new Medicine();
                medicine.setId(id);
                medicine.setName(name.trim());
                medicine.setDescription(description != null ? description.trim() : "");
                medicine.setPrice(new BigDecimal(priceStr));
                medicine.setDosageForm(dosageForm != null ? dosageForm.trim() : "");
                medicine.setQuantityInStock(Integer.parseInt(quantityStr));
                medicine.setRequiresPrescription(requiresPrescription != null && requiresPrescription.equals("on"));

                medicineService.updateMedicine(medicine);
            } else {
                // ДОБАВЛЕНИЕ нового препарата (оригинальная логика)
                Medicine medicine = new Medicine();
                medicine.setName(name.trim());
                medicine.setDescription(description != null ? description.trim() : "");
                medicine.setPrice(new BigDecimal(priceStr));
                medicine.setDosageForm(dosageForm != null ? dosageForm.trim() : "");
                medicine.setQuantityInStock(Integer.parseInt(quantityStr));
                medicine.setRequiresPrescription(requiresPrescription != null && requiresPrescription.equals("on"));

                // Сохраняем через сервис
                medicineService.addMedicine(medicine);

                // Перенаправляем на список с сообщением об успехе
                response.sendRedirect("medicine?format=html&success=1");
            }

        } catch (Exception e) {
            try {
                e.printStackTrace();
                // Определяем, куда перенаправить в случае ошибки
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.trim().isEmpty()) {
                    // Ошибка при редактировании - возвращаем на страницу редактирования
                    response.sendRedirect("medicine?format=html&action=edit&id=" + idParam +
                            "&error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
                } else {
                    // Ошибка при добавлении - возвращаем на страницу добавления
                    // Здесь можно сохранить введенные данные для повторного заполнения формы
                    String referer = request.getHeader("Referer");
                    if (referer != null && referer.contains("add")) {
                        response.sendRedirect("medicine?format=html&action=add&error=" +
                                URLEncoder.encode(e.getMessage(), "UTF-8"));
                    } else {
                        response.sendRedirect("medicine?format=html&error=" +
                                URLEncoder.encode(e.getMessage(), "UTF-8"));
                    }
                }
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
}