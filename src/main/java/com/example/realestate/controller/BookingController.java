package com.example.realestate.controller;

import com.example.realestate.model.Booking;
import com.example.realestate.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @PostMapping
    public String createBooking(@RequestBody Booking booking) {

        if (booking.getStatus() == null || booking.getStatus().isEmpty()) {
            booking.setStatus("Pending");
        }

        bookingRepository.save(booking);
        return "Booking saved successfully to the database!";
    }

    @GetMapping("/cancel/{id}")
    public void deleteBookingBrowser(@PathVariable Long id) {
        bookingRepository.deleteById(id);
    }
}