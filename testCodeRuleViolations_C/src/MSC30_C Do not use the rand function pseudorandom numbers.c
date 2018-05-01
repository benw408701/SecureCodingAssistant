/*
 * MSC30_C Do not use the rand function pseudorandom numbers.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <limits.h>


enum { len = 12 };

int main() {
  /*
   * id will hold the ID, starting with the characters
   *  "ID" followed by a random integer.
   */
  char id[len];
  int r;
  int num;
  int numbers = 1;
  char *command;
  void *commans2;

  short smallest = 32763;
  long biggest = LONG_MIN;

  printf("\nLong Longwith d: %d", biggest);
  printf("\nLong Longwith d: %ld", biggest);
  printf("\nLong Longwith d: %lld", biggest);


  printf("\nshort  d: %d", smallest);
    printf("\nshort  hd: %hd", smallest);
    printf("\nshort hhd: %hhd", smallest);

  struct empty{

  };
  /* ... */

  struct empty s1;
  r = rand();  /* Generate a random integer */
  num = snprintf(id, len, "ID%-d", r);  /* Generate the ID */
  printf("\nRandom Number: %d", r);
  printf("\nSizeof: %d", sizeof(s1) );
  printf("\nSizeof pointer: %d", sizeof(*commans2) );


   int c  = 10;
   printf("geeks for %ngeeks ", &c);
   printf("%d", c);
   //getchar();


   int var =10;
      int *p;
      p= &var;

      printf ( "\nAddress of var is: %p", &var);
      printf ( "\nAddress of var is: %p", p);

      printf ( "\nValue of var is: %d", var);
      printf ( "\nValue of var is: %d", *p);
      printf ( "\nValue of var is: %d", *( &var));

      /* Note I have used %p for p's value as it represents an address*/
      printf( "\nValue of pointer p is: %p", p);
      printf ( "\nAddress of pointer p is: %p", &p);
  /* ... */
  return 0;
}
