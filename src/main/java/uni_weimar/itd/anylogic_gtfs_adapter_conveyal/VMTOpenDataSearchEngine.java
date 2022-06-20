package uni_weimar.itd.anylogic_gtfs_adapter_conveyal;

import java.util.*;

import java.time.*;

import com.conveyal.gtfs.model.*;
import com.conveyal.gtfs.model.Calendar;

public class VMTOpenDataSearchEngine {
	
	private VMTOpenDataFileLoader fileLoader;
	
	/**
	 * default constructor
	 * @param fileLoader
	 */
	public VMTOpenDataSearchEngine(VMTOpenDataFileLoader fileLoader)
	{
		this.fileLoader = fileLoader;
	}

	/**
	 * get all stop times for a stop of an agency and route within a specific time
	 * @param stopName name of the physical stop
	 * @param routeName name of the route
	 * @param directionId direction id (0 or 1)
	 * @param period period in which all stops are
	 * @return
	 */
	public ArrayList<StopTime> getStopTimes (String agencyName, String stopName, String routeName, int directionId, TimePeriod period)
	{
		//initialize result
		ArrayList<StopTime> timesEf = new ArrayList<StopTime>();
		
		//fetch id's
		String routeId = this.getRouteIdByName(agencyName, routeName);
		String stopId = this.getStopId(stopName);
				
		//fetch all trips of a route
		HashMap<Integer,Trip> allTrips = this.getAllTripsOfRoute(routeId);
				
		//define weekday
		DayOfWeek myDay = DayOfWeek.from(period.getDate());
		int dayNumber = myDay.getValue();
		
		for (int tripId : allTrips.keySet())
		{
			Trip tempTrip = allTrips.get(tripId); 
			if (tempTrip.direction_id == directionId)
			{
				Calendar calendar = this.getCalendar(tempTrip.service_id);
				boolean dayCovered;
				
				//Is the specific date covered by the calendar time
				switch(dayNumber)
				{
					case 1: dayCovered = calendar.monday == 1; break;
					case 2: dayCovered = calendar.tuesday == 1;break;
					case 3: dayCovered = calendar.wednesday == 1; break;
					case 4: dayCovered = calendar.thursday == 1;  break;
					case 5: dayCovered = calendar.friday == 1; break;
					case 6: dayCovered = calendar.saturday == 1;  break;
					case 7: dayCovered = calendar.sunday == 1; break;
					default: dayCovered = false;
				}
				
				//is there an exception for the trip
				Map<LocalDate, CalendarDate> exceptionDates = this.getCalendarDates(tempTrip.service_id);
				if (exceptionDates.containsKey(period.getDate()))
				{
					CalendarDate exception = exceptionDates.get(period.getDate());
					if (exception.exception_type == 1)	//service has been added for the date
					{
						dayCovered = true;
					}
					else
					{
						dayCovered = false;
					}
				}
				
				//if the day is covered by the trip go on
				if (dayCovered) 
				{
					Iterable<StopTime> tempStopTimes = this.fileLoader.getDataFeed()
							.getOrderedStopTimesForTrip(tempTrip.trip_id);
					for (StopTime time : tempStopTimes) 
					{
						if (time.departure_time < 86400) {
							LocalTime departureTime = LocalTime.ofSecondOfDay(time.departure_time);
							if (time.stop_id.equals(stopId) && departureTime.compareTo(period.getStartTime()) >= 0 //Is at or after start time
									&& departureTime.compareTo(period.getEndTime()) <= 0) //Is at or before start time
							{
								timesEf.add(time);
							}
						}
					} 
				}	
			}
		}
		
		return timesEf;
	}
	
	/**
	 * get a route by its name and agency
	 * @param routeName
	 * @return
	 */
	public String getRouteIdByName(String agencyName, String routeName) 
	{
		Collection<Route> allRoutes = this.fileLoader.getDataFeed().routes.values();
		for (Route route : allRoutes)
		{
			String routeAgencyName = this.fileLoader.getDataFeed().agency.get(route.agency_id).agency_name; 
			if (routeAgencyName.equals(agencyName)
					&& route.route_short_name.equals(routeName))
			{
				return route.route_id;
			}
		}
		return null;
	}

	/**
	 * get the id of a stop by its name
	 * @param stopName
	 * @return
	 */
	public String getStopId (String stopName)
	{
		Collection<Stop> allStops = this.fileLoader.getDataFeed().stops.values();
		for (Stop stop : allStops)
		{
			if (stop.stop_name.equals(stopName))
			{
				return stop.stop_id;
			}
		}
		return null;
	}
	
	/**
	 * get all Trips of a Route
	 * @param routeId
	 * @return
	 */
	public HashMap<Integer,Trip> getAllTripsOfRoute(String routeId)
	{
		Collection<Trip> allTrips = this.fileLoader.getDataFeed().trips.values();
		HashMap<Integer,Trip> result = new HashMap<Integer,Trip>();
		for (Trip trip : allTrips)
		{
			if (trip.route_id.equals(routeId))
			{
				result.put(trip.id, trip);
			}
		}
		return result;
	}
	
	/**
	 * Get the calender for a specified service
	 * @param serviceId
	 * @return
	 */
	public Calendar getCalendar(String serviceId)
	{
		return this.fileLoader.getDataFeed().services.get(serviceId).calendar;
	}
	
	public Map<LocalDate, CalendarDate> getCalendarDates(String serviceId)
	{
		return this.fileLoader.getDataFeed().services.get(serviceId).calendar_dates;
	}

	public Trip getTrip(String trip_id) {
		return this.fileLoader.getDataFeed().trips.get(trip_id);
	}

	public Route getRouteById(String route_id) {
		return this.fileLoader.getDataFeed().routes.get(route_id);
	}
	
}
