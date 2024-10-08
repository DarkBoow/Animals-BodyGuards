package fr.darkbow_.animalsbodyguards;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import java.util.*;

public class AnimalsEvent implements Listener {
    private final AnimalsBodyGuards main;

    public AnimalsEvent(AnimalsBodyGuards animalsbodyguards){this.main = animalsbodyguards;}

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        Entity damager;
        if(event.getDamager() instanceof Projectile) {
            damager = (Entity) ((Projectile) event.getDamager()).getShooter();
        } else {
            damager = event.getDamager();
        }

        boolean cancelled = (damager instanceof Player && !main.getConfig().getBoolean("bodyguardsspawnsat.playersdamageanimals")) || (!(damager instanceof Player) && !main.getConfig().getBoolean("bodyguardsspawnsat.entitiesdamageanimals"));
        if(!cancelled){
            //Protection BodyGuards et Owner
            if(main.getBodyguards().containsKey(event.getEntity())){
                if(main.getBodyguards().get(event.getEntity()).contains(damager)){
                    cancelled = true;
                    event.setDamage(0);
                }
            }

            if(main.getBodyguardsowner().containsKey(damager)){
                if(main.getBodyguardsowner().containsKey(event.getEntity())){
                    if(main.getBodyguardsowner().get(damager) == main.getBodyguardsowner().get(event.getEntity())){
                        cancelled = true;
                        event.setDamage(0);
                    }
                }
            }
        }

