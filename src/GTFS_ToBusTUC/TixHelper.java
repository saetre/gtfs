
package GTFS_ToBusTUC;

import java.util.Date;

/**
	Helper class for file tix. 
	getLine take a string as input and then locates the fields.
	
	@author Tore Bruland
*/

public class TixHelper {
	
	public FieldHolder line; /**	substring(5,9) from input string*/
	public FieldHolder tour;	/**	substring(9,13) from input string*/	
	public FieldHolder dayc;	/**	substring(16,20) from input string*/	
	public FieldHolder deptime;	/**	substring(44,48) from input string*/	
	public FieldHolder nstat;	/**	substring(48,51) from input string*/	
	public FieldHolder tdax;	/**	substring(51,58) from input string*/

	static int MAX=20;

	int highestStationNumber = 0;	//Figure out how many stations for each LineTour series.
	Date startTime = null;  // new Date();
	boolean complete = false;
	
	/**		Specify where each field starts and stops	*/		
	public TixHelper() {
		line = new FieldHolder("line",5,9);
		tour = new FieldHolder("tour",9,13);
		dayc = new FieldHolder("dayc",16,20);
		deptime = new FieldHolder("deptime",44,48);
		nstat = new FieldHolder("nstat",48,51);
		tdax = new FieldHolder("tdax",51,58);
	}


	/**	Locates the fields in a text string
	 * OLD FORMAT: 
16110001000100200052640000000000       11020630000000001111100
16110001000200200052640000000000       11020645000000001111100
16110001000300200052640000000000       11020700000000001111100
	 * 
	 * NEW FORMAT?
GTFS:trips.txt
	 * route_id,	service_id,		trip_id,		trip_headsign,	trip_short_name,	direction_id,	wheelchair_accessible,	shape_id
   ATB:Line:0090-2018-08-13, ATB:Timetable:1600028-2018-08-13,	ATB:VehicleJourney:00900001-2018-08-13, Lundåsen, , 1,		0,
GTFS:stop_times.txt
	trip_id,		stop_id,		stop_sequence,	arrival_time,	departure_time,pickup_type, drop_off_type, shape_dist_traveled, stop_headsign
	ATB:VehicleJourney:00900001-2018-08-13, NSR:Quay:73125, 0, 15:00:00, 15:00:00, 0, 1, ,

	 * 	*/			
	public void getLine(String iObj) {
		String[] parts = iObj.split(",");
		String[] atbLineID = parts[0].split(":");
		String[] lineDate = atbLineID[2].split("-");
		//if ( startTime == null ){ // || complete ){
			line.value = lineDate[0].substring(0, 3);
			tour.value = lineDate[0].substring(4,8);
			//String[] atbTurID = parts[2].split(":");
			//String[] linjeTurId = atbTurID[2].split("-");
			dayc.value = "?????";
			nstat.value = parts[2]; //Always 0? On the first stop?
			deptime.value = parts[4].substring(0, 5); // e.g. 15:00
		//}else{ //if second line or higher, until first line
			tdax.value = iObj.substring(tdax.start-1,tdax.stop-1);
//OLD		
//		line.value = iObj.substring(line.start-1,line.stop-1);
//		tour.value = iObj.substring(tour.start-1,tour.stop-1);
//		dayc.value = iObj.substring(dayc.start-1,dayc.stop-1);
//		deptime.value = iObj.substring(deptime.start-1,deptime.stop-1);
//		nstat.value = iObj.substring(nstat.start-1,nstat.stop-1);
//		tdax.value = iObj.substring(tdax.start-1,tdax.stop-1);
		//return null;
	}//TixHelper getLine()

	/**
		Function to display a instance
	*/		
	public String toString() {
		return "TixHelper("+line+", "+tour+", "+dayc+", "+deptime+", "+nstat+", "+tdax+")";
	}

	/**
		Diaplay each FieldHolder. The start and stop positions and the length are displayed to output.
	*/		
	public void printStatus() {
		System.out.println("TIX v1.1d:");
		System.out.println(line);
		System.out.println(tour);
		System.out.println(dayc);
		System.out.println(deptime);
		System.out.println(nstat);
		System.out.println(tdax);
		System.out.println("MAX:"+MAX);
	}//printStatus

//	public String getNstat() {
//		return nstat.value;
//	}

}//TixHelper v1.1
