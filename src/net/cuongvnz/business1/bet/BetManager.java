package net.cuongvnz.business1.bet;

import net.cuongvnz.business1.AbstractManager;
import net.cuongvnz.business1.Settings;
import net.cuongvnz.business1.menus.MenuItem;
import net.cuongvnz.business1.menus.MenuManager;
import net.cuongvnz.business1.utils.RMessages;
import net.cuongvnz.business1.utils.RScheduler;
import net.cuongvnz.business1.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.cuongvnz.business1.BettingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.List;

public class BetManager extends AbstractManager {

	public static ArrayList<BetGame> gameList = new ArrayList<>();

	public static ArrayList<BetItem> betItems = new ArrayList<>();

	public BetManager(BettingPlugin pl) {
		super(pl);
	}

	@Override
	public void initialize() {
		reload();
	}

	public static void reload(){
		betItems.clear();
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("REWARDS");
		for(String key : section.getKeys(false)){
			String path = "REWARDS."+key;
			ItemStack item = new ItemStack(Material.valueOf(
					plugin.getConfig().getString(path+".MATERIAL")));
			List<String> cmds = plugin.getConfig().getStringList(path+".REWARD_COMMANDS");

			ItemMeta im = item.getItemMeta();
			im.setDisplayName(plugin.getConfig().getString(path+".META.NAME"));
			im.setLore(plugin.getConfig().getStringList(path+".META.LORES"));
			item.setItemMeta(im);

			int row = plugin.getConfig().getInt(path+".ROW");
			int col = plugin.getConfig().getInt(path+".COL");

			BetItem bi = new BetItem(item, row, col, cmds);

			bi.chance = plugin.getConfig().getDouble(path+".CHANCE");
			bi.rewardMult = plugin.getConfig().getDouble(path+".REWARD_MULTIPLIER");

			betItems.add(bi);
		}
	}

	public static void showGames(Player p, int page){
		ArrayList<Object> temp = new ArrayList<>();
		for(BetGame game : gameList){
			if(game.contains(p)) {
				p.closeInventory();
				showGui(p, game);
				return;
			}
			temp.add(game);
		}

		int maxpage = (int) Math.ceil(temp.size()/45);
		if(page > maxpage) return;

		ArrayList<Object> pagetemp = new ArrayList<>(Utils.getPage(temp, page));

		int nextpage = page+1;
		int prepage = page-1;

		String title = "Danh sách games";
		Inventory inventory = MenuManager.createMenu(p, title, 6, new Object[][] {});
		int i = 0;
		int index = 0;
		int col = 0;
		int row = 0;
		for (Object o : pagetemp) {
			BetGame game = (BetGame) o;
			ArrayList<String> lores = new ArrayList<String>();
			lores.add("");
			lores.add("§7Loại: Roulette");
			lores.add("§7Số người: " + game.profiles.size());
			if (index > 8) {
				index = 0;
				row++;
			}
			col = index;
			index++;
			MenuManager.modifyMenu(p, inventory, title, new Object[][] {
					{
							row,
							col,
							Material.SIGN,
							"Game #" + i,
							lores,
							(Runnable) () -> showGui(p, game)
					}
			});
			i++;
		}
		if(page > 0) {
			MenuManager.modifyMenu(p, inventory, title, new Object[][] {
					{
							5,
							0,
							Material.PAPER,
							ChatColor.WHITE + "Trang " + prepage,
							new Object[] {
									null,
									"",
									ChatColor.GRAY,
									"Nhấp chuột để đổi trang"
							},
							(Runnable) () -> {
								p.closeInventory();
								showGames(p, prepage);
							}
					}
			});
		}
		if(page < maxpage) {
			MenuManager.modifyMenu(p, inventory, title, new Object[][] {
					{
							5,
							8,
							Material.PAPER,
							ChatColor.WHITE + "Trang " + nextpage,
							new Object[] {
									null,
									"",
									ChatColor.GRAY,
									"Nhấp chuột để đổi trang"
							},
							(Runnable) () -> {
								p.closeInventory();
								showGames(p, nextpage);
							}
					}
			});
		}
		p.openInventory(inventory);
	}

