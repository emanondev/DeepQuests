package emanondev.quests.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemEdit extends CmdManager {

	public ItemEdit() {
		super("itemedit", Arrays.asList("ie","iedit"), null, 
				new Rename(),new Lore());
		
		//solo i player potranno usare questo sottocomando,
		//di conseguenza tutti i sottocomandi potranno avere accesso solo da player
		//di default false
		this.setPlayersOnly(true);
		
		//nasconde l'esistenza del sottocomando rename a chi 
		//non ha il relativo permesso itemedit.rename
		//di default true
		this.setShowLockedSuggestions(false);
		
	}

}

class Rename extends SubCmdManager {

	public Rename() {
		super(Arrays.asList("rename","name"),"itemedit.rename");
		
		this.setParams("[nome oggetto]");
		
		this.setDescription(Arrays.asList("&6permette di modificare il nome di un oggetto",
				"",
				"&cNota: se il testo non è specificato avrà nome vuoto"));

	}
	
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		//cast sicuro perchè il comando padre definisce player only
		Player p = (Player) sender;
		
		StringBuilder text = new StringBuilder("");
		for (String param:params) {
			text.append(param+" ");
		}
		//quindi si prende l'oggetto da player se si setta come titolo text.toString()...
	}
}

class Lore extends SubCmdManager {
	public Lore() {
		super("lore",null,new LoreAdd(),new LoreSet(),new LoreRemove());
		
		//mostrerà a tutti l'esistenza dei sottocomandi anche se non si hanno i permessi
		//marcherà però i sottocomandi a cui non si ha accesso di colore rosso
		//di default true qui è scritto solo a scopo esplicativo
		this.setShowLockedSuggestions(true);
		
		//scrive i parametri del sottocomando
		this.setParams("<add,set,remove> [...]");
		
		//descrive il comando e eventualmente i parametri
		this.setDescription(Arrays.asList("&6permette di modificare la lore",
				"&6dell'oggetto"));

	}
}

class LoreAdd extends SubCmdManager {
	public LoreAdd() {
		super("add","itemedit.lore.add");
		
		//scrive i parametri del sottocomando
		this.setParams("[text]");
		
		//descrive il comando e eventualmente i parametri
		this.setDescription(Arrays.asList("&6aggiunge una nuova linea in fondo con [text]",
				"&cNota se text non è specificato aggiunge linea vuota"));

	}
	
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		//cast sicuro perchè il comando padre definisce player only
		Player p = (Player) sender;
		
		StringBuilder text = new StringBuilder("");
		for (String param:params) {
			text.append(param+" ");
		}
		//quindi si prende l'oggetto da player ed aggiunge text.toString() alla lore...
	}
	
}

class LoreSet extends SubCmdManager {
	public LoreSet() {
		super("set","itemedit.lore.set");
		
		//scrive i parametri del sottocomando
		this.setParams("<linea> [text]");
		
		//descrive il comando e eventualmente i parametri
		this.setDescription(Arrays.asList("&6permette di settare linee della lore",
				"&6dell'oggetto","&cLa linea indicata deve essere >= 1"));

	}
	
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		//cast sicuro perchè il comando padre definisce player only
		Player p = (Player) sender;
		
		//se non è specificata la linea
		if (params.size()==0) {
			onHelp(params,sender,label,args);
			return;
		}
		
		String number = params.remove(0);
		//check, se la linea non è un numero onHelp()
		
		
		StringBuilder text = new StringBuilder("");
		for (String param:params) {
			text.append(param+" ");
		}
		//si setta la linea number-1 della lore a text.toString()...
	}
}

class LoreRemove extends SubCmdManager {
	public LoreRemove() {
		super(Arrays.asList("remove","delete"),"itemedit.lore.remove");
		
		//scrive i parametri del sottocomando
		this.setParams("<linea>");
		
		//descrive il comando e eventualmente i parametri
		this.setDescription(Arrays.asList("&6permette di rimuovere linee di lore",
				"&6dell'oggetto","&cLa linea indicata deve essere >= 1 e minore del numero di linee"));

	}
	
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		//cast sicuro perchè il comando padre definisce player only
		Player p = (Player) sender;
		
		//se non è specificata la linea
		if (params.size()==0) {
			onHelp(params,sender,label,args);
			return;
		}
		
		String number = params.remove(0);
		//check, se la linea non è un numero onHelp()
		//si rimuove la linea number-1 della lore...
	}
	
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		//cast sicuro perchè il comando padre definisce player only
		Player p = (Player) sender;
		ItemStack item = p.getInventory().getItemInMainHand();
		//se non è specificata la linea
		if (item==null||!item.getItemMeta().hasLore()) {
			return new ArrayList<>();
		}
		int loreSize = item.getItemMeta().getLore().size();
		
		//aggiungo le possibili linee da rimuovere
		ArrayList<String> result = new ArrayList<>();
		for (int i=0;i<loreSize;i++)
			result.add(""+(i+1));
		
		return result;
	}
}