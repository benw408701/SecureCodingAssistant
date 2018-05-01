/*
 * STR38C_DoNotConfuseNarrowandWideCharacterStringsAndFunctions.c
 *
 *
 *      Author: Victor Melnik
 *
 *   The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */


#include <stddef.h>
#include <string.h>
#include <wchar.h>
#include <stdlib.h>

void func_38C_NC(void) {
  wchar_t wide_str1[]  = L"0123456789";
  wchar_t wide_str2[] =  L"0000000000";

  strncpy(wide_str2, wide_str1, 10); //rule violated
  wcsncpy(wide_str1, wide_str2, 10);
}

void func_38C_NCTwo(void) {
  char narrow_str1[] = "01234567890123456789";
  char narrow_str2[] = "0000000000";

  wcsncpy(narrow_str2, narrow_str1, 10);//rule violated
  strncpy(narrow_str2, narrow_str1, 10);
}

void func_38C_NCThree(void) {
  wchar_t wide_str1[] = L"0123456789";
  wchar_t *wide_str2 = (wchar_t*)malloc(strlen(wide_str1) + 1);;//rule violated

  wchar_t **wide_str22 = NULL;

  wchar_t *wide_str3;
  wchar_t **wide_str33;

  if (wide_str2 == NULL) {
    /* Handle error */
  }
  /* ... */
  free(wide_str2);
  wide_str2 = NULL;
}

