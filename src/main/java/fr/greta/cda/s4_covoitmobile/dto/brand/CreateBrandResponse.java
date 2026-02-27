package fr.greta.cda.s4_covoitmobile.dto.brand;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBrandResponse
{
	private Long Id;
	private String brandName;
}
