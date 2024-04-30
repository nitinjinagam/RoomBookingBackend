package com.example.demo.Service;

import com.example.demo.Model.Booking;
import com.example.demo.Model.Room;
import com.example.demo.Repository.BookingRepository;
import com.example.demo.Repository.RoomRepository;
import com.example.demo.Request.BookingRequest;
import com.example.demo.Request.EditBookingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public String createBooking(int userID, int roomID, Date dateOfBooking, String timeFrom, String timeTo, String purpose) {
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        Room room = optionalRoom.get();
        Booking booking = new Booking(userID, roomID, dateOfBooking, timeFrom, timeTo, purpose);
        bookingRepository.save(booking);
        return "Booking created successfully";
    }

    public String updateBooking(int userID, int roomID, int bookingID, Date dateOfBooking, String timeFrom, String timeTo, String purpose) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingID);
        Booking booking = optionalBooking.get();
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        Room room = optionalRoom.get();
        booking.setUserId(userID);
        booking.setRoomId(roomID);
        booking.setDateOfBooking(dateOfBooking);
        booking.setTimeFrom(timeFrom);
        booking.setTimeTo(timeTo);
        booking.setPurpose(purpose);
        bookingRepository.save(booking);
        return "Booking modified successfully";
    }

    public String deleteBooking(int bookingID) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingID);
        bookingRepository.deleteById(bookingID);
        return "Booking deleted successfully";
    }

    public boolean isOverlapping(BookingRequest bookingRequest) {
        int roomID = bookingRequest.getRoomID();
        List<Booking> existingBookings = bookingRepository.findByRoomId(roomID);

        Date newBookingDate = bookingRequest.getDateOfBooking();
        LocalTime newBookingStartTime = LocalTime.parse(bookingRequest.getTimeFrom(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime newBookingEndTime = LocalTime.parse(bookingRequest.getTimeTo(), DateTimeFormatter.ofPattern("HH:mm:ss"));

        for (Booking existingBooking : existingBookings) {
            Date existingBookingDate = existingBooking.getDateOfBooking();
            LocalTime existingBookingStartTime = LocalTime.parse(existingBooking.getTimeFrom(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime existingBookingEndTime = LocalTime.parse(existingBooking.getTimeTo(), DateTimeFormatter.ofPattern("HH:mm:ss"));

            // Check if the new booking date matches the existing booking date
            if (newBookingDate.equals(existingBookingDate)) {
                // Check if the new booking time frame overlaps with the existing booking time frame
                if ((newBookingStartTime.isBefore(existingBookingEndTime) && newBookingEndTime.isAfter(existingBookingStartTime)) ||
                        (newBookingStartTime.isAfter(existingBookingStartTime) && newBookingStartTime.isBefore(existingBookingEndTime)) ||
                        (newBookingEndTime.isAfter(existingBookingStartTime) && newBookingEndTime.isBefore(existingBookingEndTime))) {
                    return true; // Overlap detected
                }
            }
        }

        return false; // No overlap detected
    }

    public boolean isOverlapping(EditBookingRequest editBookingRequest) {
        int roomID = editBookingRequest.getRoomID();
        List<Booking> existingBookings = bookingRepository.findByRoomId(roomID);

        Date newBookingDate = editBookingRequest.getDateOfBooking();
        LocalTime newBookingStartTime = LocalTime.parse(editBookingRequest.getTimeFrom(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime newBookingEndTime = LocalTime.parse(editBookingRequest.getTimeTo(), DateTimeFormatter.ofPattern("HH:mm:ss"));

        for (Booking existingBooking : existingBookings) {
            // Skip the booking being edited
            if (existingBooking.getBookingId() == editBookingRequest.getBookingID()) {
                continue;
            }

            Date existingBookingDate = existingBooking.getDateOfBooking();
            LocalTime existingBookingStartTime = LocalTime.parse(existingBooking.getTimeFrom(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime existingBookingEndTime = LocalTime.parse(existingBooking.getTimeTo(), DateTimeFormatter.ofPattern("HH:mm:ss"));

            // Check if the new booking date matches the existing booking date
            if (newBookingDate.equals(existingBookingDate)) {
                // Check if the new booking time frame overlaps with the existing booking time frame
                if ((newBookingStartTime.isBefore(existingBookingEndTime) && newBookingEndTime.isAfter(existingBookingStartTime)) ||
                        (newBookingStartTime.isAfter(existingBookingStartTime) && newBookingStartTime.isBefore(existingBookingEndTime)) ||
                        (newBookingEndTime.isAfter(existingBookingStartTime) && newBookingEndTime.isBefore(existingBookingEndTime))) {
                    return true; // Overlap detected
                }
            }
        }

        return false; // No overlap detected
    }

    public boolean isBooked(int roomID) {
        return bookingRepository.existsById(roomID);
    }

    public boolean isBookingIdPresent(int bookingID) {
        Optional<Booking> booking = bookingRepository.findByBookingId(bookingID);
        return booking.isPresent();
    }

}