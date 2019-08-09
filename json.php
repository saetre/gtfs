<?php

require_once('zipper.php');
if(isset($_GET['callback']))
		$callback = trim($_GET['callback']);

// Collect files and sort them
$datafolder = 'data';
$dir = opendir($datafolder);
$files = array();
while($file = readdir($dir)){
	if(!is_dir($file) && pathinfo($file, PATHINFO_EXTENSION) == 'zip')
		$files[] = $file;
}
asort($files);

// Fetch data from files
$lastUpdate = 0;
$json = array();
$datasets = array();
foreach($files as $file){
	$path = $datafolder.'/'.$file;
	$tool = new zipTools($path);
	$valid = $tool->getValidityPeriod();
	$ffiles = $tool->getFiles();
	ksort($ffiles);
	$lastUpdate = max(filemtime($path), $lastUpdate);
	
	$datasets[] = array(
		'validFrom' => date('Y-m-d', $valid['fromTime']),
		'validTo' => date('Y-m-d', $valid['toTime']),
		'lastUpdated' => date('c', filemtime($path)),
		'url' => $path
		//			'url' => $_SERVER['SCRIPT_URI'].$path
	);
}

// Set attributes
$json['requestStatus'] = 'ok';
$json['lastUpdated'] = date('c', $lastUpdate);
$json['datasets'] = $datasets;
$json = json_encode($json);

// Output resutl
if(isset($callback) && $callback && $callback != ''){
	header("Content-Type: text/javascript; charset=UTF-8");
	echo $callback.'('.$json.')';
} else {
	header("Content-Type: application/json; charset=UTF-8");
	echo $json;
}

