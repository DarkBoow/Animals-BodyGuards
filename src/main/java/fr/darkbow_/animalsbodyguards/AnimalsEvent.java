package fr.darkbow_.animalsbodyguards;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import java.util.*;

public class AnimalsEvent implements Listener {
    private AnimalsBodyGuards main;

    public AnimalsEvent(AnimalsBodyGuards animalsbodyguards){this.main = animalsbodyguards;}

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(!main.getBodyGuardsTypes().contains(event.getEntityType()) && (event.getEntity() instanceof Animals || AnimalsBodyGuards.PassiveEntities.contains(event.getEntityType()))){
            Entity target = null;
            if(event.getDamager() instanceof Projectile) {
                target = (Entity) ((Projectile) event.getDamager()).getShooter();
            } else {
                target = event.getDamager();
            }

            Entity bodyguard = main.spawnBodyGuard(event.getEntity(), target);
            if(bodyguard instanceof Creature && target instanceof LivingEntity){
                ((Creature) bodyguard).setTarget((LivingEntity) target);
                if(main.getDefendOwner().get(event.getEntity()).contains(target)){
                    main.getDefendOwner().get(event.getEntity()).add(target);
                }
            }
        }

        if(event.getEntity() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) event.getEntity();
            if(entity.getHealth() - event.getFinalDamage() <= 0){
                if(!main.getDefendOwner().isEmpty()){
                    for(Map.Entry<Entity,List<Entity>> map : main.getDefendOwner().entrySet()){
                        if(map.getValue().contains(event.getEntity())){
                            map.getValue().remove(event.getEntity());
                        }

                        if(map.getValue().isEmpty()){
                            main.getDefendOwner().remove(map.getKey());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Animals || AnimalsBodyGuards.PassiveEntities.contains(event.getEntityType())){
            if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
                main.spawnBodyGuard(event.getEntity(), null);
            }
        }
    }

    @EventHandler
    public void EntityDeath(EntityDeathEvent event){
        if(event.getEntity() instanceof Animals || AnimalsBodyGuards.PassiveEntities.contains(event.getEntityType())){
            if(!main.getBodyguards().isEmpty()){
                if(main.getBodyguards().containsKey(event.getEntity())){
                    for(Entity bodyguard : main.getBodyguards().get(event.getEntity())){
                        bodyguard.remove();
                    }
                    main.getBodyguards().remove(event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void OnEntityTarget(EntityTargetEvent event){
        if(!main.getBodyguards().isEmpty()){
            boolean cancelled = false;
            if(main.getBodyguards().containsKey(event.getTarget())){
                if(main.getBodyguards().get(event.getTarget()).contains(event.getEntity())){
                    cancelled = true;
                    event.setCancelled(true);
                    if(event.getEntity() instanceof Player || event.getTarget() instanceof Player){
                        Bukkit.broadcastMessage("1");
                    }
                }
            }

            Entity master = null;
            if(!cancelled){
                for(Map.Entry<Entity, List<Entity>> map : main.getBodyguards().entrySet()){
                    if(map.getValue().contains(event.getEntity())){
                        master = map.getKey();
                        if(event.getTarget() == map.getKey()){
                            cancelled = true;
                            event.setCancelled(true);
                            Bukkit.broadcastMessage("2");
                        }
                    }
                }
            }

            if(master != null){
                if(main.getDefendOwner().containsKey(master)){
                    if(!main.getDefendOwner().get(master).contains(event.getTarget())){
                        cancelled = true;
                    }
                }
            }
        }
    }
}