<?php
date_default_timezone_set("Asia/Calcutta");

$connection = mysqli_connect("localhost", "id4067669_aisandroid","aisandroid" , 'id4067669_aisandroid_db');

if(!$connection){
 	    die("Database Connection Failed" . mysqli_error());
 	}
	    
    // $select_db = mysqli_select_db('id4067669_aisandroid_db');
    /* 
     if(!$select_db){
 	    die("Database Selection Failed" . mysqli_error());
 	}	 */
?>