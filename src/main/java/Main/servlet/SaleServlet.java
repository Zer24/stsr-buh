package Main.servlet;

import Main.NotFoundException;
import Main.ServletHelper;
import Main.domain.Sale;
import Main.service.SaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@WebServlet("/sale")
public class SaleServlet extends HttpServlet {
    SaleService saleService;
    ObjectMapper mapper;

    //вызовется один раз при запуске
    @Override
    public void init() {
        this.saleService = new SaleService();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            if(Objects.equals(request.getParameter("format"), "json")){
                sendJSON(request, response);
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
    protected void sendJSON(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = ServletHelper.start(request, response);
        String idParam = request.getParameter("id");
        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Sale sale = saleService.getSaleById(id);
                out.println(mapper.writeValueAsString(sale));
            } else {
                out.println(mapper.writeValueAsString(saleService.getAllSales()));
            }
        } catch (NumberFormatException e) {
            response.setStatus(400);
            out.println("Ошибка: ID должен быть числом!");
        } catch (NotFoundException e) {
            response.setStatus(404);
            out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatus(500);
            out.println("Ошибка сервера: " + e.getMessage() + "");
        }
        out.close();
    }
    protected void sendJSP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Sale sale = saleService.getSaleById(id);
            request.setAttribute("sale", sale);
            request.getRequestDispatcher("/sale-details.jsp").forward(request, response);
        } else {
            request.setAttribute("sales", saleService.getAllSales());
            request.getRequestDispatcher("/sale-list.jsp").forward(request, response);
        }
    }
}