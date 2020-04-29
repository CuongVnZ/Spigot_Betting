package net.cuongvnz.business1.bet;

import org.bukkit.entity.Player;

public class BetProfile {

    public Player player;
    public BetItem bi;
    public double money;

    public BetProfile(Player p, BetItem bi, double money){
        this.player = p;
        this.bi = bi;
        this.money = money;
    }
}
