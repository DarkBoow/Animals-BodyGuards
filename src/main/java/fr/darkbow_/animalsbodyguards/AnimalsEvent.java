package fr.darkbow_.animalsbodyguards;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.*;

public class AnimalsEvent implements Listener {
    private AnimalsBodyGuards main;

    public AnimalsEvent(AnimalsBodyGuards animalsbodyguards){this.main = animalsbodyguards;}

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Animals || AnimalsBodyGuards.PassiveEntities.contains(event.getEntityType())){
            Entity target = null;
            if(event.getDamager() instanceof Projectile) {
                target = (Entity) ((Projectile) event.getDamager()).getShooter();
            } else {
                event.getDamager();
            }

            if(main.getBodyGuardsTypes() != null){
                if(!main.getBodyGuardsTypes().isEmpty()){
                    Random r = new Random();
                    int creaturetype = r.nextInt(main.getBodyGuardsTypes().size());

                    if(main.getBodyGuardsTypes().get(creaturetype) != null){
                        Entity bodyguard = Objects.requireNonNull(event.getEntity().getLocation().getWorld()).spawnEntity(event.getEntity().getLocation(), main.getBodyGuardsTypes().get(creaturetype));
                        if(!main.getBodyguards().containsKey(event.getEntity())){
                            main.getBodyguards().put(event.getEntity(), new ArrayList<>());
                        }
                        main.getBodyguards().get(event.getEntity()).add(bodyguard);

                        String entityname = event.getEntityType().toString();
                        if(event.getEntity().getCustomName() == null){
                            if(main.getAnimalstypescount().containsKey(event.getEntityType())){
                                main.getAnimalstypescount().put(event.getEntityType(), main.getAnimalstypescount().get(event.getEntityType()) + 1);
                            } else {
                                main.getAnimalstypescount().put(event.getEntityType(), 1);
                            }

                            String prefix = "th";
                            switch (main.getAnimalstypescount().get(event.getEntityType())){
                                case 1:
                                    prefix = "st";
                                    break;
                                case 2:
                                    prefix = "nd";
                                    break;
                                case 3:
                                    prefix = "rd";
                                    break;
                            }
                            event.getEntity().setCustomName("§a§l" + main.getAnimalstypescount().get(event.getEntityType()) + prefix + " §6§l" + event.getEntityType().toString());
                            event.getEntity().setCustomNameVisible(true);
                        }
                        entityname = event.getEntity().getCustomName();

                        bodyguard.setCustomName("§a§l" + entityname + "§b§l's §6§lBodyGuard");
                        bodyguard.setCustomNameVisible(true);

                        if(bodyguard instanceof Creature){
                            ((Creature) bodyguard).setRemoveWhenFarAway(true);

                            boolean cancelled = false;
                            if(target != null){
                                if(!main.getBodyguards().isEmpty()){
                                    if(main.getBodyguards().containsKey(target)){
                                        if(main.getBodyguards().get(target).contains(event.getEntity())){
                                            cancelled = true;
                                        }
                                    }

                                    if(!cancelled){
                                        for(List<Entity> list : main.getBodyguards().values()){
                                            if(!cancelled && list.contains(event.getEntity())){
                                                cancelled = true;
                                            }
                                        }
                                    }
                                }
                            }

                            if(!cancelled){
                                ((Creature) bodyguard).setTarget((LivingEntity) target);
                            }
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
                Random r = new Random();
                int creaturetype = r.nextInt(main.getBodyGuardsTypes().size());
                while(main.getBodyGuardsTypes().get(creaturetype) == EntityType.ENDER_DRAGON && event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END){
                    creaturetype = r.nextInt(main.getBodyGuardsTypes().size());
                }

                Entity bodyguard = Objects.requireNonNull(event.getEntity().getLocation().getWorld()).spawnEntity(event.getEntity().getLocation(), main.getBodyGuardsTypes().get(creaturetype));
                String entityname = event.getEntityType().toString();
                if(event.getEntity().getCustomName() == null){
                    if(main.getAnimalstypescount().containsKey(event.getEntityType())){
                        main.getAnimalstypescount().put(event.getEntityType(), main.getAnimalstypescount().get(event.getEntityType()) + 1);
                    } else {
                        main.getAnimalstypescount().put(event.getEntityType(), 1);
                    }

                    String prefix = "th";
                    switch (main.getAnimalstypescount().get(event.getEntityType())){
                        case 1:
                            prefix = "st";
                            break;
                        case 2:
                            prefix = "nd";
                            break;
                        case 3:
                            prefix = "rd";
                            break;
                    }
                    event.getEntity().setCustomName("§a§l" + main.getAnimalstypescount().get(event.getEntityType()) + prefix + " §6§l" + event.getEntityType().toString());
                    event.getEntity().setCustomNameVisible(true);
                }
                entityname = event.getEntity().getCustomName();

                bodyguard.setCustomName("§a§l" + entityname + "§b§l's §6§lBodyGuard");
                bodyguard.setCustomNameVisible(true);
                if(bodyguard instanceof Creature){
                    ((Creature) bodyguard).setRemoveWhenFarAway(true);
                }

                if(!main.getBodyguards().containsKey(event.getEntity())){
                    main.getBodyguards().put(event.getEntity(), new ArrayList<>());
                }
                main.getBodyguards().get(event.getEntity()).add(bodyguard);
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

            if(!cancelled){
                for(Map.Entry<Entity, List<Entity>> map : main.getBodyguards().entrySet()){
                    if(map.getValue().contains(event.getEntity())){
                        if(event.getTarget() == map.getKey()){
                            cancelled = true;
                            event.setCancelled(true);
                            Bukkit.broadcastMessage("2");
                        }
                    }
                }
            }
        }
    }
}