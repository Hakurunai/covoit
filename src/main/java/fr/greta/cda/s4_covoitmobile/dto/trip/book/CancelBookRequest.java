package fr.greta.cda.s4_covoitmobile.dto.trip.book;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelBookRequest
{
	@NotNull(message = "A passenger is required")
	Long passengerId;
}
