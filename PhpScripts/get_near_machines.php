<?php
    require('config.php');
   
	$lat   = urldecode($_POST['lat']);
    $lang   = urldecode($_POST['lang']);
    $km  = urldecode($_POST['km']);
	
	$minLat = $lat - ($km*0.0090) ;
	$maxLat = $lat + ($km*0.0090) ;
	
	$minLang = $lang - ($km*0.0117) ;
	$maxLang = $lang + ($km*0.0117) ;
   
    $result = mysqli_query($connection,"SELECT * FROM AIS_MACHINES WHERE (Latitude BETWEEN $minLat AND $maxLat) AND (Longitude BETWEEN $minLang AND $maxLang) ");

	$data = array();
	 
	while($row = mysqli_fetch_array($result)){
	array_push($data,
	array('latitude'=>$row[1],
	'longitude'=>$row[2],
	'address'=>$row[3],
	'address_tags'=>$row[4],
	'machine_serial_no'=>$row[5],
	'access'=>$row[6],
	'status'=>$row[7],
	'company1'=>$row[8],
	'company1quantity'=>$row[9],
	'company2'=>$row[10],
	'company2quantity'=>$row[11],
	'type'=>$row[12]
	));
	}
	 
	echo json_encode($data);
	 
	mysqli_close($connection);
	 
?>