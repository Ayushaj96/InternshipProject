<?php
   require('config.php');
   
   $search   = urldecode($_POST['search']);
   
   $result = mysqli_query($connection,"SELECT * FROM AIS_MACHINES WHERE Address LIKE '%$search%'");

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
	'quantity'=>$row[8],
	'type'=>$row[9],
	'cost'=>$row[10],
	'company'=>$row[11]
	));
	}
	 
	echo json_encode($data);
	 
	mysqli_close($connection);
	 
?>