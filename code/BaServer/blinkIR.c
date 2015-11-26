#include <stdio.h>
#include <stdlib.h>
#include <wiringPi.h>


int main (int argc, char *argv[]) {

wiringPiSetup();

int pin = atoi(argv[2]);
pinMode(pin, OUTPUT);

int length = atoi(argv[1]);

while(1 ) {

digitalWrite(pin,1);
delayMicroseconds(length);
digitalWrite(pin,0);
delayMicroseconds(6400);

}

return 0;

}
