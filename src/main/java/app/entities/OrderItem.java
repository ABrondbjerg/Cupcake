<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select Your Cupcake</title>
</head>
<body>

<h2>Choose a Cupcake</h2>

<!-- Form to submit the selection -->
<form method="post" action="/add-to-basket">
    <label for="top">Choose a Topping:</label>
    <select id="top" name="top" required>
        <option value="">Select a topping</option>
        <option value="1">Chocolate (5.00 DKK)</option>
        <option value="2">Blueberry (5.00 DKK)</option>
        <option value="3">Raspberry (5.00 DKK)</option>
        <option value="4">Crispy (6.00 DKK)</option>
        <option value="5">Strawberry (6.00 DKK)</option>
        <option value="6">Rum/Raisin (7.00 DKK)</option>
        <option value="7">Orange (8.00 DKK)</option>
        <option value="8">Lemon (8.00 DKK)</option>
        <option value="9">Blue Cheese (9.00 DKK)</option>
    </select>

    <br><br>

    <!-- Quantity Selection -->
    <label for="bottom">Choose a Bottom:</label>
    <select id="bottom" name="bottom" required>
        <option value="">Select a bottom</option>
        <option value="1">Chocolate (5.00 DKK)</option>
        <option value="2">Vanilla (5.00 DKK)</option>
        <option value="3">Nutmeg (5.00 DKK)</option>
        <option value="4">Pistachio (6.00 DKK)</option>
        <option value="5">Almond (7.00 DKK)</option>
    </select>

    <br><br>


    <label for="quantity">Choose quantity:</label>
    <select id="quantity" name="quantity" required>
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
        <option value="6">6</option>
        <option value="7">7</option>
        <option value="8">8</option>
        <option value="9">9</option>
        <option value="10">10</option>
    </select>


    <button type="submit">Add</button>
</form>

<hr>

<h2>Basket</h2>
<ul>
    <li th:each="cupcake, iterStat : ${basket}">
        <span th:text="|${cupcake.quantity} ${cupcake.toppingName} with ${cupcake.bottomName} - ${cupcake.totalPrice} DKK|"></span>
        <!-- Delete Form with item index -->
        <form method="post" action="/remove-from-basket" style="display:inline;">
            <input type="hidden" name="index" th:value="${iterStat.index}" />
            <button type="submit">X</button>
        </form>
    </li>
</ul>

<!-- Total Price Display -->
<h3>Total Price: <span th:text="${totalPrice}"></span> DKK</h3>

<!-- Pay Button -->
<form method="post" action="/pay">
    <button type="submit">Pay</button>
</form>

</body>
</html>