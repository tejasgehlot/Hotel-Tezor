<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Tezor | Reservation Status</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>

    <header>
        <h1 class="hotel-title">HOTEL TEZOR</h1>
        <p>Reservation Status</p>
        <nav><a href="index.html">Book a Room</a> <a href="admin">Admin</a></nav>
    </header>

    <div class="status-wrapper">
        <div class="status-card failure">
            <div class="icon-circle error">✕</div>
            <h2>Booking Not Completed</h2>
            <p class="highlight-text error-text"><c:out value="${errorMessage}"/></p>
            <p class="sub-text">Please search again to see current availability.</p>
            <div class="btn-group">
                <a href="index.html" class="btn-primary-nav">Search Again</a>
            </div>
        </div>
    </div>

</body>
</html>