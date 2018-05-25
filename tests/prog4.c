#include <stdio.h>
// Programa "sequencias"

char msg1[] = "Escolha operacao desejada: (0) N termo de Fibonacci, (1) Calcular fatorial:";
char msg2[] = "Insira o valor desejado:";
char msgErro[] = "Entrada invalida!";

int fibonacci (int termo) {
   if (termo < 2) {
      return(termo);
   } else {
      return(fibonacci (termo - 1) + fibonacci (termo - 2));
   }
}

int fatorial (int termo) {
   int i, resultado;

   if (termo = 0) {
      return(1);
   } else {
      resultado = termo;
      
      for (i = termo - 1;i > 0; i = i - 1;) {
         resultado = resultado * i;
      }
   }
   
   return(resultado);
}

int main () {
   int resultado, opcao, termo;

   printf("%s", msg1);
   
   scanf("%d", &opcao);
   
   printf("%s", msg2);
   
   scanf("%d", &termo);
   
   if (opcao = 0) {
      resultado = fibonacci (termo);
   } else {
      if (opcao = 1) {
         resultado = fatorial (termo);
      } else {
         printf("%s", msgErro);
         
         return(0);
      }
   }
   
   printf("%d %d %d", opcao, termo, resultado);
   
   return(0);
}
