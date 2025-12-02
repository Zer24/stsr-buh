<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Новая продажа</title>
    <c:url var="cssUrl" value="/css/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
    <style>
        .form-container {
            max-width: 600px;
            margin: 20px auto;
            padding: 20px;
            background: white;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #2c3e50;
        }
        .form-input, .form-select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 3px;
            box-sizing: border-box;
        }
    </style>
    <script>
        function calculateTotal() {
            const price = parseFloat(document.getElementById('unitPrice').value) || 0;
            const quantity = parseInt(document.getElementById('quantity').value) || 0;
            const total = price * quantity;
            document.getElementById('totalAmount').value = total.toFixed(2);
        }

        function updatePrice() {
            const medicineId = document.getElementById('medicineId').value;
            // Здесь можно добавить AJAX запрос для получения цены лекарства
            // document.getElementById('unitPrice').value = полученная цена;
            calculateTotal();
        }
    </script>
</head>
<body>
    <div class="header">
        <h1>💰 Новая продажа</h1>
        <div class="nav">
            <a href="sale">Назад к списку</a>
        </div>
    </div>

    <div class="form-container">
        <c:if test="${not empty error}">
            <div class="error-message">
                ❌ ${error}
            </div>
        </c:if>

        <form action="sale" method="post">
            <div class="form-group">
                <label class="form-label" for="clientId">Клиент *</label>
                <input type="number" id="clientId" name="clientId" class="form-input"
                                                   min="1" value="${param.clientId}">
            </div>
            <div class="form-group">
                <label class="form-label" for="pharmacistId">Продавец *</label>
                <input type="number" id="pharmacistId" name="pharmacistId" class="form-input"
                                                   min="1" value="${param.pharmacistId}">
            </div>
            <div class="form-group">
                <label class="form-label" for="medicineId">Препарат *</label>
                <input type="number" id="medicineId" name="medicineId" class="form-input"
                                                   min="1" value="${param.medicineId}">
            </div>

            <div class="form-group">
                <label class="form-label" for="quantity">Количество *</label>
                <input type="number" id="quantity" name="quantity" class="form-input"
                       min="1" value="${param.quantity}" oninput="calculateTotal()" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="unitPrice">Цена за единицу (руб.)</label>
                <input type="number" id="unitPrice" name="unitPrice" class="form-input"
                       step="0.01" min="0" value="${param.unitPrice}" oninput="calculateTotal()">
            </div>

            <div class="form-group">
                <label class="form-label" for="totalAmount">Общая сумма (руб.)</label>
                <input type="number" id="totalAmount" name="totalAmount" class="form-input"
                       step="0.01" min="0" value="${param.totalAmount}" readonly>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-success">Оформить продажу</button>
                <a href="sale" class="btn btn-primary">Отмена</a>
            </div>
        </form>
    </div>
</body>
</html>