### TD Rappel_oo ###

##  Question 1 :
  # 1. 
    Il affiche dans un premier temps (0,0) et ensuite (2,2).
  # 2.
    l'erreur est : "class TestPoint is public, should be declared in a file named TestPoint.java".
    Si on rajoute public devant la classe TestPoint ça ne fonctionne pas car en java une classe public doit etre définie dans un fichier portant le meme nom. Il s'agit d'une convention.
  # 3.
    Il n'y a pas de message d'erreur, j'ai toujours le même résultat.

##  Question 2 :
  # 1. 
   "x has private access in oo_basics.Point" Java n'arrive pas à trouver le point.
  # 2. 
    On obtient ceci dans la console:
    "(0.0, 0.0)
    méthode move(int, int)
    (2.0, 2.0)".
    Il y'a ici une surchage de méthode. Etant donné que les valeurs mises en paramètre de ma fonction move sont "(2,2)" java considère que ce sont des entiers et fait appel à la methode move qui prend en paramètre des entiers et pas des double
  # 3.
    Nous avons ce message d'erreur "method move(double,double) is already defined in class oo_basics.Point". En gros le compilateur nous dit que notre méthode move est déjà définie plus haut et ne peut pas être définie 2 fois même si le type de retour est différent.

##  Question 3 :
  # 1. 
    On obtient l'erreur :
    "call to this must be first statement in constructor".
    le this doit être la première instruction, il n'est pas possible de mettre autre chose avant.
  # 2. 
    On obtient cette erreur : 
    "java: constructor Point in class oo_basics.Point cannot be applied to given types;
    required: double,double
    found:    no arguments
    reason: actual and formal argument lists differ in length"
    Il n'y a pas de constructeur, le code ne peut pas fonctionner.
  # 3.
    Le code fonctionne car quand on a pas de constucteur dans classe, celle ci prend le constructeur par défaut sans paramètre.

##  Question 4 :
  # 1.
    Ce programme affiche ceci : 
    "Circle : [(0.0, 0.0), 5.0]
    Circle : [(2.0, 5.0), 5.0]
    Circle : [(2.0, 5.0), 10.0]".
  # 2.
    On crée une seule instance de point dont on modifie les coordonnées.
    
## Question 5 :
  # 1.
    Ce programme affiche :
    Circle : [(0.0, 0.0), 5.0]
    Circle : [(2.0, 5.0), 5.0]
    Circle : [(0.0, 0.0), 5.0]
  # 2.
    Il y'a une instance de Circle qui est crée et une instance de Point qui est crée. La variable p fait référence au centre du cercle et la variable p2 fait référence au meme point car la fonction p.getcirlcle permet de récupérer le centre du cerlce qui est le point p.
    L'attribut `center` de l'instance `c` (le cercle) référence également l'instance de `Point` qui est `p`.
  # 3.
    Le programme affiche ceci, à chaque fois qu'un centre est créer un nouveau point est créer 
    Circle : [(0.0, 0.0), 5.0]
    Circle : [(0.0, 0.0), 5.0]
    Circle : [(-2.0, -5.0), 5.0]
  # 4.
    Le code affiche ceci :
    Circle : [(0.0, 0.0), 5.0]    
    Circle : [(0.0, 0.0), 5.0]
    Circle : [(0.0, 0.0), 5.0]
  # 5.
    Une instance de Point est crée,
    Une instance de Circle est crée et
    Une instance de Point est crée avec GetCenter
  # 6.
    La variable p référence la première instance de Point créée avec new Point().
    La variable p2 référence la troisième instance de Point créée avec c.getCenter().
    L’attribut center de l’instance c référence la deuxième instance de Point créée dans le constructeur de Circle.

