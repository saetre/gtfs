<?php
// Dump ut JSON-svaret nederst
header("Content-Type: application/json; charset=UTF-8");
require_once('config.php');
require_once('zipper.php');

$file = trim( isset($_GET['f'] ) ? $_GET['f'] : false );
$comment = trim( isset( $_GET['comment'] ) ? $_GET['comment'] : "" );
$comment = urlencode( $comment );

$filename = false;
if(isset($file))
	$filename = $_SERVER['DOCUMENT_ROOT'] . $regtopp_data . $file;


// for use in topreg.pl and version.pl
if( isset( $filename ) ){
	$zip = new zipTools($filename);
	$validityPeriod = $zip->getValidityPeriod();

	//	format time in java
	$from = $validityPeriod['fromTime'];
	$to = $validityPeriod['toTime'];

	//	adm-code
	$admCode = $zip->getAdmCodeAndSerial();
	
	// unzip the validated uploaded zip file
	if( $zip->extractTo( $data_input ) ){
		$convert = 'OK';
	}else{
		$convert = false;
	}
}

if( $convert ){
	exec("$compile_java"); //Execute this as the busstuc/www-data user in /var/www/regtopp
	//exec("$compile_java"); //run as busstuc user on server: javac -d /var/www/regtopp/bin/ -encoding UTF-8 /var/www/regtopp/src/regtopToBusTUC/*.java
	$module = strtolower($admCode)."_".date('ymd', $from);
	exec ( "java -classpath '$javaClasspath' '$javaConverter' '$data_input' '$data_output'", $output1 );
	error_log(" Rune was Here! "+"java -classpath '$javaClasspath' '$javaConverter' '$data_input' '$data_output'" );
	exec ( "echo Rune was Here... "+"java -classpath '$javaClasspath' '$javaConverter' '$data_input' '$data_output'" );
	
	exec( "java -classpath '$javaClasspath' '$javaTopRegUpdater' '$comment' '$admCode' '$from' '$to' '$routePeriod'",$output2);
	
	exec( "java -classpath '$javaClasspath' '$javaVersionUpdater' '$comment' '$versionFile'", $output3);
	
	exec( "rm -r '$data_input'"); //clean up input-files?
	// exec( "chmod g+rx '$regcut'" ); //OBSOLETE? RS-151216. Moved to prolog compile-code? Just make sure busstuc.pl is executed
	exec( "'$regcut' '$module'"); //Slow WHILE TESTING?! Regenerates busestuc4.sav

	exec( "chgrp -R busstuc '$data_output' 2> /dev/null" );
	
	exec( "chmod -R g+w '$data_output' 2> /dev/null" );
	
}else{
	$convert = 'FAIL';
}

// Dump ut JSON-svaret
echo json_encode( array('convert' => $convert) );
exit;

?>