    public static void showGui(Player p, BetGame game){
		p.closeInventory();
		if(game == null){
			p.sendMessage(Settings.GUI_MESSAGE_NO_GAME);
		}else if(game.inProgress){
			p.openInventory(game.gameGui);
		}else{
			ArrayList<MenuItem> listBetItems = new ArrayList<>();
			for(BetItem bi: betItems){
				List<String> lores = bi.display.getItemMeta().getLore();
				lores.add("");
				for(BetProfile bp : game.profiles){
					if(bp.bi == bi){
						lores.add("§f"+bp.player.getName() + ": §7" + bp.money);
					}
				}
				MenuItem mi = new MenuItem(bi.row, bi.col,
						bi.display.clone(),
						bi.display.getItemMeta().getDisplayName(),
						lores, () -> {
							p.sendMessage(Settings.MESSAGE_ENTER_AMOUNT);
							RMessages.sendTitle(p, Settings.MESSAGE_ENTER_AMOUNT_TITLE, "", 15, 15, 15);
							if(game.states.containsKey(p)){
								game.states.replace(p, true);
								for(BetProfile bp : game.profiles){
									if(bp.player == p) bp.bi = bi;
								}
							}else{
								BetProfile bp = new BetProfile(p, bi, 0);
								game.profiles.add(bp);
								game.states.put(p, true);
							}
							p.closeInventory();
						});
				listBetItems.add(mi);
			}
			Inventory inv = MenuManager.createMenu(p, Settings.GUI_TITLE, Settings.GUI_ROWS , listBetItems);
			p.openInventory(inv);
		}
	}

	public static void startNewGame(Player p){
		BetGame game = new BetGame();
		for(Player p2 : Bukkit.getOnlinePlayers()){
			String msg = Settings.GUI_MESSAGE_START_NEW_GAME;
			msg = msg.replace("%time%", ""+Settings.TIME_TO_START);
			p2.sendMessage(msg);
		}
		game.taskID = RScheduler.schedule(plugin, ()->{
			startGame(game);
		}, Settings.TIME_TO_START*20);
		gameList.add(game);
		showGui(p, game);
	}

