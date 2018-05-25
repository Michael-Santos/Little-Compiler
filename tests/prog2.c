#include <stdio.h>
// Programa "aproxpi"

int main () {
   int limite, i, base, temp;

   float somatorio, resultado;

   scanf("%d", &limite);
   
   somatorio = limite;
   
   somatorio = 0;
   
   for (i = 1;; i < limite + 1; i = i + 1;) {
      base = i * 2 - 1;
      
      temp = i / 2;
      
      if (i - temp * 2 = 1) {
         somatorio = somatorio + 1 / base;
      } else {
         somatorio = somatorio - 1 / base;
      }
   }
   
   resultado = somatorio * 4;
   
   printf("%f", resultado);
   
   return(0);
}