## Question 6:
  # 1.
    Ca affiche : 
    (3.0, 6.0) - FF0000FF
    x: 3.0
    color : FF0000FF
  # 2. 
    Nous obtenons cette erreur : 
    Exception in thread "main" java.lang.Error: Unresolved compilation problem: 
    The method getColor() is undefined for the type Point
    Le compilateur nous dit que la méthode getColor() n'est pas définie dans la classe Point et cela est normal
    Oui nous avons toujours un message d'erreur : 
    Exception in thread "main" java.lang.Error: Unresolved compilation problems: 
        p cannot be resolved to a variable
        p cannot be resolved
        p cannot be resolved to a variable
  # 3.
    Non on ne peut pas, on obtient ce message d'erreur. Parce que le compilatreur détecte une erreur plus tot: 
    Exception in thread "main" java.lang.Error: Unresolved compilation problems: 
        Type mismatch: cannot convert from Point to ColoredPoint
        The method getColor() is undefined for the type Point
  # 4.
    Non, on ne peut pas 
  # 5.
    Nous obtenons cette erreur :
    Exception in thread "main" java.lang.Error: Unresolved compilation problems: 
        Implicit super constructor ColoredPoint() is undefined. Must explicitly invoke another constructor
        The return type is incompatible with Point.move(double, double)

        at oo_basics.Point.<init>(Point.java:13)
        at oo_basics.Point.<init>(Point.java:11)
        at oo_basics.TestPoint.main(Point.java:31)
  # 6.
    On obtient cette erreur :
    Exception in thread "main" java.lang.Error: Unresolved compilation problems: 
        The field Point.x is not visible
        The field Point.y is not visible

        at oo_inheritance.ColoredPoint.toString(ColoredPoint.java:16)
        at java.base/java.lang.String.valueOf(String.java:4465)
        at java.base/java.io.PrintStream.println(PrintStream.java:1187)
        at oo_inheritance.TestPoints.main(TestPoints.java:8)
  # 7.

## Question 7:
  # 1.
    Oui, parce que Point est une classe et Object est la classe de base de toutes les classes en Java.
    Ainsi, je peux assigner une instance de Point à une variable de type Object.
  # 2.
    Oui, pour la même raison que ci-dessus. ColoredPoint est une sous-classe de Point, et donc aussi une sous-classe de Object. 
    Je peux donc assigner une instance de ColoredPoint à une variable de type Object.
  # 3. 
    Oui, parce que la méthode hashCode() est définie dans la classe Object, 
    qui est la classe de base de toutes les classes en Java1. Cela signifie que toutes les classes, 
    y compris ColoredPoint, héritent de cette méthode.

## Question 8:
  # 1.
    J’obtiens une erreur de compilation indiquant que la première instruction d’un constructeur doit être 
    un appel à un autre constructeur de la même classe ou de la classe parente. En Java, si un constructeur appelle 
    un autre constructeur, cet appel doit être la première instruction du constructeur.
  # 2.
    J’obtiens une erreur de compilation indiquant que le constructeur de la classe parente Point n’est pas défini. 
    Cette ligne sert à appeler le constructeur de la classe parente Point avec les paramètres x et y. 
    Sans cet appel, le compilateur essaie d’appeler un constructeur par défaut de Point, 
    qui n’existe pas si nous n’avons pas défini de constructeur sans paramètres dans Point.
  # 3.
    Je n’obtiens plus la même erreur qu’au point précédent. Le constructeur par défaut de Point appelle maintenant
    le constructeur avec paramètres Point(double x, double y) avec les valeurs 0 et 0. Cela permet à ColoredPoint
    de se compiler correctement même sans l’appel explicite à super(x, y).

## Question 9:
  # 1.
    Ce code affiche :
    constructor of A
    constructor of B
    constructor of C
  # 2.
    constructor of A
    constructor of B
  # 3.
    L’effet reste identique, et le programme affiche toujours :
    constructor of A
    constructor of B
    constructor of C
    L’appel explicite à super() dans chaque constructeur assure que le constructeur de la super-classe est appelé, 
    ce qui est déjà le comportement par défaut en Java.
  # 4.
    protected Object(Object obj)
    Ce constructeur est protégé et est utilisé pour créer une copie d’un objet existant. 
    Cependant, il n’est pas directement accessible pour les utilisateurs finaux.

