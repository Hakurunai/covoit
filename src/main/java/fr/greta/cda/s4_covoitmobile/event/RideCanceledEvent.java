package fr.greta.cda.s4_covoitmobile.event;

import java.util.List;

public record RideCanceledEvent(List<String> recipients, String dateTrajet)
{
}
