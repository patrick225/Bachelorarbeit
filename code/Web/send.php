<?php

$IP_SERV = '127.0.0.1';
$PORT_SERV = 55055;
$NET_FILTER = '/^192\.168\.\d{1,3}\.\d{1,3}/';

if (PHP_SAPI == 'cli') {
    $dir = $argv[1];
} else {
    $dir = $_GET['dir'];
}


$dir = explode(' ', $dir);
$motorLeft = 0;
$motorRight = 0;
$shot = false;

// forward
if ($dir[1]) {
    $motorLeft += 100;
    $motorRight += 100;
}

//backward
if ($dir[3]) {
    $motorLeft -= 100;
    $motorRight -= 100;
}

//left
if ($dir[0]) {
    $motorLeft *= 0.7;
}

//right
if ($dir[2]) {
    $motorRight = $motorRight * 0.7;
}

//shot
if ($dir[4]) {
    $shot = true;
}

//filter ip
$ip = null;
if (preg_match($NET_FILTER, $_SERVER['REMOTE_ADDR'])) {
    $ip = $_SERVER['REMOTE_ADDR'];
} else {

    return;
}

$result = array('motorLeft' => $motorLeft,
                'motorRight' => $motorRight,
                'shot' => $shot,
                'ip' => $ip
                );

$resultJson = json_encode($result);
$sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);

socket_sendto($sock, $resultJson, strlen($resultJson), 0, $IP_SERV, $PORT_SERV);
socket_close($sock);





