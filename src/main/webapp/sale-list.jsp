<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <title>Список продаж</title>
    <c:url var="cssUrl" value="/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
    <h1>💰 Список продаж</h1>

    <table>
        <tr>
            <th>ID</th>
            <th>Дата и время</th>
            <th>Клиент</th>
            <th>Фармацевт</th>
            <th>Лекарство</th>
            <th>Количество</th>
            <th>Сумма</th>
            <th>Действия</th>
        </tr>

        <c:forEach var="sale" items="${sales}">
            <tr>
                <td>${sale.id}</td>
                <td>
                    <fmt:formatDate value="${sale.saleDateForJSP}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
                <td>
                    <a href="user?id=${sale.clientId}">${sale.clientName}</a>
                </td>
                <td>${sale.pharmacistName}</td>
                <td>
                    <a href="medicine?id=${sale.medicineId}">${sale.medicineName}</a>
                </td>
                <td>${sale.quantity} шт.</td>
                <td>${sale.totalAmount} руб.</td>
                <td>
                    <button class="btn-danger" onclick="confirmDelete(${sale.id})">Удалить</button>
                </td>
            </tr>
        </c:forEach>
    </table>

    <div>
        <a href="index.jsp">На главную</a> |
        <a href="sale-add.jsp">Новая продажа</a>
    </div>

    <script>
        function confirmDelete(saleId) {
            if (confirm('Вы уверены, что хотите удалить продажу #' + saleId + '?')) {
                fetch('sale?id=' + saleId, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    }
                }).then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Ошибка при удалении продажи');
                    }
                }).catch(error => {
                    console.error('Ошибка:', error);
                    alert('Ошибка при удалении продажи');
                });
            }
        }
    </script>
</body>
</html>