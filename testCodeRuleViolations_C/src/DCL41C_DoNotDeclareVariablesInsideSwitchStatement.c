/*
 * DCL41C_DoNotDeclareVariablesInsideSwitchStatement.c
 *
 *
 *      Author: Victor Melnik
 *
 * The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */


#include <stdio.h>

extern void f_DCL41C(int i);

void func_DCL41CNC(int expr) {
  switch (expr) {
    int i = 4;
    double j = 5.6;
    float k = 34.7;
    f_DCL41C(i);
  case 0:
    i = 17;

    break;
  default:
    printf("%d\n", i);
  }
}
