package com.example.demo.Controller;

import com.example.demo.DTO.ErrorDTO;
import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Request.BookingRequest;
import com.example.demo.Request.EditBookingRequest;
import com.example.demo.Service.BookingService;
import com.example.demo.Service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@RestController
//@
public class BookingController {
    private final BookingService bookingService;
    private final RoomService roomService;
    private final UserRepository userRepository;


    public BookingController(BookingService bookingService, RoomService roomService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.userRepository = userRepository;
    }

    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {

        User user = userRepository.findById(bookingRequest.getUserID()).orElse(null);
        if (user == null) {
            // If user does not exist, return a 404 Not Found response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("User does not exist"));
        }

        if (roomService.getRoomByID(bookingRequest.getRoomID())==null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Room does not exist"));
        }

        if (bookingService.isOverlapping(bookingRequest)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Room unavailable"));
        }

        LocalDateTime currentTime = LocalDateTime.now();

        if(bookingRequest.getTimeFrom().compareTo(bookingRequest.getTimeTo()) > 0 || bookingRequest.getTimeFrom().compareTo(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString()) < 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Invalid date/time"));
        }
        LocalDate requestedDate = bookingRequest.getDateOfBooking().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        if (requestedDate.isBefore(currentDate)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Invalid date/time"));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false); // Set lenient mode to false to catch invalid dates

        try {
            calendar.set(Calendar.YEAR, requestedDate.getYear());
            calendar.set(Calendar.MONTH, requestedDate.getMonthValue() - 1); // Months are zero-based in Calendar
            calendar.set(Calendar.DATE, requestedDate.getDayOfMonth());

            // Check if the date is valid
            int actualMaxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (requestedDate.getDayOfMonth() > actualMaxDayOfMonth) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Invalid date/time"));
            }

            // If the date is valid, we'll reach here without any exception
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Invalid date/time"));
        }


        String response = bookingService.createBooking(bookingRequest.getUserID(), bookingRequest.getRoomID(), bookingRequest.getDateOfBooking(), bookingRequest.getTimeFrom(), bookingRequest.getTimeTo(), bookingRequest.getPurpose());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/book")
    public ResponseEntity<?> updateBooking(@RequestBody EditBookingRequest editBookingRequest) {

        if (roomService.getRoomByID(editBookingRequest.getRoomID())==null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Room does not exist"));
        }

        User user = userRepository.findById(editBookingRequest.getUserID()).orElse(null);
        if (user == null) {
            // If user does not exist, return a 404 Not Found response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("User does not exist"));
        }

        if (bookingService.isOverlapping(editBookingRequest)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Room unavailable"));
        }

        if(editBookingRequest.getTimeFrom().compareTo(editBookingRequest.getTimeTo()) > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Invalid date/time"));
        }
        LocalDate requestedDate = editBookingRequest.getDateOfBooking().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        if (requestedDate.isBefore(currentDate)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Invalid date/time"));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false); // Set lenient mode to false to catch invalid dates
        try {
            calendar.set(Calendar.YEAR, requestedDate.getYear());
            calendar.set(Calendar.MONTH, requestedDate.getMonthValue() - 1); // Months are zero-based in Calendar
            calendar.set(Calendar.DATE, requestedDate.getDayOfMonth());
            // If the date is valid, we'll reach here without any exception
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Invalid date/time"));
        }

        String response = bookingService.updateBooking(editBookingRequest.getUserID(), editBookingRequest.getRoomID(), editBookingRequest.getBookingID(), editBookingRequest.getDateOfBooking(), editBookingRequest.getTimeFrom(), editBookingRequest.getTimeTo(), editBookingRequest.getPurpose());
        return ResponseEntity.ok(response);


//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body("User does not exist");
    }



    @DeleteMapping("/book")
    public ResponseEntity<?> deleteBooking(@RequestParam("bookingID") int bookingID) {
        if(!bookingService.isBookingIdPresent(bookingID)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Booking does not exist"));
        }
        String response = bookingService.deleteBooking(bookingID);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ErrorDTO("Booking Deleted successfully"));
    }

}