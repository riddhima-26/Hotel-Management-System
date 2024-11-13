package hotelmanagement;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.*;

public class HotelManagementSystem extends Application {
    private static final int TOTAL_SINGLE_ROOMS = 10;
    private static final int TOTAL_DOUBLE_ROOMS = 10;
    
    private List<Room> rooms = new ArrayList<>();
    private List<RoomService> roomServices = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage) {
        initializeRooms();
        
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label bookingLabel = new Label("Room Booking");
        ComboBox<String> roomTypeCombo = new ComboBox<>();
        roomTypeCombo.getItems().addAll("Single", "Double");
        TextField guestNameField = new TextField();
        guestNameField.setPromptText("Guest Name");
        Button bookButton = new Button("Book Room");
        
        Label serviceLabel = new Label("Room Service");
        TextField roomNumberField = new TextField();
        roomNumberField.setPromptText("Room Number");
        ComboBox<String> foodCombo = new ComboBox<>();
        foodCombo.getItems().addAll(
            "Breakfast - ₹800", 
            "Lunch - ₹1200", 
            "Dinner - ₹1500"
        );
        Button orderButton = new Button("Order Food");
        
        Label checkoutLabel = new Label("Checkout");
        TextField checkoutRoomField = new TextField();
        checkoutRoomField.setPromptText("Room Number");
        Button checkoutButton = new Button("Checkout");
        
        TextArea displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefRowCount(10);
        
        grid.add(bookingLabel, 0, 0);
        grid.add(roomTypeCombo, 0, 1);
        grid.add(guestNameField, 1, 1);
        grid.add(bookButton, 2, 1);
        
        grid.add(serviceLabel, 0, 3);
        grid.add(roomNumberField, 0, 4);
        grid.add(foodCombo, 1, 4);
        grid.add(orderButton, 2, 4);
        
        grid.add(checkoutLabel, 0, 6);
        grid.add(checkoutRoomField, 0, 7);
        grid.add(checkoutButton, 1, 7);
        
        grid.add(displayArea, 0, 9, 3, 1);
        
        Button viewRoomsButton = new Button("View Available Rooms");
        grid.add(viewRoomsButton, 0, 8);
        
        viewRoomsButton.setOnAction(e -> {
            StringBuilder status = new StringBuilder("Room Status:\n");
            for (Room room : rooms) {
                status.append(String.format("Room %d (%s): %s\n", 
                    room.getRoomNumber(), 
                    room.getType(), 
                    room.isOccupied() ? "Occupied by " + room.getGuestName() : "Available"));
            }
            displayArea.setText(status.toString());
        });
        
        bookButton.setOnAction(e -> {
            String roomType = roomTypeCombo.getValue();
            String guestName = guestNameField.getText();
            
            if (roomType == null || guestName.isEmpty()) {
                displayArea.setText("Please fill in all fields");
                return;
            }
            
            Room bookedRoom = bookRoom(roomType, guestName);
            if (bookedRoom != null) {
                displayArea.setText(String.format("Room %d booked for %s", 
                    bookedRoom.getRoomNumber(), guestName));
            } else {
                displayArea.setText("No " + roomType + " rooms available");
            }
        });
        
        orderButton.setOnAction(e -> {
            try {
                int roomNum = Integer.parseInt(roomNumberField.getText());
                String foodOrder = foodCombo.getValue();
                
                if (foodOrder == null) {
                    displayArea.setText("Please select a food item");
                    return;
                }
                
                if (orderFood(roomNum, foodOrder)) {
                    displayArea.setText("Order placed for Room " + roomNum);
                } else {
                    displayArea.setText("Invalid room number or room not occupied");
                }
            } catch (NumberFormatException ex) {
                displayArea.setText("Please enter a valid room number");
            }
        });
        
        checkoutButton.setOnAction(e -> {
            try {
                int roomNum = Integer.parseInt(checkoutRoomField.getText());
                double bill = checkout(roomNum);
                if (bill >= 0) {
                    displayArea.setText(String.format("Checkout complete. Total bill: ₹%.2f", bill));
                } else {
                    displayArea.setText("Invalid room number or room not occupied");
                }
            } catch (NumberFormatException ex) {
                displayArea.setText("Please enter a valid room number");
            }
        });
        
        Scene scene = new Scene(grid, 600, 400);
        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void initializeRooms() {
        for (int i = 1; i <= TOTAL_SINGLE_ROOMS; i++) {
            rooms.add(new Room(i, "Single", 5000.0)); // ₹5000 for single room
        }
        for (int i = TOTAL_SINGLE_ROOMS + 1; i <= TOTAL_SINGLE_ROOMS + TOTAL_DOUBLE_ROOMS; i++) {
            rooms.add(new Room(i, "Double", 8000.0)); // ₹8000 for double room
        }
    }
    
    private Room bookRoom(String type, String guestName) {
        for (Room room : rooms) {
            if (room.getType().equalsIgnoreCase(type) && !room.isOccupied()) {
                room.book(guestName);
                return room;
            }
        }
        return null;
    }
    
    private boolean orderFood(int roomNumber, String foodOrder) {
        Room room = rooms.stream()
            .filter(r -> r.getRoomNumber() == roomNumber && r.isOccupied())
            .findFirst()
            .orElse(null);
            
        if (room != null) {
            double price = switch (foodOrder) {
                case "Breakfast - ₹800" -> 800.0;
                case "Lunch - ₹1200" -> 1200.0;
                case "Dinner - ₹1500" -> 1500.0;
                default -> 0.0;
            };
            roomServices.add(new RoomService(roomNumber, foodOrder, price));
            return true;
        }
        return false;
    }
    
    private double checkout(int roomNumber) {
        Room room = rooms.stream()
            .filter(r -> r.getRoomNumber() == roomNumber && r.isOccupied())
            .findFirst()
            .orElse(null);
            
        if (room != null) {
            double roomCharge = room.getRate();
            double serviceCharge = roomServices.stream()
                .filter(service -> service.roomNumber() == roomNumber)
                .mapToDouble(RoomService::price)
                .sum();
            
            room.checkout();
            roomServices.removeIf(service -> service.roomNumber() == roomNumber);
            
            return roomCharge + serviceCharge;
        }
        return -1;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

class Room {
    private final int roomNumber;
    private final String type;
    private final double rate;
    private boolean occupied;
    private String guestName;
    
    public Room(int roomNumber, String type, double rate) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.rate = rate;
        this.occupied = false;
    }
    
    public void book(String guestName) {
        this.guestName = guestName;
        this.occupied = true;
    }
    
    public void checkout() {
        this.guestName = null;
        this.occupied = false;
    }
    
    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getRate() { return rate; }
    public boolean isOccupied() { return occupied; }
    public String getGuestName() { return guestName; }
}

record RoomService(int roomNumber, String foodOrder, double price) {}