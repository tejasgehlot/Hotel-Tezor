<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Tezor | Reservation Confirmation</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>

    <header>
        <h1 class="hotel-title">HOTEL TEZOR</h1>
        <p>Reservation Confirmation Hub</p>
        <nav><a href="index.html">Book a Room</a> <a href="admin">Admin</a></nav>
    </header>

    <div class="status-wrapper">
        <div class="status-card success">
            <div class="icon-circle success">✓</div>
            <h2>Booking Registered!</h2>
            <p class="highlight-text">We are waiting for your arrival!</p>

            <div class="summary-box vertical">
                <span><strong>Booking Code:</strong> <c:out value="${booking.bookingCode}"/></span>
                <span><strong>Customer Code:</strong> <c:out value="${booking.customerCode}"/></span>
                <span><strong>Name:</strong> <c:out value="${booking.name}"/></span>
                <span><strong>Room:</strong> <c:out value="${booking.roomNo}"/> - <c:out value="${booking.roomType}"/></span>
                <span><strong>Check-In:</strong> <c:out value="${booking.checkIn}"/></span>
                <span><strong>Check-Out:</strong> <c:out value="${booking.checkOut}"/></span>
                <span><strong>Amount Due:</strong> ₹<fmt:formatNumber value="${booking.amount}" minFractionDigits="2" maxFractionDigits="2"/></span>
                <span><strong>Payment Status:</strong> <c:out value="${booking.paymentStatus}"/></span>
            </div>

            <p class="sub-text">Please keep your booking code safe. You will need it at check-in.</p>

            <div class="btn-group">
                <a href="index.html" class="btn-secondary">New Booking</a>
                <a href="admin" class="btn-primary-nav">View Admin Log</a>
            </div>
        </div>
    </div>

</body>
</html>
