package fr.greta.cda.s4_covoitmobile.dto.car.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateBrandRequest
{
	@NotBlank(message = "A name is required")
	@Size(min = 2, max = 40, message = "Brand name must be comprise between 2 and 40 characters")
	private String brandName;
}
