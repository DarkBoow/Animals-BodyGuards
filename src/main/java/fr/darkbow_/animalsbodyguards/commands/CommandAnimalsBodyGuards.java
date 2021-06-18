package fr.darkbow_.animalsbodyguards.commands;

import fr.darkbow_.animalsbodyguards.AnimalsBodyGuards;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

public class CommandAnimalsBodyGuards implements CommandExecutor {
    private final AnimalsBodyGuards main;

    public CommandAnimalsBodyGuards(AnimalsBodyGuards animalsBodyGuards) {this.main = animalsBodyGuards;}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(args.length == 0){
            sender.sendMessage("§8/animalsbg mode §7: Choose to make Animals Bodyguards disappear or not after their master's death.\n§8/animalsbg names §7: Change Bodyguards and Masters names visibility.");
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("mode")){
                main.getConfigurationoptions().put("bodyguards_die_with_their_master", String.valueOf(!Boolean.parseBoolean(main.getConfigurationoptions().get("bodyguards_die_with_their_master"))));
                main.getConfig().set("bodyguards_die_with_their_master", Boolean.parseBoolean(main.getConfigurationoptions().get("bodyguards_die_with_their_master")));
                String message;
                if(Boolean.parseBoolean(main.getConfigurationoptions().get("bodyguards_die_with_their_master"))){
                    message = "§c§lDIE";
                } else {
                    message = "§b§lSTOP Dying";
                }

                sender.sendMessage("§aAnimals BodyGuards Will Now " + message + "§a at their Master's death.");
                main.saveConfig();
            } else if(args[0].equalsIgnoreCase("names")) {
                main.getConfigurationoptions().put("special_names", String.valueOf(!Boolean.parseBoolean(main.getConfigurationoptions().get("special_names"))));
                main.getConfig().set("special_names", Boolean.parseBoolean(main.getConfigurationoptions().get("special_names")));
                String status;
                if(Boolean.parseBoolean(main.getConfigurationoptions().get("special_names"))){
                    status = "§aON";
                    if(!main.getBodyguards().isEmpty()){
                        for(Map.Entry<Entity, List<Entity>> bodyguardsmap : main.getBodyguards().entrySet()){
                            if(main.getCustomname().containsKey(bodyguardsmap.getKey())){
                                bodyguardsmap.getKey().setCustomName(main.getCustomname().get(bodyguardsmap.getKey()));
                                bodyguardsmap.getKey().setCustomNameVisible(true);
                            }

                            if(bodyguardsmap.getValue().size() > 0){
                                for(Entity bodyguard : bodyguardsmap.getValue()){
                                    if(main.getCustomname().containsKey(bodyguard)){
                                        bodyguard.setCustomName(main.getCustomname().get(bodyguard));
                                        bodyguard.setCustomNameVisible(true);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    status = "§cOFF";
                    if(!main.getBodyguards().isEmpty()){
                        for(Map.Entry<Entity, List<Entity>> bodyguardsmap : main.getBodyguards().entrySet()){
                            if(main.getPreviousCustomName().containsKey(bodyguardsmap.getKey())){
                                bodyguardsmap.getKey().setCustomName(main.getPreviousCustomName().get(bodyguardsmap.getKey()));
                            }

                            if(main.getCustomNameWasVisible().containsKey(bodyguardsmap.getKey())){
                                bodyguardsmap.getKey().setCustomNameVisible(main.getCustomNameWasVisible().get(bodyguardsmap.getKey()));
                            }

                            if(bodyguardsmap.getValue().size() > 0){
                                for(Entity bodyguard : bodyguardsmap.getValue()){
                                    if(main.getPreviousCustomName().containsKey(bodyguard)){
                                        bodyguard.setCustomName(main.getPreviousCustomName().get(bodyguard));
                                    }
                                    bodyguard.setCustomNameVisible(false);
                                }
                            }
                        }
                    }
                }

                sender.sendMessage("§3Custom Names " + status + "§3.");
            } else {
                if(args[0].equalsIgnoreCase("help")){
                    sender.sendMessage("§8/animalsbg mode §7: Choose to make Animals Bodyguards disappear or not after their master's death.\n§8/animalsbg names §7: Change Masters and Bodyguards names visibility.");
                } else {
                    sender.sendMessage("§cWrong command. Use §4§l/animalsbg help §cto see the commands available.");
                }
            }
        }

        if(args.length > 1){
            sender.sendMessage("§cWrong command. Use §4§l/animalsbg help §cto see the commands available.");
        }

        return false;
    }
}