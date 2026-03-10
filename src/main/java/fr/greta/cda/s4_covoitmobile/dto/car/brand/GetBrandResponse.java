package fr.greta.cda.s4_covoitmobile.dto.car.brand;

import fr.greta.cda.s4_covoitmobile.models.CarBrand;
import lombok.Data;

@Data
public class GetBrandResponse
{
	public GetBrandResponse(CarBrand brand)
	{
		id = brand.getId();
		brandName = brand.getName();
	}
	
	private Long id;
	private String brandName;
}
