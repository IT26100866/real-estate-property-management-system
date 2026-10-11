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
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingRepository.save(booking);
    }

    // බ්‍රව්සරයෙන් ලින්ක් එක ගහලා ඩිලීට් කරන්න පුළුවන් වෙන්න මෙන්න මේක දාන්න:
    @GetMapping("/cancel/{id}")
    public void deleteBookingBrowser(@PathVariable Long id) {
        bookingRepository.deleteById(id);
    }
}