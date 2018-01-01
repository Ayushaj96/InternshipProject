<?php

require('config.php');

if($_SERVER['REQUEST_METHOD']=='POST'){

 $f_name = $_POST['full_name'];
 $mobile = $_POST['mobile'];
 $email = $_POST['email'];
 $username = $_POST['username'];
 $password = $_POST['password'];
 $dob = $_POST['dob'];
 $profession = $_POST['profession'];

 $CheckSQL = "SELECT * FROM UserDetails WHERE Email='$email'";
 
 $check = mysqli_fetch_array(mysqli_query($connection,$CheckSQL));
 
 if(isset($check)){

 echo 'Email Already Exist';

 }else{ 
$Sql_Query = "INSERT INTO UserDetails (FullName,Email,Username,MobileNo,Password,DOB,Profession) values ('$f_name','$email','$username','$mobile','$password','$dob','$profession')";

 if(mysqli_query($connection,$Sql_Query))
{
 echo 'Registration Successfully';
}
else
{
 echo 'Something went wrong';
 }
 }
}else{
    echo 'No POST' ;
}
 mysqli_close($connection);
?>