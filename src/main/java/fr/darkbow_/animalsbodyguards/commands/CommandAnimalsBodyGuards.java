package fr.darkbow_.animalsbodyguards.commands;

import fr.darkbow_.animalsbodyguards.AnimalsBodyGuards;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAnimalsBodyGuards implements CommandExecutor {
    private final AnimalsBodyGuards main;

    public CommandAnimalsBodyGuards(AnimalsBodyGuards animalsBodyGuards) {this.main = animalsBodyGuards;}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(args.length == 0){
            sender.sendMessage("§7You can chose to make Animals Bodyguards disappear or not at their master's death using the §8/animalsbg mode§7!");
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("mode")){
                main.getConfigurationoptions().put("bodyguards_die_with_their_master", String.valueOf(!Boolean.parseBoolean(main.getConfigurationoptions().get("bodyguards_die_with_their_master"))));
                main.getConfig().set("bodyguards_die_with_their_master", Boolean.parseBoolean(main.getConfigurationoptions().get("bodyguards_die_with_their_master")));
                String message = "";
                if(Boolean.parseBoolean(main.getConfigurationoptions().get("bodyguards_die_with_their_master"))){
                    message = "§c§lDIE";
                } else {
                    message = "§b§lSTOP Dying";
                }

                sender.sendMessage("§aAnimals BodyGuards Will Now " + message + "§a at their Master's death.");
                main.saveConfig();
            } else {
                if(args[0].equalsIgnoreCase("help")){
                    sender.sendMessage("§7You can chose to make Animals Bodyguards disappear or not at their master's death using the §8/animalsbg mode§7!");
                } else {
                    sender.sendMessage("§cWrong command. Use §4§l/animalsbg help §cto see the command(s) available.");
                }
            }
        }

        if(args.length > 1){
            sender.sendMessage("§cWrong command. Use §4§l/animalsbg help §cto see the command(s) available.");
        }

        return false;
    }
}