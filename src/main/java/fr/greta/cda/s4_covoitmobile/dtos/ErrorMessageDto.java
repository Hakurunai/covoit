package fr.greta.cda.s4_covoitmobile.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorMessageDto
{
	private int statusCode;
	private LocalDateTime timeStamp;
	private String message;
}
