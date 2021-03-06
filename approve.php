<?php

error_reporting(E_ALL);
ini_set('display_errors', '1');

require_once('config.php');
require_once('zipper.php');

// Hente navnet på fila vi skal jobbe med
$filename = trim(isset($_GET['f']) ? $_GET['f'] : false);
$overwrite = isset($_GET['o']) ? $_GET['o'] : 0;
$status = 'UNKNOWN';

// Prøve å finne en matchende fil i mappa
$pathFrom = $_SERVER['DOCUMENT_ROOT'] . $regtopp_uploads;
$pathTo   = $_SERVER['DOCUMENT_ROOT'] . $regtopp_data;
// Starter med null resultater og går gjennom alt i mappa
$filepathFrom = false;
$filepathTo = false;
if($filename){
	$dir = opendir($pathFrom);
	while ($file = readdir($dir)){
		if($filename == $file){
			$filepathFrom = $pathFrom.'/'.$filename;
		}
	}
	closedir($dir);
	
	// Hvis fila som skal flyttes finnes
	if(file_exists($filepathFrom)){
		
		// Åpne ZIP for å hente ut data som trengs til flytting av fila
		//			$zip = zip_open($filepathFrom);
		$zip = new zipTools($filepathFrom);
		
		if($zip->error){
			// Fila finnes, men er ikke gyldig ZIP
			$status = 'FILE-NOT-ZIP';
		} else if($zip){
			// Fila funnet og kan prosesseres
			//				$validityPeriod = getValidityPeriod($filepathFrom);
			//				$admCode = getAdmCodeAndSerial($filepathFrom);
			$validityPeriod = $zip->getValidityPeriod();
			$admCode = $zip->getAdmCodeAndSerial();
			// Setter filnavnet hvis vi har nok data
			if($admCode && is_array($validityPeriod))
				$newFile = '/atb-'.date('Ymd', $validityPeriod['fromTime']).'-'.date('Ymd', $validityPeriod['toTime']).'.zip';
			$filepathTo = $pathTo . '/atb-'.date('Ymd', $validityPeriod['fromTime']).'-'.date('Ymd', $validityPeriod['toTime']).'.zip';
			//				zip_close($zip);
		}
		
		
		// Hvis destinasjonsnavnet er satt
		if($filepathTo){
			// Hvis det finnes en godkjent fil fra før (som risikerer overskriving)
			if(!$overwrite && file_exists($filepathTo)){
				$status = 'FILE-EXISTS';
			} else {
				rename($filepathFrom, $filepathTo);
				// Hvis fila blir liggende og ingen ny oppstår
				if(file_exists($filepathFrom) || !file_exists($filepathTo))
					$status = 'MOVE-FAILED';
				// Hvis fila forsvinner fra opplastet mappe, og havner i godkjent mappe
				else if(!file_exists($filepathFrom) && file_exists($filepathTo))
					$status = 'SUCCESS';
				// Hvis halvveis flytting av fil
				else
					$status = 'MOVE-PARTLY-FAILED';
			}
			// Hvis destinasjonsnavnet mangler
		} else {
			$status = 'DESTINATION-UNKNOWN';
		}
		
		// Hvis fila ikke finnes
	} else {
		$status = 'FILE-NOT-FOUND';
	}
	
	//Hvis filnavnet ikke finnes i GET-arrayet
} else {
	$status = 'FILENAME-MISSING';
}

// Send ut resultatet
header("Content-Type: application/json; charset=UTF-8");
echo json_encode(array('status' => $status, 'file' => $newFile));

exit;