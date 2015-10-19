#include <stdio.h>
#include <stdlib.h>
#include <wiringPi.h>


int main (int argc, char *argv[]) {

wiringPiSetup();
pinMode(16, OUTPUT);

int length = atoi(argv[1]);

while(1 ) {

digitalWrite(16,1);
delayMicroseconds(length);
digitalWrite(16,0);
delayMicroseconds(6400);

}

return 0;

}
