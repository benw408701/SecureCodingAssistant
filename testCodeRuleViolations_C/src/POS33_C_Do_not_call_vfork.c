/*
 * POS33_C_Do_not_call_vfork.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

#include <stdio.h>

int main1()
{
	FILE *LOG;

	LOG = fopen("./out.txt", "a+");

	pid_t pid = vfork();
	 if (pid == 0 )  /* child */ {
	   if (execve(LOG, NULL, NULL) == -1) {
	     /* Handle error */
	   }
	   _exit(1);  /* in case execve() fails */
	}
	 return 1;
}


