LES REGEX


  QUESTION 1
      
   1.  ^[gG]\d{5}$

        ^ : début de la chaîne
        [gG] : une lettre "g" (majuscule ou minuscule)
        \d{5} : cinq chiffres
        $ : fin de la chaîne

   2.  ^([gG]\d{5})(\s+[gG]\d{5})*$ 

        ^ : début de la chaîne
        ([gG]\d{5}) : un matricule
        (\s+[gG]\d{5})* : zéro ou plusieurs matricules supplémentaires, chacun précédé d'un ou plusieurs espaces
        $ : fin de la chaîne

   3. ^Bonjour.*merci\.$

        ^ : début de la chaîne
        Bonjour : mot "Bonjour"
        .* : zéro ou plusieurs caractères (n'importe quel caractère)
        merci\. : mot "merci" suivi d'un point (le \ est pour échapper le point)
        $ : fin de la chaîne
   
   4. ^add\s+circle\s+(\d+)\s+(\d+)\s+(\d+)\s+([a-zA-Z])$

        ^ : début de la chaîne
        add : mot "add"
        \s+ : un ou plusieurs espaces
        circle : mot "circle"
        \s+ : un ou plusieurs espaces
        (\d+) : un nombre naturel (au moins un chiffre)
        \s+ : un ou plusieurs espaces (répété pour les trois nombres)
        ([a-zA-Z]) : un caractère unique (lettre)
        $ : fin de la chaîne

   4. ^move\s+(\d+)\s+(-?\d+)\s+(-?\d+)$

        ^ : début de la chaîne
        move : mot "move"
        \s+ : un ou plusieurs espaces
        (\d+) : un nombre naturel (au moins un chiffre)
        \s+ : un ou plusieurs espaces
        (-?\d+) : un nombre qui peut être négatif (option de -)
        \s+ : un ou plusieurs espaces
        (-?\d+) : un autre nombre qui peut être négatif
        $ : fin de la chaîne


 QUESTION 2
 
       Le group 0 contient l'ensemble de group
    



LES GENERIQUES

    
   1. Erreur : incompatible types: java.lang.Double cannot be converted to java.lang.Integer
      L'erreur dit qu'on peut pas convertir un double en entier car le box attends des entiers et non un double.
      
   2. Le type Box<Integer> n'est pas un sous classe de Box<Object>
      
   3. - Erreur : cannot be converted to esi.generics.Box<java.lang.Object>
        on ne peut pas caster un box<Integer> en Box<Integer> car il y'a aucien lien entre les deux 
        
      -   ligne 11, un double ne peut etre converti en Integer hors Box est instancie avec Des Integer
      
   4. Erreur : java: incompatible types: java.lang.Integer cannot be converted to capture#1 of ?   
             En déclarant Box<?> box, cela signifie que box peut contenir une Box de n'importe quel type. 
             Par conséquent, le compilateur ne sait pas quel type est utilisé et ne peut pas garantir 
             que new Integer(43) est du même type que celui contenu dans box.
             
   5. Erreur :  cannot find symbol method compareTo(T)
   
            La Methode compareTo(T) n'est pas une methode de l'object T
            
   6. Erreur : java: type argument java.lang.Number is not within bounds of type-variable T
            Dans la classe Pair<T extends comparable <T>> , on impose que tout type qui implemente
            l'inteface comparable , or le type Number n'implemente pas l'interface comparable;
            
   7. 
      1.

     class Animal {
         @Override
         public String toString() {
         return "Animal";
         }
     }

    class Dog extends Animal {
        @Override
        public String toString() {
        return "Dog";
        }
     }

    public class TestCopy {
        public static void main(String[] args) {
             List<Animal> animals = new ArrayList<>();
             animals.add(new Animal());
             animals.add(new Dog());

             List<Dog> dogs = new ArrayList<>();
             dogs.add(new Dog());

             Collections.copy(animals, dogs);
             System.out.println(animals);
        }
    }
    
      Explication : La liste animals est de type List<Animal>.
                    La liste dogs est de type List<Dog>.
                    Collections.copy(animals, dogs); compile et
                    fonctionne parce que List<? super T> accepte List<Animal> comme Animal est un supertype de Dog.



     2. public class TestSort {
           public static void main(String[] args) {
                 List<Integer> numbers = new ArrayList<>();
                 numbers.add(5);
                 numbers.add(2);
                 numbers.add(8);
                 numbers.add(1);

                 Comparator<Number> comparator = (n1, n2) -> Integer.compare(n1.intValue(), n2.intValue());

                 Collections.sort(numbers, comparator);

                 System.out.println(numbers);
         } 
    }

            
