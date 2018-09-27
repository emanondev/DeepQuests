package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class ItemBuilder {
	private ItemStack result;
	private ItemMeta resultMeta;
	public ItemBuilder setGuiProperty(){
		setUnbreakable(true);
		hideAllFlags();
		if (resultMeta.getDisplayName()== null)
			resultMeta.setDisplayName("");
		return this;
	}
	public ItemBuilder(Material result){
		this.result = new ItemStack(result);
		this.resultMeta = this.result.getItemMeta();
	}
	public ItemBuilder(ItemStack result){
		this.result = new ItemStack(result);
		this.resultMeta = this.result.getItemMeta();
	}
	public ItemStack build(){
		this.result.setItemMeta(this.resultMeta);
		return this.result;
	}
	public ItemBuilder setUnbreakable(boolean value){ //added
		this.resultMeta.setUnbreakable(value);
		return this;
	}
	public ItemBuilder addFlag(ItemFlag f){ //added
		this.resultMeta.addItemFlags(f);
		return this;
	}
	public ItemBuilder setDisplayName(String s){
		
		this.resultMeta.setDisplayName(s);
		return this;
	}
	public ItemBuilder addLore(String s){
		if ( s == null )
			return this;
		List<String> lore = new ArrayList<String>();
		if (this.resultMeta.hasLore()){
			lore = this.resultMeta.getLore();
		}
		lore.add(s);
		this.resultMeta.setLore(lore);
		return this;
	}
	public ItemBuilder setLore(List<String> lore){
		this.resultMeta.setLore(lore);
		return this;
	}
	public ItemBuilder addEnchantment(Enchantment enchantment,int level){
		this.resultMeta.addEnchant(enchantment,level,true);
		return this;
	}
	public ItemBuilder setArmorColor(Color color){
		LeatherArmorMeta meta = (LeatherArmorMeta) this.resultMeta;
		meta.setColor(color);
		return this;
	}
	public ItemBuilder setAuthor(String name){
		BookMeta meta = (BookMeta) this.resultMeta;
		meta.setAuthor(name);
		return this;
	}
	public ItemBuilder setTitle(String name){
		BookMeta meta = (BookMeta) this.resultMeta;
		meta.setTitle(name);
		return this;
	}
	public ItemBuilder setPage(int page,String text){
		BookMeta meta = (BookMeta) this.resultMeta;
		meta.setPage(page,text);
		return this;
	}
	public ItemBuilder addPotionEffect(PotionEffect effect){
		PotionMeta meta = (PotionMeta)this.resultMeta;
		meta.addCustomEffect(effect,true);
		return this;
	}
	@SuppressWarnings("deprecation")
	public ItemBuilder setSkullOwner(String name){
		SkullMeta meta = (SkullMeta) this.resultMeta;
		meta.setOwner(name);
		return this;
	}
	public ItemBuilder setDamage(int dmg){
		short dm = (short) dmg;
		this.result.setDurability(dm);
		return this;
	}
	public ItemBuilder setAmount(int amount){
		this.result.setAmount(amount);
		return this;
	}
	public ItemBuilder getCopy() {
		ItemBuilder copy = new ItemBuilder(this.result);
		copy.resultMeta = this.resultMeta.clone();
		return copy;
	}
	public ItemBuilder hideAllFlags() {
		for (int i = 0; i < ItemFlag.values().length;i++)
			this.addFlag(ItemFlag.values()[i]);
		return this;
	}
}
