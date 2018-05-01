/*
 * DCL36C_DoNotDeclareAnIndentifierWithConflictingLinkageClassification.c
 *
 *
 *      Author: Victor Melnik
 *
 *
 * The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

//From CERT Website

int i1 = 10;         /* Definition, external linkage */
static int i2 = 20;  /* Definition, internal linkage */
extern int i3 = 30;  /* Definition, external linkage */
int i4;              /* Tentative definition, external linkage */
static int i5, i6, i7;       /* Tentative definition, internal linkage */


int i1;  /* Valid tentative definition */
int i2;  /* Undefined, linkage disagreement with previous */
int i3;  /* Valid tentative definition */
int i4;  /* Valid tentative definition */
int i5;  /* Undefined, linkage disagreement with previous */

int main_DCL36(void) {
  /* ... */
  return 0;
}
