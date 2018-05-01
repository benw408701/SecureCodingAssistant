/*
 * TESTNodeProcessor.c
 *
 *  Created on: Feb 26, 2018
 *      Author: Victor Melnik
 */

#include <stdlib.h>
#include <limits.h>
#include <signal.h>
#include <string.h>
#include <stdatomic.h>
#include <ctype.h>
#include <stdio.h>
#include <wchar.h>
#include <stddef.h>


int ii1 = 10;         /* Definition, external linkage */
static int i2 = 20;  /* Definition, internal linkage */
extern int ii3 = 30;  /* Definition, external linkage */
int i4;              /* Tentative definition, external linkage */
static int i5;       /* Tentative definition, internal linkage */


enum { BUFFER_SIZE = 32 };

//******************************************************************************************************************************
#ifdef __STDC_NO_ATOMICS__
#error "Atomics are not supported"
#elif ATOMIC_INT_LOCK_FREE == 0
#error "int is never lock-free"
#endif

enum { MAX_MSG_SIZE = 24 };
char *err_msg;
char **err_msgT;
int sigFlag = 0;
int sigFlagTemp;
//atomic_int test;
volatile sig_atomic_t testT = 0;
int array[10];

void handler_TEST(int signum) {
  strcpy(err_msg, "SIGINT encountered.");
  //sigFlag = 1;
	//testT = 1;
}

//*******************************************************************************************************************************
//NON compliant
int f_MEM31C_TEST_NC(void) {
  char *text_buffer = (char *)malloc(BUFFER_SIZE);
  char **text_bufferT = (char **)malloc(BUFFER_SIZE); //false positive for EXP34_C
  char * text_bufferTT;

  text_bufferTT = (char *)malloc(BUFFER_SIZE); //false positive for EXP34_C

  if (text_buffer == NULL) {
    return -1;
  }

  free(text_bufferTT);
  return 0;
}

//**********************************************************************************************************************************
size_t count_preceding_whitespace_TEST(const char *s) {
  const char *t = s;
  char tt[] = "abc";
  unsigned char ttt = "a";
  size_t length = strlen(s) + 1;
  while (isspace(*t) && (t - s < length)) {
    ++t;
  }
  tolower(*t);
  tolower(tt[0]);
  tolower(ttt);
  return t - s;
}
//**********************************************************************************************************************************
void funcINT33_TEST_DivNC(signed long s_a, signed long s_b) {
  signed long result;
  if ((s_a == LONG_MIN) && (s_b == -1)) {
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
	  result = s_a / s_b;
  }

}
//**********************************************************************************************************************************

void func_EXP32C_TEST(void) {
  static volatile int **ipp;
  static int *ip;
  static int *ipT;
  static volatile int i = 0;

  static volatile int **ippT = &ipT;
  int a, b;

  a = 15;
  //printf(" = %d\n",a);

  printf("i = %d.\n", i);

  ipp = &ip; /* May produce a warning diagnostic */
  ipp = (int**) &ip; /* Constraint violation; may produce a warning diagnostic */
  *ipp = &i; /* Valid */

  if (*ip != 0) { /* Valid */
    /* ... */
  }
}

//**********************************************************************************************************************************
void func_DCL41CNC_TEST(int expr) {
  switch (expr) {
    int i = 4;
    double j = 5.6;
    float k = 34.7;
   // f_DCL41C(i);
  case 0:
    i = 17;

    break;
  default:
    printf("%d\n", i);
  }
}

//**********************************************************************************************************************************
struct flexArrayStruct {
  int num;
  int data[1];
};

struct flexArrayStructComplaint{
	int num;
	  int data[];
};
//**********************************************************************************************************************************
void open_some_file_TEST(const char *file) {
  FILE *f = fopen(file, "r");
  if (NULL != f) {
    /* File exists, handle error */
  } else {
    if (fclose(f) == EOF) {
      /* Handle error */
    }
    f = fopen(file, "w");
    if (NULL == f) {
      /* Handle error */
    }

    /* Write to file */
    if (fclose(f) == EOF) {
      /* Handle error */
    }
  }
}
//***************************************************************************************************************************************

void func_FLP30C_NC_TEST(void) {

	float y;

	for (float x = 0.1f; x <= 1.0f; x += 0.1f) {
    /* Loop may iterate 9 or 10 times */
	}

	//sdfsdfsdfsdf
	for (y = 0.1f; y <= 1.0f; y += 0.1f) {
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
}

//***************************************************************************************************************************************
void func_38C_NC_TEST(void) {
  wchar_t wide_str1[]  = L"0123456789";
  wchar_t wide_str2[] =  L"0000000000";

  strncpy(wide_str2, wide_str1, 10);
  wcsncpy(wide_str1, wide_str2, 10);
}

void func_38C_NCTwo(void) {
  char narrow_str1[] = "01234567890123456789";
  char narrow_str2[] = "0000000000";

  wcsncpy(narrow_str2, narrow_str1, 10);
  strncpy(narrow_str2, narrow_str1, 10);
}

//****************************************************************************************************************************************
int main_test(void) {
  /* ... */
  return 0;
}

