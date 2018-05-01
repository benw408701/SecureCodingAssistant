/*
 * FIO47C_UseValidFormatStrings.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

#include <stdio.h>

void func_fio47nc(void) {
  const char *error_msg = "Resource not available to user.";
  int error_type = 3;
  unsigned int ch;
  /* ... */
  printf("Err,or %% \" (type %s): %d\n", error_type, error_msg);

  scanf("%c", &ch);
  /* ... */
}

void func_fio47C(void) {
  const char *error_msg = "Resource not available to user.";
  int error_type = 3;
  /* ... */
  printf("Error (type %d): %s\n", error_type, error_msg);

  /* ... */

  int a,b;
  		float c,d;

  		a = 15;
  		b = a / 2;
  		printf("%d\n",b);
  		printf("%3d\n",b);
  		printf("%03d\n",b);

  		c = 15.3;
  		d = c / 3;
  		printf("%3.2f\n",d);

  		printf("The color: %s\n", "blue");
  			printf("First number: %d\n", 12345);
  			printf("Second number: %04d\n", 25);
  			printf("Third number: %i\n", 1234);
  			printf("Float number: %3.2f\n", 3.14159);
  			printf("Hexadecimal: %x\n", 255);
  			printf("Octal: %o\n", 255);
  			printf("Unsigned value: %u\n", 150);
  			printf("Just print the percentage sign %%\n", 10);

  			 char ch;
  			   char str[100];
  			   printf("Enter any character \n");
  			   scanf("%c", &ch);
  			   printf("Entered character is %c \n", ch);
  			   printf("Enter any string ( upto 100 character ) \n");
  			   scanf("%s", &str);
  			   printf("Entered string is %s \n", str);
}
