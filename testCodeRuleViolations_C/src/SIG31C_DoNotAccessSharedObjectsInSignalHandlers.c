/*
 * SIG31C_DoNotAccessSharedObjectsInSignalHandlers.c
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

#include <signal.h>
#include <stdlib.h>
#include <string.h>
#include <stdatomic.h>

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
atomic_int test;
volatile sig_atomic_t testT = 0;
int array[10];

void handler(int signum) {
  //strcpy(err_msg, "SIGINT encountered.");
	sigFlag = 1;
	//testT = 1;
	test = 1;
}

int main_SIG31_NC(void) {
  signal(SIGINT, handler);

  err_msg = (char *)malloc(MAX_MSG_SIZE);
  if (err_msg == NULL) {
    /* Handle error */
  }
  strcpy(err_msg, "No errors yet.");
  /* Main code loop */
  free(err_msg);
  return 0;
}


