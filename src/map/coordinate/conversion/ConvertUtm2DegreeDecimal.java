/**
 * Convert BussTUCs UTM coordinates to decimal degree latitude/longitude coordinates
 */
package map.coordinate.conversion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.CharBuffer;

import com.ibm.util.CoordinateConversion;

/**
 * @author Rune Sætre
 * @since 2012.07.26
 */
@SuppressWarnings("unused")
public class ConvertUtm2DegreeDecimal {

	private static final String INFILE = "src/map/coordinate/conversion/stations.txt";
	private static final String OUTFILE = "src/map/coordinate/conversion/latLngName.txt";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CoordinateConversion cc = new CoordinateConversion();

		BufferedReader br = null;
		BufferedWriter out = null;
		try {
				br = new BufferedReader( new InputStreamReader( new FileInputStream ( INFILE ), "ISO-8859-15" ) );
				out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( OUTFILE ), "UTF-8") );
		} catch (FileNotFoundException e) {
			System.err.println( " In: "+INFILE+"\nOut: "+OUTFILE );
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String tekst = null;
		try {
			if (br != null){
				while ( (tekst = br.readLine()) != null){
					String[] nameLatLng = tekst.split("\t");
					// format: 34 G 683473 4942631
					String utm = "32 W "+nameLatLng[1]+" "+nameLatLng[2];
					double[] latLng = cc.utm2LatLon(utm);
					out.write( latLng[0]+"\t"+latLng[1]+"\t"+nameLatLng[0]+"\n" );
				}
				br.close();
			}
			if (out != null) out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}//class CoordinateConversion:main
