package org.example.model;

/**
 * Room: Plain data class representing a hotel room.
 * Contains room details like number, type, capacity, and nightly price.
 */
public class Room {
    private int roomId;
    private String roomNo;
    private String roomType;
    private int capacity;
    private double pricePerNight;

    /**
     * Constructor to create a Room instance.
     */
    public Room(int roomId, String roomNo, String roomType, int capacity, double pricePerNight) {
        this.roomId = roomId;
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
    }

    // Getters
    public int getRoomId() {
        return roomId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }
}
