# Hotel Management System

## Description
The provided Java code implements a basic Hotel Management System using JavaFX for the graphical user interface (GUI). The application allows users to book rooms, order food, and check out, all while managing room availability and services efficiently.

## Technologies Used
- Java
- JavaFX

## Installation and Setup
1. Clone the repository
   git clone https://github.com/riddhima-26/Hotel-Management-System.git

2. Make sure you have javafx-sdk-23.0.1 installed.
3.Go to project directory and then on cmd type:
javac -d bin -cp "src;lib\javafx-sdk-23.0.1\lib\*" hotelmanagement\*.java

Then enter:

java --module-path lib\javafx-sdk-23.0.1\lib --add-modules javafx.controls -cp bin hotelmanagement.HotelManagementSystem
