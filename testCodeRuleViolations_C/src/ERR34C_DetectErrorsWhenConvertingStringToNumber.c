/*
 * ERR34C_DetectErrorsWhenConvertingStringToNumber.c
 *
 *      Author: Victor Melnik
 *
 * The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */
#include <stdlib.h>
#include <stdio.h>

void func_first(const char *buff) {
  int si;

  if (buff) {
    si = atoi(buff);
  } else {
    /* Handle error */
  }
}

void function_c(const char *buff) {
  int matches;
  int si;

  if (buff) {
    matches = sscanf(buff, "%d", &si);
    if (matches != 1) {
      /* Handle error */
    }
  } else {
    /* Handle error */
  }
 }


