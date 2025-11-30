<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Добавить лекарство</title>
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
        .form-input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 3px;
            box-sizing: border-box;
        }
        .form-textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 3px;
            resize: vertical;
            min-height: 80px;
        }
        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>💊 Добавить новое лекарство</h1>
        <div class="nav">
            <a href="medicine">Назад к списку</a>
        </div>
    </div>

    <div class="form-container">
        <%-- Сообщение об ошибке --%>
        <c:if test="${not empty error}">
            <div class="error-message">
                ❌ ${error}
            </div>
        </c:if>

        <%-- Форма добавления --%>
        <form action="medicine" method="post">

            <div class="form-group">
                <label class="form-label" for="name">Название лекарства *</label>
                <input type="text" id="name" name="name" class="form-input"
                       value="${param.name}" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="description">Описание</label>
                <textarea id="description" name="description" class="form-textarea"
                         placeholder="Описание препарата...">${param.description}</textarea>
            </div>

            <div class="form-group">
                <label class="form-label" for="price">Цена (руб.) *</label>
                <input type="number" id="price" name="price" class="form-input"
                       step="0.01" min="0" value="${param.price}" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="dosageForm">Форма выпуска</label>
                <input type="text" id="dosageForm" name="dosageForm" class="form-input"
                       value="${param.dosageForm}" placeholder="Таблетки, сироп, мазь...">
            </div>

            <div class="form-group">
                <label class="form-label" for="quantityInStock">Количество на складе *</label>
                <input type="number" id="quantityInStock" name="quantityInStock"
                       class="form-input" min="0" value="${param.quantityInStock}" required>
            </div>

            <div class="form-group">
                <div class="checkbox-group">
                    <input type="checkbox" id="requiresPrescription" name="requiresPrescription"
                           ${param.requiresPrescription ? 'checked' : ''}>
                    <label class="form-label" for="requiresPrescription">
                        Рецептурный препарат
                    </label>
                </div>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-success">Добавить лекарство</button>
                <a href="medicine" class="btn btn-primary">Отмена</a>
            </div>
        </form>
    </div>
</body>
</html>