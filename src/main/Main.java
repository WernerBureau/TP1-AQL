package main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main {

	static String nomFichier = "entree.txt";
	static String sortie;

	public static void main(String[] args) throws IOException {

		// Lecture du fichier entree.txt
		lireFichier(nomFichier);

	}

	private static void lireFichier(String entree) {

		String ligne = null;

		String[] lignes = new String[30];
		String[] texte;

		try {

			sortie = "";
			FileReader liseurFichier = new FileReader(nomFichier);
			BufferedReader liseurBuff = new BufferedReader(liseurFichier);

			// Ajout de toutes les lignes du fichier texte dans un tableau.
			int indice = 0;
			while ((ligne = liseurBuff.readLine()) != null) {
				lignes[indice] = ligne;
				indice++;
			}
			liseurBuff.close();

			// Création d'un nouveau tableau ayant la longueur du nombre de
			// lignes.
			texte = new String[indice];

			// Transfer du vieux tableau au nouveau tableau.
			for (int j = 0; j < texte.length; j++) {
				texte[j] = lignes[j];
			}

			int i1 = -1, i2 = -1, i3 = -1, i4 = -1;
			for (int i = 0; i < texte.length; i++) {
				if (texte[i].equals("Clients :")) {
					i1 = i;
				} else if (texte[i].equals("Plats :")) {
					i2 = i;
				} else if (texte[i].equals("Commandes :")) {
					i3 = i;
				}

				else if (texte[i].equals("Fin")) {
					i4 = i;
				}
			}

			// Clients
			lireClients(lignes, i1, i2);

			// Plats
			lirePlats(lignes, i2, i3);

			// Commandes
			lireCommandes(lignes, i3, i4);

			ecrireSortie();
			afficherSortie();
			
			//Création du nom de la facture
			String nomFichierSortie = creationFacture();

			FileWriter ecriveurFichier = new FileWriter(nomFichierSortie
					+ ".txt");
			BufferedWriter ecriveurBuff = new BufferedWriter(ecriveurFichier);

			ecrireFichierSortie(ecriveurBuff);

			ecriveurBuff.flush();
			ecriveurBuff.close();
			liseurFichier.close();
			liseurBuff.close();

		} catch (FileNotFoundException ex) {
			System.out.println("Impossible d'ouvrir le fichier '" + nomFichier
					+ "'");
		} catch (IOException ex) {
			System.out.println("Erreur lors de la lecture du fichier '"
					+ nomFichier + "'");
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out
					.println("Le fichier ne respecte pas le format demandé !");
		}
	}

	private static String creationFacture() {
		String nomFichierSortie = "Facture-du-";
		Calendar cal = Calendar.getInstance();

		nomFichierSortie += new SimpleDateFormat("dd-MMM-HH").format(cal
				.getTime())
				+ 'h'
				+ new SimpleDateFormat("mm").format(cal.getTime());
		return nomFichierSortie;
	}

	private static void lireCommandes(String[] lignes, int i3, int i4) {
		for (int i = 1; i < i4 - i3; i++) {
			sortie += lireLigneCommande(lignes[i3 + i]);
		}
	}

	private static void lirePlats(String[] lignes, int i2, int i3) {
		for (int i = 1; i < i3 - i2; i++) {
			sortie += lireLignePlat(lignes[i2 + i]);

		}
	}

	private static void lireClients(String[] lignes, int i1, int i2) {
		for (int i = 1; i < i2 - i1; i++) {
			sortie += lireLigneClient(lignes[i]);
		}
	}

	public static String lireLigneClient(String ligne) {
		
		String s = "";
		boolean clientExisteDeja = true;
		boolean clientContientTable = false;
		String clientNom = "";
		int clientTable = 0;
		clientExisteDeja = Client.chercherClient(ligne) >= 0;

		//Vérifier si la ligne client contient une table
		for (char c : ligne.toCharArray()) {
			if(Character.isDigit(c)){
				clientContientTable = true;
			}
		}
		
		// Il y a un espace
		/*
		if (clientExisteDeja) {
				s += "Erreur avec la ligne " + ligne + "\r\n";
				s += "\tLe client existe déjà.\r\n";
		} 
		*/
		
		if (!clientContientTable) {
			s += "Erreur avec la ligne " + ligne + "\r\n";
			s += "\tLe client ne fait pas partie d'une table.\r\n";
		}
		else {
			try {
				String[] clientInfos = ligne.split(" ");
				clientNom = clientInfos[0];
				try {
					clientTable = Integer.parseInt(clientInfos[1]);
				} catch (Exception e) {
					s += "Erreur avec la ligne " + ligne + "\r\n";
					s += "\tLe format de la table n'est pas respecté\r\n";
				}
				
				clientExisteDeja = Client.chercherClient(clientInfos[0]) >= 0;
				if (clientExisteDeja) {
					throw new IndexOutOfBoundsException();
				} 
				
				if (clientInfos.length == 2){
					if (! (clientTable < 100 && clientTable > 0))
						throw new NumberFormatException();
					else
						new Client(clientNom, clientTable);
				} else {
					throw new ArrayIndexOutOfBoundsException();
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				s += "Erreur avec la ligne " + ligne + "\r\n";
				s += "\tLe format de la ligne n'est pas respecté\r\n";
			} catch (NumberFormatException e) {
				s += "Erreur avec la ligne " + ligne + "\r\n";
				s += "\tLa table n'est pas un nombre valide (entre 1 et 99)\r\n";
			} catch (IndexOutOfBoundsException e) {
				s += "Erreur avec la ligne " + ligne + "\r\n";
				s += "\tLe client existe déjà.\r\n";
			}
		}
		
		return s;
	}

	public static String lireLignePlat(String ligne) {
		String s = "";
		String[] platSplit = ligne.split(" ");
		if (platSplit.length != 2) {
			s += "Erreur avec la ligne " + ligne
					+ "\r\n\tLe format de la ligne n'est pas respecté\r\n";
		}
		boolean platExisteDeja = false, prixValide = false;
		double prix = 0;
		try {

			platExisteDeja = (Plat.chercherPlat(platSplit[0]) >= 0);
			try {
				prix = Double.parseDouble(platSplit[1]);
				prixValide = true;
			} catch (NumberFormatException ex) {
				prixValide = false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		if (platExisteDeja || !prixValide) {
			// Il y a au moins une erreur;
			s += "Erreur avec la ligne " + ligne + "\r\n";
			s += platExisteDeja ? "\tLe plat existe déjà\r\n" : "";
			s += !prixValide ? "\tLe prix n'est pas valide\r\n" : "";
		} else {
			// Aucune erreur, ajouter la commande
			new Plat(platSplit[0], prix);
		}

		return s;

	}

	public static String lireLigneCommande(String ligne) {
		String s = "";
		String[] commandesSplit = ligne.split(" ");
		if (commandesSplit.length != 3) {
			s += "Erreur avec la ligne " + ligne
					+ "\r\n\tLe format de la ligne n'est pas respecté\r\n";
		}

		boolean clientExiste = false, platExiste = false, quantiteValide = false;
		int quantite = 0, indexClient = 0, indexPlat = 0;

		try {
			indexClient = Client.chercherClient(commandesSplit[0]);
			indexPlat = Plat.chercherPlat(commandesSplit[1]);
			clientExiste = (indexClient >= 0);
			platExiste = (indexPlat >= 0);

			try {
				quantite = Integer.parseInt(commandesSplit[2]);
				quantiteValide = true;
			} catch (NumberFormatException ex) {
				quantiteValide = false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			s += "Erreur avec la ligne " + ligne
					+ "\r\n\tLe format de la ligne n'est pas respecté\r\n";
		}

		if (!clientExiste || !platExiste || !quantiteValide) {
			// Il y a au moins une erreur;
			s += "Erreur avec la ligne " + ligne + "\r\n";
			s += !clientExiste ? "\tLe client n'existe pas\r\n" : "";
			s += !platExiste ? "\tLe plat n'existe pas\r\n" : "";
			s += !quantiteValide ? "\tLa quantité n'est pas valide\r\n" : "";
		} else {
			// Aucune erreur, ajouter la commande
			Commande com = new Commande(Plat.getPlat(indexPlat), quantite);
			Client.getClient(indexClient).ajouterCommande(com);
		}
		return s;
	}

	// Écrit les commandes correctes dans le string sortie
	private static void ecrireSortie() {
		if (sortie.length() > 0)
			sortie += "\r\n-------------------\r\n\r\n";
		sortie += "Bienvenue chez Barette!\r\nFactures:\r\n"
				+ Table.compilerFactures();
	}

	private static void afficherSortie() {
		System.out.println(sortie);
	}

	private static void ecrireFichierSortie(BufferedWriter ecriveurBuff)
			throws IOException {
		ecriveurBuff.write(sortie);
	}

}
