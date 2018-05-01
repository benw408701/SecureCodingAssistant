/*
 * FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles.c
 *
 *
 *      Author: Victor Melnik
 *
 *  The text and/or code below is from the CERT website:
 * "https://wiki.sei.cmu.edu/confluence/display/seccode"
 */


#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>

void open_some_file(const char *file) {
  FILE *f = fopen(file, "r");
  if (NULL != f) {
    /* File exists, handle error */
  } else {
    if (fclose(f) == EOF) {
      /* Handle error */
    }
    f = fopen(file, "w"); //VIOLATED
    if (NULL == f) {
      /* Handle error */
    }

    /* Write to file */
    if (fclose(f) == EOF) {
      /* Handle error */
    }
  }
}


void open_some_file_compliant(const char *file) {
	FILE *f = fopen(file, "wx");
	  if (NULL != f) {
	    /* File exists, handle error */
	  } else {
	    if (fclose(f) == EOF) {
	      /* Handle error */
	    }
	    f = fopen(file, "w"); //VIOLATED
	    if (NULL == f) {
	      /* Handle error */
	    }

	    /* Write to file */
	    if (fclose(f) == EOF) {
	      /* Handle error */
	    }
	  }
}


void open_some_file_posix_compliant(const char *file) {
	//int fd;
	int fd = open(file, O_CREAT | O_EXCL | O_WRONLY);
	//fd = open(file, O_CREAT | O_EXCL | O_WRONLY);
  if (-1 != fd) {
    FILE *f = fdopen(fd, "w");
    if (NULL != f) {
      /* Write to file */

      if (fclose(f) == EOF) {
        /* Handle error */
      }
    } else {
      if (close(fd) == -1) {
        /* Handle error */
      }
    }
  }
}
