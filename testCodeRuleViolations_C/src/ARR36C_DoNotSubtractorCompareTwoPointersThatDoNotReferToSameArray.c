/*
 * ARR36C_DoNotSubtractorCompareTwoPointersThatDoNotReferToSameArray.c
 *
 *
 *      Author: Victor Melnik
 *
 *
 * The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

#include <stddef.h>

enum { SIZE = 32 };

//non-compliant example from CERT
void func_ARR36_NC(void) {
  int nums[SIZE];
  int numsS[] = {0,1,2,3};
  int end = 234;
  int *next_num_ptr = nums;
  int *next_num_ptrS;

  next_num_ptrS = numsS;
  size_t free_elements;

  if(&nums[SIZE] <= next_num_ptr)
  {
	  //sdsdsd
  }
  else if(&nums[SIZE] >= next_num_ptrS) //VIOLATED
  {
	  //sdsdsd
  }
  /* Increment next_num_ptr as array fills */

  free_elements = next_num_ptr - &end; //VIOLATED
  free_elements = &end - next_num_ptr; //VIOLATED
  free_elements = next_num_ptr - &end; //VIOLATED

  start = end - end;
}


//complaint example from CERT
void func_ARR36_C(void) {
  int nums[SIZE];
  int *next_num_ptr = nums;
 // int ** next_num_dptr = new int*[SIZE];
  size_t free_elements;

  /* Increment next_num_ptr as array fills */

  free_elements = &(nums[SIZE]) - next_num_ptr;
}