## Question 10:
  # 1.
    Le programme affiche :
    0.0 - 0.0 - not pinned
    1.0 - 1.0 - pinned
    Explication :
    La première ligne affiche les coordonnées initiales du point (0, 0) et indique qu’il n’est pas fixé (not pinned).
    Après le premier déplacement, les coordonnées deviennent (1, 1).
    Ensuite, le point est fixé (pinned).
    Le deuxième déplacement n’a pas d’effet car le point est fixé, donc les coordonnées restent (1, 1).
  # 2.
    celle de PinnablePoint car l’objet référencé par la variable à ce moment-là est de type PinnablePoint ?
    La méthode move exécutée est celle de PinnablePoint car l’objet référencé par la variable à ce moment-là
    est de type PinnablePoint.
  # 3.
    J’obtiens une erreur de compilation indiquant que la méthode move dans PinnablePoint ne peut pas déclarer une exception plus large que celle déclarée dans la méthode move de la classe parente Point. En effet, la méthode move de Point ne déclare pas d’exception.
  # 4.
    Je n’ai plus d’erreur de compilation. IllegalStateException est une exception non vérifiée (runtime exception), ce qui signifie qu’elle n’a pas besoin d’être déclarée dans la clause throws de la méthode.
  # 5.
    Non, je n’ai pas d’erreur. Comme mentionné précédemment, IllegalStateException est une exception non vérifiée, donc elle n’a pas besoin d’être déclarée dans la clause throws.
  # 6.
    Non, je n’ai pas d’erreur. 
  # 7.
    Non, je n’ai pas d’erreur. Cependant, cela peut rendre le code moins clair et moins sûr, car je perds les informations spécifiques au type.
  # 8.
    J’obtiens une erreur de compilation indiquant que je ne peux pas réduire la visibilité de la méthode héritée de Point. La méthode move dans Point est publique, donc la méthode move dans PinnablePoint doit également être publique.
  # 9.
    L’appel super.move(dx, dy); appelle la méthode move de la classe parente Point. Cela permet de déplacer le point en appelant la logique définie dans la classe Point.

## Question 11.
  # 1.
    Je constate une erreur de compilation. En effet, les méthodes dans une interface ne peuvent pas être protected. Elles doivent être public ou avoir la visibilité par défaut (qui est implicitement public).
  # 2.
    L’erreur de compilation disparaît. Les méthodes dans une interface doivent être public car elles définissent un contrat que les classes implémentant l’interface doivent respecter.
  # 3.
    public class Point implements Movable {
    // ...
    @Override
    public Movable move(double dx, double dy) {
        // Implémentation de la méthode move
        return this;
    }
    // ...
    }
    En modifiant la classe Point pour qu’elle implémente l’interface Movable, je dois m’assurer que la méthode move est correctement implémentée.
  # 4.
    Point at (1.0, 1.0)
    Cela montre que le polymorphisme fonctionne correctement, permettant à une variable de type Movable de référencer un objet de type Point


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

            
            
            
LAMBDA

  Question 1
   
   1. List<Person> filteredList = filter(myList, p -> p.getFirstname().startsWith("J"));
    
   2. List<Person> filteredList = filter(myList, p ->  p.getFirstname().startsWith("J") && p.getAge() < 50);


 Question 2
 
   1.  Collections.sort(list, (word1, word2) -> word1.length() - word2.length());
   2. Collections.sort(list, (w1, w2) -> Character.compare(w1.charAt(0), w2.charAt(0)));

 Question 3
 
   3. (w1, w2) -> {...} : Acceptée. Le compilateur peut inférer le type.
   8. (w1, w2) -> w1.length() - w2.length(); : Acceptée. Cette syntaxe est valide sans return et sans accolades si c'est une simple expression.
   9. (w1, w2) -> w1.length() - w2.length() : Acceptée.
   
   
   ### TD JAVA FX ###

