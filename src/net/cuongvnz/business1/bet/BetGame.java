package net.cuongvnz.business1.bet;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;

public class BetGame {

    public HashMap<Player, Boolean> states = new HashMap<>();
    public ArrayList<BetProfile> profiles = new ArrayList<>();

    public Inventory gameGui = null;

    public BetItem winItem = null;

    public Boolean inProgress = false;
    public int taskID;

    public BetGame(){

    }

    public void announce(String msg){
        for(BetProfile bp : profiles){
            bp.player.sendMessage(msg);
        }
    }

    public boolean contains(Player p){
        for(BetProfile bp : profiles){
            if(bp.player == p) return true;
        }
        return false;
    }

    public BetProfile getProfile(Player p){
        for(BetProfile bp : profiles){
            if(bp.player == p) return bp;
        }
        return null;
    }
}
