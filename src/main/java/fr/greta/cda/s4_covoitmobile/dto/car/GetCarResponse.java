package fr.greta.cda.s4_covoitmobile.dto.car;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetCarResponse
{
	private Long carId;
	private String brandId;
	private String model;
	private Short seats;
	private String licensePlate;
}
