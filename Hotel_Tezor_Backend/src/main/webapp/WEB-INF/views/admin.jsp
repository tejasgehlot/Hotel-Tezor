<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Tezor | Admin Dashboard</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>

    <header>
        <h1 class="hotel-title">TEZOR MANAGEMENT</h1>
        <p>Internal Operations & Reservation Control Center</p>
        <nav><a href="index.html">Book a Room</a> <a href="admin">Admin</a></nav>
    </header>

    <div class="container">
        <div class="dashboard-grid">

            <%-- BOOKINGS TABLE --%>
            <div class="table-section">
                <h2>All Bookings</h2>
                <div class="table-scroll">
                    <c:choose>
                        <c:when test="${empty bookings}">
                            <table>
                                <tr>
                                    <td style="text-align: center; padding: 20px;">No records yet</td>
                                </tr>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Code</th>
                                        <th>Customer</th>
                                        <th>Phone</th>
                                        <th>Room</th>
                                        <th>Check In</th>
                                        <th>Check Out</th>
                                        <th>Guests</th>
                                        <th>Amount</th>
                                        <th>Payment</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="b" items="${bookings}">
                                        <tr>
                                            <td><c:out value="${b.bookingCode}"/></td>
                                            <td><c:out value="${b.name}"/></td>
                                            <td><c:out value="${b.phone}"/></td>
                                            <td><c:out value="${b.roomNo}"/> - <c:out value="${b.roomType}"/></td>
                                            <td><c:out value="${b.checkIn}"/></td>
                                            <td><c:out value="${b.checkOut}"/></td>
                                            <td><c:out value="${b.guests}"/></td>
                                            <td>₹<fmt:formatNumber value="${b.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                            <td><span class="badge ${b.paymentStatus.toLowerCase()}"><c:out value="${b.paymentStatus}"/></span></td>
                                            <td><span class="badge ${b.bookingStatus.toLowerCase()}"><c:out value="${b.bookingStatus}"/></span></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${b.bookingStatus == 'CONFIRMED'}">
                                                        <form action="cancel-booking" method="post" style="display: inline;">
                                                            <input type="hidden" name="booking_id" value="${b.bookingId}">
                                                            <button type="submit" class="btn-danger">Cancel</button>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        -
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <%-- ROOMS TABLE --%>
            <div class="table-section">
                <h2>Rooms</h2>
                <div class="table-scroll">
                    <c:choose>
                        <c:when test="${empty rooms}">
                            <table>
                                <tr>
                                    <td style="text-align: center; padding: 20px;">No records yet</td>
                                </tr>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Room ID</th>
                                        <th>Room No</th>
                                        <th>Type</th>
                                        <th>Capacity</th>
                                        <th>Price Per Night</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="r" items="${rooms}">
                                        <tr>
                                            <td><c:out value="${r.roomId}"/></td>
                                            <td><c:out value="${r.roomNo}"/></td>
                                            <td><c:out value="${r.roomType}"/></td>
                                            <td><c:out value="${r.capacity}"/> Guests</td>
                                            <td>₹<fmt:formatNumber value="${r.pricePerNight}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <%-- CUSTOMERS TABLE --%>
            <div class="table-section">
                <h2>Customers</h2>
                <div class="table-scroll">
                    <c:choose>
                        <c:when test="${empty customers}">
                            <table>
                                <tr>
                                    <td style="text-align: center; padding: 20px;">No records yet</td>
                                </tr>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Customer ID</th>
                                        <th>Code</th>
                                        <th>Name</th>
                                        <th>Phone</th>
                                        <th>Email</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${customers}">
                                        <tr>
                                            <td><c:out value="${c.customerId}"/></td>
                                            <td><c:out value="${c.customerCode}"/></td>
                                            <td><c:out value="${c.name}"/></td>
                                            <td><c:out value="${c.phone}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty c.email}">
                                                        -
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${c.email}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </div>
    </div>

</body>
</html>
