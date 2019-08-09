<?php 
header('Content-Type: text/html; charset=utf-8');
?>
<!DOCTYPE html>
<html lang="nb" xml:lang="nb">
	<head>
		<meta charset="UTF-8" />
		<meta name="author" content="Rune M. Andersen" />
		<meta name="description" content="Opplasting av ruteinformasjon i REGTOPP-format" />
		<meta name="keywords" content="regtopp atb rutetider buss bussinformasjon rutetabell ruteplan" />
    	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href='http://fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
		<script type="text/javascript" src="./uploadify/jquery.uploadify-3.1.js"></script>
		<script type="text/javascript" src="./jsrender.js"></script>
		<link rel="stylesheet" type="text/css" href="./uploadify/uploadify.css" />
		<script type="text/javascript" src="./script.js"></script>
		<link href='style.css' rel='stylesheet' type='text/css'>
		<meta name=viewport content="width=device-width, initial-scale=1">
		<title>AtB REGTOPP</title>
	</head>
	<body>
		<div id="header">
			<h1>AtB REGTOPP</h1>
		</div>

<?php 
$data_selected = 'selected';
$lastopp_selected = '';
if(array_key_exists('p',$_GET) && ($_GET['p'] == 'lastopp')){
	$data_selected = '';
	$lastopp_selected = 'selected';		
}	
?>
		<div id="nav">
			<ul>
				<li id="data" class="<?= $data_selected ?>"><a href=".">Rutedata</a></li>
				<li id="lastopp" class="<?= $lastopp_selected ?>"><a href="?p=lastopp">Last opp</a></li>
			</ul>
		</div>

		<div id="page">

			<?php
				
if($lastopp_selected){
	require_once('config.php');	
	require_once('lastopp.php');
}
else{
	require_once('config.php');	
	require_once('data.php');
}
?>
		</div>


	</body>


</html>


