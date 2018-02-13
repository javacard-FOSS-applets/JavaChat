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

## Consignes pour SecureChat

- [x] V0
	- [x] Utilisation du client TCP générique telnet
	- [x] Serveur à l'écoute sur un port TCP fixe
	- [x] Serveur muet sur la sortie standard
	- [x] Quand serveur plein : envoi d'un message informatif aux tentatives de connexion
	- [x] Pas de gestion de la déconnexion des utilisateurs
- [x] V1
	- [x] Port TCP pouvant être indiqué au lancement du serveur en ligne de commande
	- [x] Serveur volubile sur la sortie standard : port TCP utilisé, mode en cours (attente de connexion, tentative de connexion)
	- [x] Log sur la console du serveur des différentes actions (connexion d'un utilisateur, déconnexion d'un utilisateur, messages transmis, etc.)
	- [x] A la connexion si serveur pas plein : demande d'un pseudo qui doit être unique (pas 2 connectés avec le même login)
	- [x] Affichage du nombre d'utilisateurs en ligne à chaque nouveau connecté
	- [x] Chaque message préfixé du loding de son émetteur "<foo> Hello world!"
- [ ] V2
	- [x] Commande pour se déconnecter "/quit"
	- [x] A la deconnexion d'un utilisateur, message "[SERVER] Bye foo!"
	- [x] A la connexion d'un nouvel arrivant, message "[SERVER] Hello foo!"
	- [x] Commande pour énumérer les pseudos des utilisateurs en ligne "/list"
	- [x] Envoi de message personnel "/msg foo Hello world!"
	- [x] Authentification par mot de passe : lors de la première connexion d'un pseudo donné, enregistrement côté serveur du mot de passe correspondant pour authentification ultérieure
- [ ] V3
	- [x] Utilisation comme client de ClientChat.java, sur la base de Telnet.java
	- [x] Serveur et port TCP pouvant être indiqués au lancement du serveur en ligne de commande, sinon utilisation de valeurs par défaut ("localhost" et "2222")
	- [ ] Commande pour transmettre un fichier "/file foo file.bin" (pour transmettre un fichier binaire à traver une transmission textuelle, encodage du binaire en base 64)
- [ ] V4
	- [ ] Authentification forte à la connexion : couple login/mot de passe remplacé par login/publicKey et challenge RSA à la connexion résolut par la carte à puce de l'utilisateur (génération d'un bi-clé RSA à la demande et exfiltration de la clé publique pour envoi au serveur à la première connexion)
	- [ ] Chiffrement/déchiffrement symétrique des données transmises avec la clé DES partagé par toutes les cartes à puce des utilisateurs en ligne
- [ ] V5 (facultative) : gérer le serveur à partir de sa console
	- [ ] Enumérer les utilisateurs en ligne "/list"
	- [ ] "/kill foo"
	- [ ] Quand parler à tous : [SYSTEM] Hello world!
	- [ ] Arrêter le serveur dans 2min en avertissant les utilisateurs en ligne "/shutdown 2"