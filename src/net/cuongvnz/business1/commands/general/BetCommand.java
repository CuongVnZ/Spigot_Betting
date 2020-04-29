package net.cuongvnz.business1.commands.general;

import net.cuongvnz.business1.BettingPlugin;
import net.cuongvnz.business1.Settings;
import net.cuongvnz.business1.bet.BetManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.cuongvnz.business1.commands.AbstractCommand;

public class BetCommand extends AbstractCommand {

    public BetCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }
    
    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

	@Override
	public void executePlayer(Player p, String[] args) {
        if(args.length == 0) {
            BetManager.showGames(p,0);
        }else{
            try{
                switch (args[0]){
                    case "new":
                        if(!p.hasPermission("bettinggame.new")) return;
                        BetManager.startNewGame(p);
                        break;
                    case "forcestart":
                        //BetManager.startGame();
                        break;
                    case "stop":
                        break;
                    case "reload":
                        if(!p.hasPermission("bettinggame.reload")) return;
                        plugin.reloadConfig();
                        Settings.reload();
                        p.sendMessage("§7Reloaded configuration file.");
                        break;
                    case "help":
                        p.sendMessage("---------------");
                        p.sendMessage("§7/bet: Open gui");
                        p.sendMessage("§7/bet new: Start a new game");
                        p.sendMessage("§7/bet forcestart: Force start game");
                        p.sendMessage("§7/bet stop: Cancel the game");
                        p.sendMessage("§7/bet reload: Reload file configuration");
                        p.sendMessage("§7/bet help: Help commands");
                        break;
                }
            }catch (Exception e){
                p.sendMessage("---------------");
                p.sendMessage("§7/bet: Open gui");
                p.sendMessage("§7/bet new: Start a new game");
                p.sendMessage("§7/bet forcestart: Force start game");
                //p.sendMessage("/bet stop: Cancel the game");
                p.sendMessage("§7/bet reload: Reload file configuration");
                p.sendMessage("§7/bet help: Help commands");
            }
        }
	}
}