# Question 1 #
  # 1.
    Si je modifie ces valeurs, par exemple :
    Scene scene = new Scene(root, 500, 300);
    Les effets seront :
    Largeur augmentée : La fenêtre sera plus large (500 pixels au lieu de 250).
    Hauteur augmentée : La fenêtre sera plus haute (300 pixels au lieu de 100).
    Ces valeurs définissent les dimensions initiales de la scène. Modifier les 
    paramètres permet donc de contrôler la taille de la fenêtre lors de l'affichage de l'application.
  # 2.
    La méthode primaryStage.initStyle(StageStyle.TRANSPARENT); modifie l'apparence de la fenêtre JavaFX. 
    Plus précisément, elle rend la fenêtre transparente, ce qui signifie que la fenêtre n'aura plus de 
    bordure ou de décoration, et le fond sera complètement invisible. 

    StageStyle.DECORATED :
    C'est le style par défaut.
    La fenêtre aura une barre de titre, des bordures, et des boutons de fermeture/minimisation/agrandissement
     standard.
    StageStyle.UNDECORATED :

    La fenêtre n'aura ni barre de titre ni bordures. Elle sera entièrement contrôlée par le contenu de la scène 
    sans décoration supplémentaire.
    StageStyle.TRANSPARENT :

    La fenêtre sera totalement transparente. Le fond de la fenêtre et les bordures n'existeront pas, ce qui rend 
    le contenu de la scène visible, mais pas le cadre de la fenêtre elle-même.
    StageStyle.UNIFIED (Non disponible sur toutes les plateformes) :

    Un style où la barre de titre et la zone de contenu sont fusionnées, créant un look unifié.
    StageStyle.UTILITY :

    Utilisé pour créer une fenêtre de type "boîte de dialogue" ou utilitaire. Elle aura un style simplifié avec 
    une barre de titre, mais sans les boutons standards de maximisation.
  # 3.
    setTop() :
    Le composant sera placé dans la zone supérieure (en haut) de la fenêtre.
    Si j'appelle root.setTop(helloText);, le texte "Hello World" apparaîtra au sommet de la fenêtre, au-dessus des autres éléments s'il y en a.
    
    setBottom() :
    Le composant sera placé dans la zone inférieure (en bas) de la fenêtre.
    Si j'utilise root.setBottom(helloText);, le texte "Hello World" sera affiché en bas de la fenêtre.
    
    setLeft() :
    Le composant sera placé dans la zone à gauche de la fenêtre.
    En appelant root.setLeft(helloText);, le texte "Hello World" sera positionné sur le côté gauche de la fenêtre.
    
    setRight() :
    Le composant sera placé dans la zone à droite de la fenêtre.
    En utilisant root.setRight(helloText);, le texte "Hello World" sera aligné sur le côté droit de la fenêtre.
  # 4.
    La méthode setUnderline(true) sur l'objet Label permet d'ajouter un soulignement au texte
  
# Question 2 #
  # 1.
    checkBox1 est simplement une case à cocher, cochée par défaut.
    checkBox2 est une case à cocher qui démarre dans un état indéterminé et ne peut pas être décochée ou cochée
    jusqu'à ce que l'utilisateur interagisse avec elle.
    checkBox3 est une case à cocher normale mais qui permet un troisième état indéterminé, offrant plus de
    flexibilité lors de l'interaction.
  # 2.
    Avec BorderPane.setAlignment(Pos.CENTER) : Les CheckBox seront alignées au centre vertical de leurs zones 
    respectives (gauche et droite).
    Sans BorderPane.setAlignment(Pos.CENTER) : Les CheckBox seront alignées vers le haut de leurs zones, 
    donnant une apparence moins équilibrée.
  # 3.
    Pour transformer le composant TextField en PasswordField, je dois simplement remplacer l’instance de
    TextField par PasswordField
