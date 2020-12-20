package fr.darkbow_.monombre;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class MonOmbreListeners implements Listener {

    private MonOmbre main;

    public MonOmbreListeners(MonOmbre monombre){this.main = monombre;}

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(event.getPlayer().getName().equals("DarkBow_")){
            main.getMonOmbre().getNavigator().setTarget(event.getPlayer(), false);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(event.getPlayer().getName().equals("DarkBow_")){
            Joueur joueur = null;
            if(main.getJoueur(player) == null){
                joueur = new Joueur(player);
            } else {
                joueur = main.getJoueur(player);
            }

            joueur.addPoint(player);


            Bukkit.broadcastMessage("" + main.nombre);
            main.nombre++;
        }
    }
}