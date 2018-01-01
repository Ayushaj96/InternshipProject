<?php

require('config.php');

if($_SERVER['REQUEST_METHOD']=='POST'){

 $machine_id= $_POST['machine_id'];
 $company_no = $_POST['company_no'];
 $quantity = $_POST['quantity'];
 
 $result = mysql_query("SELECT Company1Quantity FROM AIS_MACHINES WHERE MachineSerialNo ='$machine_id' LIMIT 1");
 echo '$result' ;
 
 if($company_no == '1'){
     $Sql_Query1 = "UPDATE AIS_MACHINES SET Company1Quantity='$quantity' WHERE MachineSerialNo='$machine_id' " ;
     if(mysqli_query($connection,$Sql_Query1)){
     echo 'Machine Table Updated';
     }else{
     echo 'Something went wrong';
     }
 }else{
     $Sql_Query2 = "UPDATE AIS_MACHINES SET Company2Quantity='$quantity' WHERE MachineSerialNo='$machine_id' " ;
     if(mysqli_query($connection,$Sql_Query2)){
     echo 'Machine Table Updated';
     }else{
     echo 'Something went wrong';
     }
 }
 
}else{
    echo 'No POST' ;
}
 mysqli_close($connection);
?>