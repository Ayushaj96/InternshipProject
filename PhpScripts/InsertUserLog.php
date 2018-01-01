<?php

require('config.php');

if($_SERVER['REQUEST_METHOD']=='POST'){

 $user_id = $_POST['user_id'];
 $trans_id = $_POST['trans_id'];
 $quality = $_POST['quality'];
 $quantity = $_POST['quantity'];
 $cost = $_POST['cost'];
 $company = $_POST['company'];
 $trans_start_time = $_POST['trans_start_time'];
 $trans_mode = $_POST['trans_mode'];
 $trans_status = $_POST['trans_status'];
 $trans_end_time = $_POST['trans_end_time'];
 $encrypted_code = $_POST['encrypted_code'];
 $machine_id = $_POST['machine_id'];

$Sql_Query = "INSERT INTO UserTransactionLog (UserId,TransId,Quality,Quantity,Cost,Company,TransStartTime,TransMode,TransStatus,TransEndTime,EncryptedCode,MachineId) values ('$user_id','$trans_id','$quality','$quantity','$cost','$company',
            '$trans_start_time','$trans_mode','$trans_status','$trans_end_time','$encrypted_code','$machine_id')";

 if(mysqli_query($connection,$Sql_Query))
{
 echo 'User Log Inserted';
}
else
{
 echo 'Something went wrong';
 }
 
}else{
    echo 'No POST' ;
}
 mysqli_close($connection);
?>