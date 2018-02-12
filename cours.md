# Application réseau sécurisée
*[Vincent Guyot](mailto:vincent.guyot@esiea.fr)*

## Rappels sur les threads en Java

```java
public class FooThread extends Thread{
	public FooThread(){
		start();
		while(true){
			System.out.println("A");
		}
	}
	public void run(){
		while(true){
			System.out.println("B");
		}
	}
}
```

Pour cette application :
* `ServerChat`
	* boucle en attente
	* lance un server pour chaque objet socket
	* doit lier les services : mot-clé `static`
		* différentes instanciations d'une même classe donnée vont partager le même emplacement mémoire pour la variable `static`
		* `static nbClients`
* `ServiceChat extends Thread`
	* `static PrintStream outputs[NBCLIENTSMAX]`
	* `BufferReader input`

### Exemple d'utilisation de `static`

```java
public class C{
	public static int attr;
	public C(int attr){
		this.attr = attr;
	}
}
```
```java
C c1 = new C(1);
C c2 = new C(2);
C c3 = new C(3);
System.out.println(c1.attr)	// ==> 3
```

##

```java
import java.net.*;

public class Telnet extends Thread{
	Socket s;
	BufferedReader inputConsole, inputNetwork;
	PrintStream outputConsole, outputNetwork;

	public Telnet(String[] args) throws Exception{
		s = new Socket(args[0], Integer.parseInt(args[1]));
		initInputOutput(s);
		start();
		listenConsole();
	}

	public void run(){
		listenNetwork();
	}

	public static void main(String[] args) throws Exception{
		new Telnet(args);
	}
}
```
```bash
$ java Telnet <host> <port>
```

## Commandes à implémenter

* list user
* disco
* un utilisateur en particulier

### Types de données envoyées

* Message
* Commande
* Fichier