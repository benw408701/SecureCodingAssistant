/*
 * SIG30C_CallOnlyAsynchronousSafeFuntionsWithinSignalHandlers.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

#include <setjmp.h>
#include <signal.h>
#include <stdlib.h>

enum { MAXLINE = 1024 };
static jmp_buf env;
int tempPP;

void handlerFunc(int signum)
{
	//do nothing
	handlerFunc1(signum);
}

void handlerFunc1(int signum)
{
	printf("Inside handler");
	//tempPP = 1;
}

void handlerFunc2(int signum)
{
	//do nothing
	handlerFunc1(signum);
	printf("Inside handler");
}

void handler1(int signum) {
  ///longjmp(env, 1);

	//printf("Inside handler");
	handlerFunc(signum);
	//handlerFunc1(signum);
	//abort();
}

void log_message(char *info1, char *info2) {
  static char *buf = NULL;
  static size_t bufsize;
  char buf0[MAXLINE];

  if (buf == NULL) {
    buf = buf0;
    bufsize = sizeof(buf0);
  }

  /*
   * Try to fit a message into buf, else reallocate
   * it on the heap and then log the message.
   */

  /* Program is vulnerable if SIGINT is raised here */

  if (buf == buf0) {
    buf = NULL;
  }
}

int main_SIG30NC(void) {
  if (signal(SIGINT, handler1) == SIG_ERR) {
    /* Handle error */
  }

  if (signal(SIGINT, handlerFunc1) == SIG_ERR) {
     /* Handle error */
   }
  char *info1;
  char *info2;

  /* info1 and info2 are set by user input here */

  if (setjmp(env) == 0) {
    while (1) {
      /* Main loop program code */
      log_message(info1, info2);
      /* More program code */
    }
  } else {
    log_message(info1, info2);
  }

  return 0;
}


