<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Tezor | Confirm Your Reservation</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>

    <header>
        <h1 class="hotel-title">HOTEL TEZOR</h1>
        <p>Almost There: Confirm Your Reservation</p>
        <nav><a href="index.html">Book a Room</a> <a href="admin">Admin</a></nav>
    </header>

    <div class="container">

        <div class="summary-box">
            <span><strong>Room:</strong> <c:out value="${room.roomNo}"/> - <c:out value="${room.roomType}"/></span>
            <span><strong>Check-In:</strong> <c:out value="${checkIn}"/></span>
            <span><strong>Check-Out:</strong> <c:out value="${checkOut}"/></span>
            <span><strong>Guests:</strong> <c:out value="${guests}"/></span>
            <span><strong>Total:</strong> ₹<fmt:formatNumber value="${room.pricePerNight * nights}" minFractionDigits="2" maxFractionDigits="2"/></span>
        </div>

        <div class="booking-card">
            <h2 class="card-title">Step 3: Your Details</h2>
            <p class="hint">No payment needed now. Payment is collected at the hotel.</p>

            <form method="post" action="confirm-booking">
                <%-- Hidden Fields --%>
                <input type="hidden" name="room_id" value="${room.roomId}">
                <input type="hidden" name="check_in" value="${checkIn}">
                <input type="hidden" name="check_out" value="${checkOut}">
                <input type="hidden" name="guests" value="${guests}">

                <div class="form-grid">
                    <div class="form-group">
                        <label for="name">Full Name</label>
                        <input type="text" id="name" name="name" required maxlength="100">
                    </div>
                    <div class="form-group">
                        <label for="phone">Phone Number</label>
                        <input type="tel" id="phone" name="phone" required pattern="[0-9+]{10,15}">
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" maxlength="100">
                    </div>
                </div>

                <button type="submit" class="btn-submit">Confirm Reservation</button>
            </form>
        </div>

    </div>

</body>
</html>
