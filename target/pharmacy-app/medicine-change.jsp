<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Редактировать лекарство</title>
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
        .med-info {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #007bff;
        }
        .med-info p {
            margin: 5px 0;
        }
        .med-id {
            font-weight: bold;
            color: #007bff;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Редактировать лекарство</h1>
        <div class="nav">
            <a href="medicine">Назад к списку</a>
        </div>
    </div>

    <div class="form-container">
        <%-- Информация о редактируемом препарате --%>
        <div class="med-info">
            <p>ID препарата: <span class="med-id">${medicine.id}</span></p>
            <p>Текущее название: <strong>${medicine.name}</strong></p>
        </div>

        <%-- Сообщение об ошибке --%>
        <c:if test="${not empty error}">
            <div class="error-message">
                ❌ ${error}
            </div>
        </c:if>

        <%-- Сообщение об успехе --%>
        <c:if test="${not empty success}">
            <div class="success-message">
                ✅ ${success}
            </div>
        </c:if>

        <%-- Форма редактирования --%>
        <form action="medicine" method="post">
            <%-- Скрытое поле для передачи ID --%>
            <input type="hidden" name="id" value="${medicine.id}">
            <%-- Скрытое поле для указания действия --%>
            <input type="hidden" name="action" value="update">

            <div class="form-group">
                <label class="form-label" for="name">Название лекарства *</label>
                <input type="text" id="name" name="name" class="form-input"
                       value="${medicine.name}" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="description">Описание</label>
                <textarea id="description" name="description" class="form-textarea"
                         placeholder="Описание препарата...">${medicine.description}</textarea>
            </div>

            <div class="form-group">
                <label class="form-label" for="price">Цена (руб.) *</label>
                <input type="number" id="price" name="price" class="form-input"
                       step="0.01" min="0" value="${medicine.price}" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="dosageForm">Форма выпуска</label>
                <input type="text" id="dosageForm" name="dosageForm" class="form-input"
                       value="${medicine.dosageForm}" placeholder="Таблетки, сироп, мазь...">
            </div>

            <div class="form-group">
                <label class="form-label" for="quantityInStock">Количество на складе *</label>
                <input type="number" id="quantityInStock" name="quantityInStock"
                       class="form-input" min="0" value="${medicine.quantityInStock}" required>
            </div>

            <div class="form-group">
                <div class="checkbox-group">
                    <input type="checkbox" id="requiresPrescription" name="requiresPrescription"
                           ${medicine.requiresPrescription ? 'checked' : ''}>
                    <label class="form-label" for="requiresPrescription">
                        Рецептурный препарат
                    </label>
                </div>
            </div>

            <div class="form-group" style="display: flex; gap: 10px;">
                <button type="submit" class="btn btn-success">Сохранить изменения</button>
                <a href="medicine" class="btn btn-primary">Отмена</a>
        </form>
    </div>

    <script>
        // Валидация формы
        document.querySelector('form').addEventListener('submit', function(e) {
            const price = document.getElementById('price').value;
            const quantity = document.getElementById('quantityInStock').value;

            if (parseFloat(price) < 0) {
                alert('Цена не может быть отрицательной!');
                e.preventDefault();
                return false;
            }

            if (parseInt(quantity) < 0) {
                alert('Количество не может быть отрицательным!');
                e.preventDefault();
                return false;
            }
        });
    </script>
</body>
</html>