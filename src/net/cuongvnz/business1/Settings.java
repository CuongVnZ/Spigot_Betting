package net.cuongvnz.business1;

import net.cuongvnz.business1.bet.BetManager;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    public static int TIME_TO_START = 60;
    public static int TIME_ROLL = 5;
    public static double ROLL_SPEED = 0.25;
    public static double BET_MIN = 100.0;
    public static double BET_MAX = 10000.0;

    public static String SOUND_ROLLING = "UI_BUTTON_CLICK";
    public static String SOUND_RESULT = "ENTITY_FIREWORK_BLAST";

    public static String GUI_MESSAGE_START_GAME = "§cTrò chơi đã diễn ra. Đang quay phần thưởng...";
    public static String GUI_MESSAGE_START_NEW_GAME = "§cTrò chơi mới sẽ diễn ra sau %time% giây";
    public static String GUI_MESSAGE_NO_GAME = "§cHiện tại không có trò chơi nào đang diễn ra";

    public static String MESSAGE_ENTER_AMOUNT = "§eVui lòng nhập số tiền muốn bet vào thanh chat!";
    public static String MESSAGE_ENTER_AMOUNT_TITLE = "§a§lNhập số tiền";
    public static String MESSAGE_BET = "§aBạn đã bet số tiền %amount% vào %item%";
    public static String MESSAGE_NOT_ENOUGH = "§cBạn không có đủ tiền";
    public static String MESSAGE_WINNER = "§b%player% đã giành chiến thắng nhận được %amount%";
    public static String MESSAGE_END = "§eTrò chơi kết thúc.";

    public static String GUI_TITLE = "§cBetting Game";
    public static int GUI_ROWS = 3;

    public static void reload(){
        FileConfiguration config = BettingPlugin.plugin.getConfig();

        GUI_MESSAGE_START_GAME = config.getString("LANGUAGE.GUI_MESSAGE_START_GAME");
        GUI_MESSAGE_START_NEW_GAME = config.getString("LANGUAGE.GUI_MESSAGE_START_NEW_GAME");
        GUI_MESSAGE_NO_GAME = config.getString("LANGUAGE.GUI_MESSAGE_NO_GAME");

        MESSAGE_ENTER_AMOUNT = config.getString("LANGUAGE.MESSAGE_ENTER_AMOUNT");
        MESSAGE_ENTER_AMOUNT_TITLE = config.getString("LANGUAGE.MESSAGE_ENTER_AMOUNT_TITLE");
        MESSAGE_BET = config.getString("LANGUAGE.MESSAGE_BET");
        MESSAGE_NOT_ENOUGH = config.getString("LANGUAGE.MESSAGE_NOT_ENOUGH");
        MESSAGE_WINNER = config.getString("LANGUAGE.MESSAGE_WINNER");
        MESSAGE_END = config.getString("LANGUAGE.MESSAGE_END");

        TIME_TO_START = config.getInt("GAME_SETTINGS.TIME_TO_START");
        TIME_ROLL = config.getInt("GAME_SETTINGS.TIME_ROLL");
        BET_MIN = config.getDouble("GAME_SETTINGS.BET.MIN");
        BET_MAX = config.getDouble("GAME_SETTINGS.BET.MAX");
        ROLL_SPEED = config.getDouble("GAME_SETTINGS.ROLL_SPEED");
        SOUND_ROLLING = config.getString("GAME_SETTINGS.SOUND.ROLLING");
        SOUND_RESULT = config.getString("GAME_SETTINGS.SOUND.RESULT");
        GUI_TITLE = config.getString("GAME_SETTINGS.BET_GUI.TITLE");
        GUI_ROWS = config.getInt("GAME_SETTINGS.BET_GUI.ROWS");
        BetManager.reload();
    }

}
