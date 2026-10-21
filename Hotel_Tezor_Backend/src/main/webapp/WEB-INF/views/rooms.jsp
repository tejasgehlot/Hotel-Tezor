<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Tezor | Available Rooms</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>

    <header>
        <h1 class="hotel-title">HOTEL TEZOR</h1>
        <p>Available Rooms For Your Stay</p>
        <nav><a href="index.html">Book a Room</a> <a href="admin">Admin</a></nav>
    </header>

    <div class="container">

        <div class="summary-box">
            <span><strong>Check-In:</strong> <c:out value="${checkIn}"/></span>
            <span><strong>Check-Out:</strong> <c:out value="${checkOut}"/></span>
            <span><strong>Guests:</strong> <c:out value="${guests}"/></span>
            <span><strong>Nights:</strong> <c:out value="${nights}"/></span>
            <a href="index.html">Change dates</a>
        </div>

        <h2 class="section-title">Step 2: Choose a Room</h2>

        <%-- Only c:when and c:otherwise may sit directly inside c:choose --%>
        <c:choose>
            <c:when test="${empty rooms}">
                <p class="notice">No rooms are available for these dates. Please try different dates.</p>
            </c:when>
            <c:otherwise>
                <div class="room-grid">
                    <c:forEach var="room" items="${rooms}">
                        <div class="room-card">
                            <h3>Room <c:out value="${room.roomNo}"/></h3>
                            <span class="badge confirmed"><c:out value="${room.roomType}"/></span>
                            <p>Capacity: ${room.capacity} guests</p>
                            <p>₹<fmt:formatNumber value="${room.pricePerNight}" minFractionDigits="2" maxFractionDigits="2"/> / night</p>
                            <p class="total">Total: ₹<fmt:formatNumber value="${room.pricePerNight * nights}" minFractionDigits="2" maxFractionDigits="2"/></p>

                            <form action="book-room" method="GET">
                                <input type="hidden" name="room_id" value="${room.roomId}">
                                <input type="hidden" name="check_in" value="${checkIn}">
                                <input type="hidden" name="check_out" value="${checkOut}">
                                <input type="hidden" name="guests" value="${guests}">
                                <button type="submit" class="btn-small">Select This Room</button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </div>

</body>
</html>