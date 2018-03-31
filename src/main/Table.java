package main;

import java.util.ArrayList;

public class Table {
	private int noTable;

	private ArrayList<Client> clients;
	
	public static ArrayList<Table> listeTables = new ArrayList<Table>();
	
	public Table(int noTable){
		this.noTable = noTable;
		clients = new ArrayList<Client>();
		listeTables.add(this);
	}
	
	//Ajoute le client � la table avec le no recu en param�tre. Si elle n'existe pas, elle est cr�e et le client est ajout�
	public static void ajouterClient(Client cli, int noTable){
		int index = chercherTable(noTable);
		if (index != -1) {
			listeTables.get(index).clients.add(cli);
		}else {
			new Table(noTable);
			listeTables.get(listeTables.size()-1).clients.add(cli);
		}
	}
	
	public static int chercherTable(int noTable){
		int index = -1;
		for (int i = 0; i < listeTables.size(); i++) {
			if (listeTables.get(i).getNoTable()==noTable) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public int getNoTable() {
		return noTable;
	}

	public void setNoTable(int noTable) {
		this.noTable = noTable;
	}

}