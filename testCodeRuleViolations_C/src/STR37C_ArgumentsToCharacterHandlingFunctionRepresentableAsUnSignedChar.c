/*
 * STR37C_ArgumentsToCharacterHandlingFunctionRepresentableAsUnSignedChar.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

//Example code from CERT
#include <ctype.h>
#include <string.h>

size_t count_preceding_whitespace(const char *s) {
  const char *t = s;
  char tt[] = "abc";
  unsigned char ttt = "a";
  unsigned int var1 = 30;
  size_t length = strlen(s) + 1;
  while (isspace(*t) && (t - s < length)) { //VIOLATION
    ++t;
  }
  tolower(*t); //VIOLATION
  tolower(tt[0]);//VIOLATION
  tolower(ttt);

  var1 = var1* 400;

  tolower(var1);
  return t - s;
}

size_t count_preceding_whitespace_C(const char *s) {
  const char *t = s;
  size_t length = strlen(s) + 1;
  while (isspace((unsigned char)*t) && (t - s < length)) {
    ++t;
  }
  return t - s;


}
