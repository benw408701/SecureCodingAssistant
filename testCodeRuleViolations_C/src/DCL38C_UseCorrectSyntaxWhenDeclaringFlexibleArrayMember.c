/*
 * DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember.c
 *
 *
 *      Author: Victor Melnik
 *
 * The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */


#include <stdlib.h>

struct flexArrayStruct {
  int num;
  int data[1];
};

struct flexArrayStructComplaint{
	int num;
	  int data[];
};


void func_DCL38CNC(size_t array_size) {
  /* Space is allocated for the struct */
  struct flexArrayStruct *structP
    = (struct flexArrayStruct *)
     malloc(sizeof(struct flexArrayStruct)
          + sizeof(int) * (array_size - 1));
  if (structP == NULL) {
    /* Handle malloc failure */
  }

  structP->num = array_size;

  /*
   * Access data[] as if it had been allocated
   * as data[array_size].
   */
  for (size_t i = 0; i < array_size; ++i) {
    structP->data[i] = 1;
  }
}
