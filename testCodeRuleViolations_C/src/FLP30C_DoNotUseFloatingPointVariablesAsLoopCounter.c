/*
 * FLP30C_DoNotUseFloatingPointVariablesAsLoopCounter.c
 *
 *
 *      Author: Victor Melnik
 *
 * The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

#include <stdio.h>

void func_FLP30C_NC(void) {

	float y;
	int z = 0;

	for (float x = 0.1f; x <= 1.0f; x += 0.1f) {
    /* Loop may iterate 9 or 10 times */
	}

	//sdfsdfsdfsdf
	for (y = 0.1f; y <= 1.0f; y += 0.1f) {
      /* Loop may iterate 9 or 10 times */
    }

	for (z = 0; z <= 11; z +=1) {
	      /* Loop may iterate 9 or 10 times */
	    }

	y = 10.1;
	while(y < 20)
	{
		printf("Hello %f", y);
	}

	y = 10.2;

	do{
		//do something
		printf("Hello %f", y);
		//asdasd

	}while(y < 20);

	z= 0;
	do{
			//do something
			printf("Hello %d", z);
			//asdasd

		}while(z < 20);
}