# Question 4 #
    après avoir pressé la touche Enter, le texte saisi est “enregistré” (indiqué par le label ajouté), 
    et le champ de texte devient non modifiable. 
# Question 5 #
    Pane pane = new Pane();
    Circle circle = new Circle(50, Color.BLUE);
    Rectangle rectangle = new Rectangle(100, 100, Color.RED);
    pane.getChildren().addAll(circle, rectangle);
    
    Dans cet exemple, les enfants (un cercle et un rectangle) sont ajoutés à la liste des enfants du Pane, 
    et toute modification de cette liste peut être observée et gérée en conséquence.
# Question 6 #
  # 1.
    root.setAlignment(Pos.CENTER);
  # 2.
    root.getChildren().addAll(checkBox1, checkBox2, checkBox3);
  # 3.
    Cela peut entraîner des problèmes d’affichage où des parties des composants peuvent être invisibles ou 
    chevaucher d’autres éléments de l’interface utilisateur.
  # Question 7 #
  # 1.
    Lorsque vous placez plusieurs composants dans une même cellule d’un GridPane, ils sont empilés les uns 
    sur les autres par défaut. Cependant, vous pouvez contrôler leur disposition en utilisant des conteneurs 
    supplémentaires comme VBox ou HBox pour organiser les composants à l’intérieur de la cellule.

    Par exemple : 
    VBox vbox = new VBox();
    vbox.getChildren().addAll(new Label("Label 1"), new Label("Label 2"));
    root.add(vbox, 0, 0);
  # 2.
    En remplaçant GridPane.setHalignment(lblPassword, HPos.RIGHT) par GridPane.setHalignment(lblPassword, 
    HPos.CENTER), le label lblPassword sera centré horizontalement dans sa cellule. Cela signifie que le 
    texte “Password” apparaîtra au centre de la cellule, plutôt qu’à droite.
  # 3.
    En remplaçant GridPane.setFillWidth(tfdPassword, false) par GridPane.setFillWidth(tfdPassword, true), 
    le champ de texte tfdPassword s’étendra pour remplir toute la largeur disponible de sa cellule. Cela 
    permet au champ de texte de s’adapter dynamiquement à la taille de la cellule, offrant une meilleure 
    utilisation de l’espace disponible.


### TD JAVA FX EVENTS ###
  
  # Question 1 #
    Les événements de clic de souris sur les éléments de l'interface (stage, scène, boutons, etc.) 
    déclenchent le GraphDisplayHandler, qui affiche le texte associé au composant où le clic a eu lieu.
    
    Le bouton Insert, quant à lui, exécute son propre gestionnaire, ajoutant du texte à l'interface utilisateur
    (dans le TextArea), tout en affichant des messages de debug dans la console grâce aux GraphDisplayHandler.

    Le résultat montre que chaque clic sur un composant de l'interface déclenche deux types d'événements : l'un lié à l'interface graphique 
    (modification de texte, etc.), et l'autre pour la journalisation en console via le gestionnaire d'événements.
  # Question 2 #
    addEventFilter(KeyEvent.KEY_TYPED, e -> event.consume()) : Ce code ajoute un filtre d'événements pour le champ de texte (TextField) tfdCharacter.
    KeyEvent.KEY_TYPED : Cet événement est déclenché chaque fois qu'une touche est pressée et relâchée, c'est-à-dire lors de la saisie de texte.
    event.consume() : Cette méthode indique que l'événement a été "consommé", ce qui signifie qu'il ne doit plus être traité par les autres 
    gestionnaires d'événements ou filtres. En d'autres termes, cela empêche l'événement de continuer dans le système d'événements JavaFX, 
    donc le caractère saisi n'apparaît pas dans le champ de texte.

