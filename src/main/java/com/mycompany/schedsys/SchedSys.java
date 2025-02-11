package com.mycompany.schedsys;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SchedSys {
    public static void main(String[] args) {
        FunctionHallSchedulingSystem.main(args);
    }

    public static class Booking {
        String section;
        String date;
        String time;

        Booking(String section, String date, String time) {
            this.section = section;
            this.date = date;
            this.time = time;
        }

        @Override
        public String toString() {
            return "Section: " + section + ", Date: " + date + ", Time: " + time;
        }
    }

    public static class FunctionHallSchedulingSystem {
        private static final String[] VALID_SECTIONS = {"INF 243", "INF 244", "INF 245", "INF 246", "INF 247"};
        private static ArrayList<Booking> bookings = new ArrayList<>();
        private static Scanner scanner = new Scanner(System.in);

        public static void main(String[] args) {
            while (true) {
                System.out.println("\nFunction Hall Scheduling System");
                System.out.println("1. Create Booking");
                System.out.println("2. View Bookings");
                System.out.println("3. Update Booking");
                System.out.println("4. Delete Booking");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                int choice = getIntInput();

                switch (choice) {
                    case 1:
                        createBooking();
                        break;
                    case 2:
                        viewBookings();
                        break;
                    case 3:
                        updateBooking();
                        break;
                    case 4:
                        deleteBooking();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }

        private static void createBooking() {
            System.out.println("\n(Available Sections: INF 243, INF 244, INF 245, INF 246, INF 247)");
            System.out.print("Enter section: ");
            String section = scanner.nextLine();
            if (!isValidSection(section)) {
                System.out.println("Invalid section. Booking not created.");
                return;
            }

            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = scanner.nextLine();
            if (!isValidYear(date)) {
                System.out.println("Invalid Year. Booking not created.");
                return;
            }

            System.out.print("Enter time (HH:MM [AM/PM] to HH:MM [AM/PM]): ");
            String time = scanner.nextLine();
            if (!isValidTimeFormat(time)) {
                System.out.println("Invalid time format. Please use 'HH:MM [AM/PM] to HH:MM [AM/PM]' format.");
                return;
            }

            // Check for time conflicts with existing bookings on the same date
            Optional<Booking> conflictingBooking = bookings.stream()
                .filter(b -> b.date.equals(date) && isTimeOverlap(time, b.time))
                .findFirst();

            if (conflictingBooking.isPresent()) {
                System.out.println("Conflict detected with existing booking: " + conflictingBooking.get());
                System.out.println("Booking not created.");
                return;
            }

            bookings.add(new Booking(section, date, time));
            System.out.println("Booking created successfully.");
        }

        private static void viewBookings() {
            if (bookings.isEmpty()) {
                System.out.println("No bookings available.");
            } else {
                for (Booking booking : bookings) {
                    System.out.println(booking);
                }
            }
        }

        private static void updateBooking() {
            System.out.print("Enter the index of the booking to update: ");
            int index = getIntInput();
            if (index < 0 || index >= bookings.size()) {
                System.out.println("Invalid index. Booking not updated.");
                return;
            }

            System.out.print("Enter new section (INF 243, INF 244, INF 245, INF 246, INF 247): ");
            String section = scanner.nextLine();
            if (!isValidSection(section)) {
                System.out.println("Invalid section. Booking not updated.");
                return;
            }

            System.out.print("Enter new date (YYYY-MM-DD): ");
            String date = scanner.nextLine();
            if (!isValidYear(date)) {
                System.out.println("Invalid Year. Booking not updated.");
                return;
            }

            System.out.print("Enter new time (HH:MM [AM/PM] to HH:MM [AM/PM]): ");
            String time = scanner.nextLine();
            if (!isValidTimeFormat(time)) {
                System.out.println("Invalid time format. Please use 'HH:MM [AM/PM] to HH:MM [AM/PM]' format.");
                return;
            }

            Booking booking = bookings.get(index);
            booking.section = section;
            booking.date = date;
            booking.time = time;
            System.out.println("Booking updated successfully.");
        }

        private static void deleteBooking() {
            System.out.print("Enter the index of the booking to delete: ");
            int index = getIntInput();
            if (index < 0 || index >= bookings.size()) {
                System.out.println("Invalid index. Booking not deleted.");
                return;
            }
            bookings.remove(index);
            System.out.println("Booking deleted successfully.");
        }

        private static boolean isValidSection(String section) {
            for (String validSection : VALID_SECTIONS) {
                if (validSection.equals(section)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isValidYear(String date) {
            try {
                String year = date.substring(0, 4);
                return year.equals("2024");
            } catch (Exception e) {
                return false;
            }
        }

        private static boolean isValidTimeFormat(String time) {
            time = time.trim();
            if (!time.matches(".+\\sto\\s.+")) {
                System.out.println("The time must contain 'to' surrounded by spaces.");
                return false;
            }

            String[] parts = time.split("\\s+to\\s+");
            for (String part : parts) {
                if (!part.matches("(?i)^(0?[1-9]|1[0-2]):[0-5][0-9] [AP]M$")) {
                    System.out.println("Invalid time format. Please use 'HH:MM AM/PM' format.");
                    return false;
                }
            }
            return true;
        }

        private static boolean isTimeOverlap(String newTime, String existingTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

            String[] newParts = newTime.split(" to ");
            String[] existingParts = existingTime.split(" to ");

            LocalTime newStart = LocalTime.parse(newParts[0], formatter);
            LocalTime newEnd = LocalTime.parse(newParts[1], formatter);
            LocalTime existingStart = LocalTime.parse(existingParts[0], formatter);
            LocalTime existingEnd = LocalTime.parse(existingParts[1], formatter);

            return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
        }

        private static int getIntInput() {
            while (true) {
                try {
                    return Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.print("Invalid input. Please enter a valid integer: ");
                }
            }
        }
    }
}