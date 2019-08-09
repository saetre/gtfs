<?php
//validated zips
$regtopp_data = '/regtopp/data';
//regtopp upload:
$data_input = $_SERVER['DOCUMENT_ROOT'].$regtopp_data.'/temp';
//$data_output = $_SERVER['DOCUMENT_ROOT'].$regtopp_data.'/';
$data_output = '/home/busstuc/busstuc/db/tables/';
$datafolder = 'data';
//new regtopp zips
$regtopp_uploads = '/regtopp/uploads';

//Compile java classes (Execute this as the busstuc/www-data user in /var/www/regtopp)
$javaClasspath = '/var/www/regtopp/bin/';
$compile_java = "javac -d $javaClasspath -encoding UTF-8 /var/www/regtopp/src/regtopToBusTUC/*.java";

//ConvertRegTop.java
$javaConverter = 'regtopToBusTUC.ConvertRegTop';

//TopRegUpdate.java
$javaTopRegUpdater = 'regtopToBusTUC.TopRegUpdate';
$routePeriod = '/home/busstuc/busstuc/db/route_period.pl';

//VersionUpdate.java
$javaVersionUpdater = 'regtopToBusTUC.VersionUpdate';
$versionFile = '/home/busstuc/busstuc/version.pl';

//extract_cut.sh
$regcut = '/home/busstuc/busstuc/compile/extract_cut.sh';

?>
	