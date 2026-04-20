package com.grietm.challenge.application.port.in;

import java.time.LocalDate;
import java.util.List;

public record PersistSearchCommand(
	String searchId,
	String hotelId,
	LocalDate checkIn,
	LocalDate checkOut,
	List<Integer> ages
) {
}
