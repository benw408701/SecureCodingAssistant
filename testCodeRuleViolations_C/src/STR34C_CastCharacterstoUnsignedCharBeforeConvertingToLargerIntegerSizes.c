/*
 * STR34C_CastCharacterstoUnsignedCharBeforeConvertingToLargerIntegerSizes.c
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

//FROM CERT Website (but modified)
#include <limits.h>
#include <stddef.h>
#include <stdio.h>

static int yy_string_get(void) {
  register char *c_str;
  register int c;

  c_str = NULL;
  c = EOF;

  /* If the string doesn't exist or is empty, EOF found */
  if (c_str && *c_str) {
    c = *c_str++;
    //bash_input.location.string = c_str;
  }

  int cc = *c_str; //VIOLATION
  return (c);
}

//compliant
static int yy_string_get_Compliant(void) {
  register char *c_str;
  register int c;

  c_str = NULL;
  c = EOF;

  /* If the string doesn't exist or is empty, EOF found */
  if (c_str && *c_str) {
    /* Cast to unsigned type */
    c = (unsigned char)*c_str++;
    c = (unsigned int)*c_str++;
    //bash_input.location.string = c_str;
  }

  int cc = (unsigned char)*c_str;

  return (c);
}


static const char table[UCHAR_MAX + 1] = { 'a' /* ... */ };

//Noncompliant
ptrdiff_t first_not_in_table(const char *c_str) {



  for (const char *s = c_str; *s; ++s) {
    if (table[(unsigned int)*s] != *s) {
      return s - c_str;
    }
  }
  return -1;
}

//compliant
ptrdiff_t first_not_in_table_compliant(const char *c_str) {



  for (const char *s = c_str; *s; ++s) {
    if (table[(unsigned char)*s] != *s) {
      return s - c_str;
    }
  }
  return -1;
}
