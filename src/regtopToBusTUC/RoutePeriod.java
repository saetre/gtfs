package regtopToBusTUC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoutePeriod implements Comparable<RoutePeriod>{
	
	private String route;
	private Date date;

	RoutePeriod( String route ){
		setRoute( route );
	}


	public void setRoute( String route ){
		this.route = route;
		setData();
	}

	private void setData(){
		String date = this.route.substring( 59, 69 );
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyy,MM,dd" );
		try {
			this.date = formatter.parse(date);
		} catch (ParseException e) {
			System.out.println("ParseException: "+e);
		}
	}

	public Date getDate() {
		return this.date;
	}


	public String getRoute() {
		return this.route;
	}


	/** Compare to sort on descending period end-dates */
	@Override
	public int compareTo( RoutePeriod o ){
		if( this.getDate() == null || o.getDate() == null ){
			return 0;
		}
		return (int) this.getDate().compareTo(o.getDate() );
	}//compareTo (another RoutePeriod)

}//class RoutePeriod
