#include <stdio.h>
// Programa "fibonacci"

int soma (int x, int y) {
   int z;

   z = x + y;
   
   return(z);
}

int recursao (int x, int y, int num, int entrada) {
   int temp1;

   if (num = entrada) {
      return(y);
   }
   
   temp1 = soma (x, y);
   
   x = y;
   
   y = temp1;
   
   num = num + 1;
   
   y = recursao (x, y, num, entrada);
   
   return(y);
}

int main () {
   int x, y, result, num, entrada;

   scanf("%d", &entrada);
   
   x = 0;
   
   y = 1;
   
   num = 1;
   
   if (entrada = 0) {
      result = x;
   } else {
      result = recursao (x, y, num, entrada);
   }
   
   printf("%d", result);
   
   return(0);
}
