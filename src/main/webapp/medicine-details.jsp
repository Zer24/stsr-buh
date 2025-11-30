<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>${medicine.name}</title>
    <c:url var="cssUrl" value="/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
    <h1>${medicine.name}</h1>

    <div>
        <strong>ID:</strong> ${medicine.id}<br>
        <strong>Описание:</strong> ${medicine.description}<br>
        <strong>Форма выпуска:</strong> ${medicine.dosageForm}<br>
        <strong>Цена:</strong> ${medicine.price} руб.<br>
        <strong>Количество на складе:</strong> ${medicine.quantityInStock} шт.<br>
        <strong>Рецептурный:</strong> ${medicine.requiresPrescription ? 'Да' : 'Нет'}<br>
    </div>

    <p>
        <a href="medicine">Назад к списку</a>
    </p>
</body>
</html>