package uni_weimar.itd.anylogic_gtfs_adapter_conveyal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import com.conveyal.gtfs.model.*;

public class TestClass {
	
	public static void main(String[] args) {
		System.out.println("Load data");
		VMTOpenDataFileLoader loader = new VMTOpenDataFileLoader();
		String fileUrl = "https://transitfeeds.com/p/verkehrsverbund-mittelth-ringen/1080/latest/download";
		loader.loadFileFromUrl(fileUrl);
//		String localFile = "C:\\Users\\ripo9018\\Downloads\\VMT_GTFS_transitfeed.zip";
//		loader.loadLocalFile(localFile);
		System.out.println("Loading complete");
		
		System.out.println("Search...");
		VMTOpenDataSearchEngine engine = new VMTOpenDataSearchEngine(loader);
		TimePeriod period = new TimePeriod(LocalDate.of(2019, 12, 2), LocalTime.of(8, 0), LocalTime.of(9, 0));
		ArrayList<StopTime> stops = engine.getStopTimes("Erfurter Verkehrsbetriebe AG", "Erfurt, Hauptbahnhof", "3", 0, period);
		System.out.println("search complete");
		
		System.out.println("Output: " + stops.size());
		for (StopTime time : stops)
		{
			int seconds = time.departure_time % 60;
			int minutes = (time.departure_time / 60) % 60;
			int hour = time.departure_time / 3600;
			System.out.println(String.format("Trip: %s; %02d:%02d:%02d", time.trip_id, hour, minutes, seconds));
			Trip tempTrip = engine.getTrip(time.trip_id);
			Route tempRoute = engine.getRouteById(tempTrip.route_id);
			System.out.println("Route-Id: " + tempTrip.route_id);
			System.out.println("Route: " + tempRoute.route_short_name);
		}
		loader.close();
//		String fileLocal = "C:\\Users\\ripo9018\\eclipse-workspace\\anylogic-gtfs-adapter-conveyal\\VMT_GTFS.zip";
//		loader.loadLocalFile(fileLocal);
		
	}

}