        //Apparition de BodyGuard
        if(!cancelled){
            boolean newtarget = false;
            Entity master = null;
            if(main.getPassiveEntityTypes().contains(event.getEntityType())){
                newtarget = true;

                if(!main.getBodyguardsowner().containsKey(event.getEntity())){
                    Entity newbodyguard = main.spawnBodyGuard(event.getEntity(), damager);

                    master = event.getEntity();
                    main.getBodyguardsowner().put(newbodyguard, event.getEntity());
                    if(!main.getDefendOwner().containsKey(event.getEntity())){
                        main.getDefendOwner().put(event.getEntity(), new ArrayList<>());
                    }

                    if(!main.getDefendOwner().get(event.getEntity()).contains(damager)){
                        if(!main.getTargets().containsKey(damager)){
                            main.getTargets().put(damager, new ArrayList<>());
                        }

                        main.getTargets().get(damager).add(event.getEntity());
                        main.getDefendOwner().get(event.getEntity()).add(damager);
                    }

                    if(damager instanceof LivingEntity){
                        if(newbodyguard instanceof Creature){
                            ((Creature) newbodyguard).setTarget((LivingEntity) damager);
                        }
                    }
                }
            }

            //Ciblage Auto Si Pas De Cible
            if(main.getBodyguardsowner().containsKey(event.getEntity())){
                newtarget = true;
                master = main.getBodyguardsowner().get(event.getEntity());

                if(!main.getTargets().containsKey(damager)){
                    main.getTargets().put(damager, new ArrayList<>());
                }
                main.getTargets().get(damager).add(main.getBodyguardsowner().get(event.getEntity()));

                if(damager instanceof LivingEntity){
                    if(event.getEntity() instanceof Creature){
                        ((Creature) event.getEntity()).setTarget((LivingEntity) damager);
                    }
                }
            }

            if(newtarget){
                if(main.getDefendOwner().containsKey(master)){
                    if(!main.getDefendOwner().get(master).isEmpty()){
                        for(Entity bodyguard : main.getBodyguards().get(master)){
                            if(bodyguard instanceof LivingEntity){
                                if(bodyguard instanceof Creature){
                                    if(((Creature) bodyguard).getTarget() == null || Objects.requireNonNull(((Creature) bodyguard).getTarget()).isDead()){
                                        if(main.getDefendOwner().get(master).get(main.getDefendOwner().get(master).size() - 1) instanceof LivingEntity){
                                            ((Creature) bodyguard).setTarget((LivingEntity) main.getDefendOwner().get(master).get(main.getDefendOwner().get(master).size() - 1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Test Optionnel de Raison de Mort
            if(main.getBodyguards().containsKey(event.getEntity())){
                main.getLastdamager().put(event.getEntity(), damager);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getCause() != EntityDamageEvent.DamageCause.VOID && !event.getCause().name().contains("ENTITY") && event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE){
            if(main.getConfig().getBoolean("bodyguardsspawnsat.animalsnaturaldamages")){
                if(main.getBodyguards().containsKey(event.getEntity()) || main.getBodyguardsowner().containsKey(event.getEntity())){
                    if(event.getEntity() instanceof LivingEntity){
                        if(((LivingEntity) event.getEntity()).getHealth() - event.getFinalDamage() <= 0){
                            main.getLastdamager().remove(event.getEntity());
                        }
                    }
                }

                if(!main.getBodyguardsowner().containsKey(event.getEntity())){
                    if(main.getPassiveEntityTypes().contains(event.getEntityType())){
                        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK && event.getCause() != EntityDamageEvent.DamageCause.VOID){
                            main.spawnBodyGuard(event.getEntity(), null);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void EntityDeath(EntityDeathEvent event){
        //Mort des BodyGuards si Owner Mort
        if(main.getBodyguards().containsKey(event.getEntity())){
            if(!main.getTargets().isEmpty()){
                for(Map.Entry<Entity, List<Entity>> targetsmap : main.getTargets().entrySet()){
                    targetsmap.getValue().remove(event.getEntity());
                }
            }

            if(main.getDefendOwner().containsKey(event.getEntity())){
                for(Entity target : main.getDefendOwner().get(event.getEntity())){
                    if(main.getTargets().containsKey(target)){
                        if(main.getTargets().get(target).contains(event.getEntity())){
                            main.getTargets().get(target).remove(event.getEntity());
                            if(main.getTargets().get(target).size() == 0){
                                main.getTargets().remove(target);
                            }
                        }
                    }
                }

                main.getDefendOwner().remove(event.getEntity());
            }

            if(Boolean.parseBoolean(main.getConfigurationoptions().get("bodyguards_die_with_their_master"))){
                if(main.getBodyguards().get(event.getEntity()).size() > 0){
                    for(Entity bodyguard : main.getBodyguards().get(event.getEntity())){
                        main.getBodyguardsowner().remove(bodyguard);
                        main.getLastdamager().remove(bodyguard);
                        bodyguard.remove();
                    }
                }

                main.getBodyguards().remove(event.getEntity());
            }

            /*main.getLastdamager().remove(event.getEntity());*/
            /*main.getBodyguards().remove(event.getEntity());*/
        }

        //Mort d'un BodyGuard
        if(main.getBodyguardsowner().containsKey(event.getEntity())){
            if(main.getBodyguardsowner().get(event.getEntity()) instanceof LivingEntity && main.getBodyguardsowner().get(event.getEntity()) != null && !main.getBodyguardsowner().get(event.getEntity()).isDead()){
                main.getBodyguards().get(main.getBodyguardsowner().get(event.getEntity())).remove(event.getEntity());
                /*main.getBodyguardsowner().remove(event.getEntity());*/
            }
        }

        //Ciblage Auto Si Pas de Cible et Nouvelle Cible
        if(main.getTargets().containsKey(event.getEntity())){
            List<Entity> masters = new ArrayList<>();
            if(main.getTargets().get(event.getEntity()).isEmpty()){
                main.getTargets().remove(event.getEntity());
            } else {
                masters = main.getTargets().get(event.getEntity());
            }

            main.getTargets().remove(event.getEntity());

            if(!masters.isEmpty()){
                for(Entity master : masters){
                    if(master instanceof LivingEntity){
                        if(main.getDefendOwner().containsKey(event.getEntity())){
                            main.getDefendOwner().get(master).remove(event.getEntity());

                            if(!main.getDefendOwner().get(master).isEmpty()){
                                for(Entity bodyguard : main.getBodyguards().get(master)){
                                    if(bodyguard instanceof Creature){
                                        if(((Creature) bodyguard).getTarget() == null || Objects.requireNonNull(((Creature) bodyguard).getTarget()).isDead()){
                                            if(main.getDefendOwner().get(master).get(main.getDefendOwner().get(master).size() - 1) instanceof LivingEntity){
                                                ((Creature) bodyguard).setTarget((LivingEntity) main.getDefendOwner().get(master).get(main.getDefendOwner().get(master).size() - 1));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                main.getTargets().remove(event.getEntity());
            }
        }
    }

    @EventHandler
    public void OnEntityTarget(EntityTargetEvent event){
        if(!main.getBodyguardsowner().isEmpty()){
            if(main.getBodyguardsowner().containsKey(event.getEntity())){
                if(!(event.getTarget() instanceof Player)){
                    boolean cancelled = false;
                    if(main.getDefendOwner().containsKey(main.getBodyguardsowner().get(event.getEntity()))){
                        if(main.getDefendOwner().get(main.getBodyguardsowner().get(event.getEntity())).isEmpty()){
                            event.setCancelled(true);
                            cancelled = true;
                        } else {
                            if(!main.getDefendOwner().get(main.getBodyguardsowner().get(event.getEntity())).contains(event.getTarget())){
                                event.setCancelled(true);
                                cancelled = true;
                            }
                        }
                    } else {
                        event.setCancelled(true);
                        cancelled = true;
                    }

                    if(!cancelled){
                        if(main.getBodyguards().containsKey(event.getTarget())){
                            if(!main.getDefendOwner().containsKey(event.getTarget())){
                                main.getDefendOwner().put(event.getTarget(), new ArrayList<>());
                            }

                            if(!main.getDefendOwner().get(event.getTarget()).contains(event.getEntity())){
                                main.getDefendOwner().get(event.getTarget()).add(event.getEntity());
                                for(Entity bodyguard : main.getBodyguards().get(event.getTarget())){
                                    if(event.getEntity() instanceof  LivingEntity && bodyguard instanceof LivingEntity){
                                        if(bodyguard instanceof Creature){
                                            if(((Creature) bodyguard).getTarget() == null || Objects.requireNonNull(((Creature) bodyguard).getTarget()).isDead()){
                                                ((Creature) bodyguard).setTarget((LivingEntity) event.getEntity());
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if(main.getBodyguardsowner().containsKey(event.getEntity())){
                                if(main.getBodyguardsowner().get(event.getEntity()) == null || main.getBodyguardsowner().get(event.getEntity()).isDead()){
                                    if(!(event.getTarget() instanceof Player)){
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event){
        Entity shooter = (Entity) event.getEntity().getShooter();
        if(main.getBodyguardsowner().containsKey(event.getEntity())){
            if(shooter instanceof Creature){
                Creature creature = (Creature) shooter;
                if(creature.getTarget() == null){
                    event.setCancelled(true);
                }
            }
        }
    }
}