/*
 * CON40C_DoNotReferToAtomicVariableTwiceinExpression.c
 *
 *      Author: Victor Melnik
 *
 *
 * The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */


#include <stdatomic.h>
#include <stdbool.h>

static atomic_bool flag = ATOMIC_VAR_INIT(false);

void init_flag(void) {
  atomic_init(&flag, false);
}

void toggle_flag(void) {
  bool temp_flag = atomic_load(&flag);

  temp_flag = !temp_flag;

  atomic_store(&flag, temp_flag);
}


static atomic_bool flagTTT;
int j;

bool get_flag(void) {
  return atomic_load(&flag);
}

//Non-compliant example 2
//********************************************

atomic_int n = ATOMIC_VAR_INIT(0);

int compute_sum(void) {
	n++;
	n--;
	int tempInt = atomic_load(&n);
	int tempIntt;
	tempIntt = atomic_load(&n);

	tempIntt++;
	tempInt++;

  return n * (n + 1) / 2; //VIOLATED RULE
}
