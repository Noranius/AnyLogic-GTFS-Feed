package uni_weimar.itd.anylogic_gtfs_adapter_conveyal;

import java.time.*;

/**
 * Class to encapsulate a time frame within a single day
 * @author ripo9018
 *
 */
public class TimePeriod {

	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	
	public LocalDate getDate() {
		return date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public TimePeriod(LocalDate date, LocalTime startTime, LocalTime endTime)
	{
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	
}
