/*
 * Test123.c
 *
 *  Created on: Feb 26, 2018
 *      Author: Victor Melnik
 */
#include <stdio.h>
#include <stddef.h>
#include <stdatomic.h>
#include <stdbool.h>

  //int nums[SIZE];
int nums[20];
  int numsS[] = {0,1,2,3};
  int end;
  int *next_num_ptr = nums;
  int *next_num_ptrS;

  int a1, b2, c3;
  int a, b, c = 0;
  int *aa, *bb, *cc = NULL;
  int *aax, *bbx, *ccx;
  int **aaaax, **bbbbx, **ccccx;
  int **aaaa, **bbbb, **cccc = NULL;
  int aaa[] = {0, 1, 2, 3};

  const int XYZ = 0;

  volatile int vBM = 1;

  int *restrict bXDCS;

  extern void f_DCL41C(int i);

  FILE *f;

  union {
      int i_u;
      float f_u;
      struct {
          unsigned int u_u;
          double d_u;
      } s_u;
  } u__u;

  struct flexArrayStructComplaint{
  	int numSS;
  	  int dataSS[];
  };

  static atomic_bool flagTTT;


  int function1()
  {
	  int kkas = 0;

	  return kkas;
  }