	public static void startGame(BetGame game){
		if(game == null) return;
		Bukkit.getScheduler().cancelTask(game.taskID);

		Inventory inv = Bukkit.createInventory(null, 45, "ROLLING");
		for(int i = 0; i<45; i++){
			List<Integer> blackList = Arrays.asList(4,13,22,31,40);
			List<Integer> whiteList = Arrays.asList(18,19,20,21,23,24,25,26);
			if(!blackList.contains(i)) {
				inv.setItem(i, new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 15));
			}
			if(whiteList.contains(i)) {
				inv.setItem(i, new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 5));
			}
		}
		for(BetProfile bp : game.profiles){
			plugin.econ.withdrawPlayer(bp.player, bp.money);
			bp.player.sendMessage(Settings.GUI_MESSAGE_START_GAME);
			bp.player.closeInventory();
			bp.player.openInventory(inv);
		}
		game.gameGui = inv;
		game.inProgress = true;
		handleGame(game, getRandomItems(game));
	}

	private static void handleGame(BetGame game, ArrayList<BetItem> bis){
		if(game == null) return;
		game.inProgress = true;
		Inventory inv = game.gameGui;
		int loop = (int) (Settings.TIME_ROLL/Settings.ROLL_SPEED);
		for(int i = 1; i <= loop; i++) {
			int finalI = i;
			RScheduler.schedule(plugin, ()->{
				inv.setItem(4, bis.get(finalI +3).rollDisplay);
				inv.setItem(13, bis.get(finalI +2).rollDisplay);
				inv.setItem(22, bis.get(finalI +1).rollDisplay);
				inv.setItem(31, bis.get(finalI).rollDisplay);
				inv.setItem(40, bis.get(finalI -1).rollDisplay);
				for(BetProfile bp : game.profiles){
					bp.player.playSound(bp.player.getLocation(), Sound.valueOf(Settings.SOUND_ROLLING), 20, 40);
				}
				if(finalI==loop){
					for(BetProfile bp: game.profiles){
						if(bp.bi == bis.get(finalI+1)) {
							double reward = bp.money * bp.bi.rewardMult;
							Player winner = bp.player;
							for (Player p : Bukkit.getOnlinePlayers()) {
								String msg = Settings.MESSAGE_WINNER;
								msg = msg.replace("%player%", winner.getName());
								msg = msg.replace("%amount%", ""+reward);
								p.sendMessage(msg);
								for(String cmd : bp.bi.cmds){
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName()));
								}
							}
							System.out.println(reward);
							plugin.econ.depositPlayer(winner, reward);
						}
						bp.player.sendMessage("§dKết quả: §a" + game.winItem.display.getItemMeta().getDisplayName());
					}
					for(BetProfile bp : game.profiles){
						bp.player.playSound(bp.player.getLocation(), Sound.valueOf(Settings.SOUND_RESULT), 60, 60);
					}
					clearGame(game);
				}
			}, (int) (i*Settings.ROLL_SPEED*20));
		}
	}

	private static void clearGame(BetGame game){
		game.announce(Settings.MESSAGE_END);
		gameList.remove(game);
		game = null;
	}

	private static ArrayList<BetItem> getRandomItems(BetGame game){
		Random random = new Random();
		ArrayList<BetItem> list = new ArrayList<>();
		int loop = (int) (Settings.TIME_ROLL/Settings.ROLL_SPEED)+5;
		for(int i = 1; i <= loop; i++) {
			double rand = Math.random();
			BetItem bi = betItems.get(random.nextInt(betItems.size()));

			List<Double> chances = new ArrayList<Double>();
			for(BetItem temp : betItems){
				chances.add(temp.chance);
			}
			Collections.sort(chances, Comparator.reverseOrder());
			for(double chance : chances){
				if(rand<=chance){
					boolean done = false;
					for(BetItem bitemp : betItems){
						if(bi.chance==chance) {
							bi = bitemp;
							done = true;
						}
					}
					if(!done) {
						for(BetItem bitemp : betItems){
							if(bi.chance==chances.get(0)) {
								bi = bitemp;
							}
						}
					}
				}
			}

			if(i == loop-3){
				game.winItem = bi;
			}
			list.add(bi);
		}
		return list;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		for(BetGame game : gameList) {
			if (game == null) return;
			Player p = e.getPlayer();
			if (game.states.containsKey(p) && game.states.get(p)) {
				if(game.contains(p)) {
					BetProfile bp = game.getProfile(p);
					double money = Double.parseDouble(e.getMessage());
					if (plugin.econ.getBalance(p) >= money) {
						if (money >= Settings.BET_MIN
								&& money <= Settings.BET_MAX) {
							bp.money = money;
							String msg = Settings.MESSAGE_BET.replace("%amount%", ""+money);
							msg = msg.replace("%item%", bp.bi.display.getItemMeta().getDisplayName());
							p.sendMessage(msg);
						} else {
							p.sendMessage("§cSố tiền bet phải từ §f" + Settings.BET_MIN + " §cđến §f" + Settings.BET_MAX);
						}
					} else {
						p.sendMessage(Settings.MESSAGE_NOT_ENOUGH);
					}
					//showGui(p, game);
					game.states.replace(p, false);
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event){
		if(event.getInventory().getName().equals("ROLLING")){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event){
		for(BetGame game : gameList) {
			if (game != null
					&& game.inProgress
					&& event.getInventory().getName().equals("ROLLING")) {
				RScheduler.schedule(plugin, () -> {
					Player p = (Player) event.getPlayer();
					p.closeInventory();
					p.openInventory(game.gameGui);
				}, 15);
			}
		}
	}

}
