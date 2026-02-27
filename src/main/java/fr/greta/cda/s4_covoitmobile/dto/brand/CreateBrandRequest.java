package fr.greta.cda.s4_covoitmobile.dto.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBrandRequest
{
	@NotBlank(message = "A name is required")
	@Size(min = 2, max = 40, message = "Brand name must be comprise between 2 and 40 characters")
	private String brandName;
}
