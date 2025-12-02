<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Список лекарств</title>
    <c:url var="cssUrl" value="/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
    <h1>💊 Список лекарств</h1>

    <table>
        <tr>
            <th>ID</th>
            <th>Название</th>
            <th>Форма выпуска</th>
            <th>Цена</th>
            <th>Количество</th>
            <th>Рецептурный</th>
            <th>Действия</th>
        </tr>

        <%-- items="${medicines}" - это список из request.setAttribute() --%>
        <c:forEach var="med" items="${medicines}">
            <tr>
                <td>${med.id}</td>
                <td>
                    <a href="medicine?id=${med.id}">${med.name}</a>
                </td>
                <td>${med.dosageForm}</td>
                <td>${med.price} руб.</td>
                <td class="${med.quantityInStock < 10 ? 'low-stock' : ''}">
                    ${med.quantityInStock} шт.
                </td>
                <td>${med.requiresPrescription ? 'Да' : 'Нет'}</td>
                <td>
                    <button class="btn-danger" onclick="confirmDelete(${med.id},'${med.name}')">Удалить</button>
                    <button class="btn-primary" onclick="window.location.href='medicine?action=edit&id=${med.id}'">Изменить</button>
                </td>
            </tr>
        </c:forEach>
    </table>

    <div>
        <a href="index.jsp">На главную</a> |
        <a href="medicine?action=add">Добавить препарат</a>
    </div>

    <script>
        function confirmDelete(medicineId, medicineName) {
            if (confirm('Вы уверены, что хотите удалить лекарство '+ medicineName + ' (ID: '+medicineId+')?')) {
                fetch('medicine?id=' + medicineId, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    }
                }).then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Ошибка при удалении лекарства');
                    }
                }).catch(error => {
                    console.error('Ошибка:', error);
                        alert('Ошибка при удалении лекарства');
                    }
                );
            }
        }
        </script>
</body>
</html>