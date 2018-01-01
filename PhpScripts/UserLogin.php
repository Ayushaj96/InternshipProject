<?php

    require("config.php");

 if($_SERVER['REQUEST_METHOD']=='POST'){

 $email = $_POST['email'];
 $password = $_POST['password'];
 
 $Sql_Query = "select * from UserDetails where Email = '$email' and Password = '$password' ";
 
 $check = mysqli_fetch_array(mysqli_query($connection,$Sql_Query));
 
 if(isset($check)){
 echo "Login Success";
 
 $result = mysqli_query($connection , "select * from UserDetails where Email = '$email' and Password = '$password' ");
 
 $data = array();
	 
	while($row = mysqli_fetch_array($result)){
	array_push($data,
	array('full_name'=>$row[1],
	'email'=>$row[2],
	'username'=>$row[3],
	'mobile'=>$row[4],
	'dob'=>$row[6],
	'profession'=>$row[7]
	));
	}
echo json_encode($data);
 
 }
 else{
 echo "Invalid Username or Password Please Try Again";
 }
 
 }else{
 echo "Check Again";
 }
mysqli_close($connection);

?>