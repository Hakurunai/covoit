package fr.greta.cda.s4_covoitmobile.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorMessageResponse
{
	private int statusCode;
	private LocalDateTime timeStamp;
	private String message;
	private String accountStatus;
}
