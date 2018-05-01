/*
 * MEM31C_FreeDynamicallyAllocatedMemoryWhenNoLongerNeeded.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

//Example code from CERT website

#include <stdlib.h>
#include<stdio.h>

enum { BUFFER_SIZE = 32 };

//NON compliant
int f_MEM31C_NC(void) {
  char *text_buffer = (char *)malloc(BUFFER_SIZE); //RULE VIOLATION
  char **text_bufferT = (char **)malloc(BUFFER_SIZE);//RULE VIOLATION
  char * text_bufferTT;

  text_bufferTT = (char *)malloc(BUFFER_SIZE);

  if (text_buffer == NULL) {
    return -1;
  }

  free(text_bufferTT);
  free(text_bufferT);
  return 0;
}

//Compliant
int f_MEM31_CC(void) {
  char *text_buffer = (char *)malloc(BUFFER_SIZE);
  if (text_buffer == NULL) {
    return -1;
  }

  free(text_buffer);
  return 0;
}

int main_MEM31_C_CompliantRealloc (void)
{
int * p1, * p2, *p3, *p4;
p1 = (int *) calloc (5, sizeof (int)); /* number of elements in an array of type 5 int */

p2 = (int *) realloc (p1, sizeof (int));

p3 = (int *) calloc (5, sizeof (int));
p4 = (int *) realloc (p1, sizeof (int)); //RULE VIOLATION
free(p3);

if (p2 == NULL)
{
free (p1);
return 0;
}
p1 = NULL;
free (p2);
return 0;
}




typedef struct rec
{
		int i;
		float PI;
		char A;
}RECORD;

int mainasdsdad()
{
		RECORD *ptr_one;

		ptr_one = (RECORD *) malloc (sizeof(RECORD));

		(*ptr_one).i = 10;
		(*ptr_one).PI = 3.14;
		(*ptr_one).A = 'a';

		printf("First value: %f\n",(*ptr_one).i);
		printf("Second value: %f\n", (*ptr_one).PI);
		printf("Third value: %c\n", (*ptr_one).A);

		//free(ptr_one);

		return 0;
}
