/*
 * EXP32C_DoNotAccessVolatileObjectThroughNonvolatileRefernece.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */


#include <stdio.h>

void func_EXP32C(void) {
  static volatile int **ipp;
  static int **ITTT;
  static int *ip;
  static int *ipT;
  static volatile int i = 0;
  int *restrict bXDCS;

  static volatile int **ippT = &ipT;
  ippT = &ip;
  int a, b;
  int aa[34];
  a = 15;
  //printf(" = %d\n",a);

  printf("i = %d.\n", i);

  ipp = &ip; /* May produce a warning diagnostic */
  ipp = (int**) &ip; /* Constraint violation; may produce a warning diagnostic */
  *ipp = &i; /* Valid */
  ITTT = &ip;
  ip = &i;


  i = i/2 * 10;
  i *= 2;

  if (*ip != 0) { /* Valid */
    /* ... */
  }
}
