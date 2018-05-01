/*
 * INT33C_EnsureDivisionAndRemainderDoNoResultDividebyZeroError.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */


#include <limits.h>

void funcINT33_DivNC(signed long s_a, signed long s_b) {
  signed long result;
  if ((s_a = LONG_MIN) && (s_b == -1)) {
    /* Handle error */
  }
  else if(s_a == 8)
  {
	  //do nothing
  }
  else {
    result = s_a / s_b;
    result = s_a + s_b;
  }
  /* ... */

  if(s_b != 0)
  {
	  result = s_a + s_a/ s_b;
  }

  s_b /= s_a;
  s_a %= s_b;
  s_b /= s_b;
  if(12 > 12)
  {

  }
  else if(12 < 12)
  {

  }
  else if(12 >= 12)
  {

  }
  else if(12 <= 12)
  {

  }
}

void funcINT33_ModNC(signed long s_a, signed long s_b) {
  signed long result;
  if ((s_a == LONG_MIN) && (s_b == -1)) {
    /* Handle error */
  } else if (s_b != 0){
    result = s_a % s_b;
  }

  result = s_b % s_a;

  result = s_b / 0;
  result = s_b /20;
  /* ... */
}
