package net.cuongvnz.business1.bet;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BetItem {

    public ItemStack rollDisplay;
    public ItemStack display;

    public int row;
    public int col;

    public double chance = 1;
    public double rewardMult = 1;

    public List<String> cmds;

    public BetItem(ItemStack display, int row, int col, List<String> cmds){
        this.display = display;

        ItemMeta im = display.getItemMeta();
        im.setLore(null);
        this.rollDisplay = display.clone();
        this.rollDisplay.setItemMeta(im);

        this.row = row;
        this.col = col;
        this.cmds = cmds;
    }

}
