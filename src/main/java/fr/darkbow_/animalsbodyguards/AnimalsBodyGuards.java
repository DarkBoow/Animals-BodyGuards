package fr.darkbow_.animalsbodyguards;

import fr.darkbow_.animalsbodyguards.scoreboard.ScoreboardSign;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class AnimalsBodyGuards extends JavaPlugin {
    public static BukkitTask task;
    private AnimalsBodyGuards instance;
    public Titles title = new Titles();

    private Map<Player, ScoreboardSign> boards;
    private Map<Entity, List<Entity>> bodyguards;
    private List<EntityType> bodyguardstypes;
    private Map<Entity, Entity> damagers;
    private Map<EntityType, Integer> animalstypescount;

    private Map<Entity, List<Entity>> DefendOwner;
    private List<Entity> targets;

    static List<EntityType> PassiveEntities;

    public AnimalsBodyGuards getInstance() {
        return this.instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.boards = new HashMap<>();

        PassiveEntities = new ArrayList<>();
        this.bodyguards = new HashMap<>();
        this.bodyguardstypes = new ArrayList<>();
        this.damagers = new HashMap<>();
        this.animalstypescount = new HashMap<>();
        this.DefendOwner = new HashMap<>();
        this.targets = new ArrayList<>();

        getServer().getPluginManager().registerEvents(new AnimalsEvent(this), this);

        PassiveEntities.add(EntityType.HORSE);
        PassiveEntities.add(EntityType.MULE);
        PassiveEntities.add(EntityType.TURTLE);
        PassiveEntities.add(EntityType.PIG);
        PassiveEntities.add(EntityType.BAT);
        PassiveEntities.add(EntityType.CAT);
        PassiveEntities.add(EntityType.CHICKEN);
        PassiveEntities.add(EntityType.COD);
        PassiveEntities.add(EntityType.BEE);
        PassiveEntities.add(EntityType.COW);
        PassiveEntities.add(EntityType.DOLPHIN);
        PassiveEntities.add(EntityType.DONKEY);
        PassiveEntities.add(EntityType.FOX);
        PassiveEntities.add(EntityType.IRON_GOLEM);
        PassiveEntities.add(EntityType.LLAMA);
        PassiveEntities.add(EntityType.MUSHROOM_COW);
        PassiveEntities.add(EntityType.OCELOT);
        PassiveEntities.add(EntityType.PANDA);
        PassiveEntities.add(EntityType.PARROT);
        PassiveEntities.add(EntityType.PUFFERFISH);
        PassiveEntities.add(EntityType.RABBIT);
        PassiveEntities.add(EntityType.SALMON);
        PassiveEntities.add(EntityType.SHEEP);
        PassiveEntities.add(EntityType.SNOWMAN);
        PassiveEntities.add(EntityType.SQUID);
        PassiveEntities.add(EntityType.STRIDER);
        PassiveEntities.add(EntityType.TRADER_LLAMA);
        PassiveEntities.add(EntityType.TROPICAL_FISH);
        PassiveEntities.add(EntityType.VILLAGER);
        PassiveEntities.add(EntityType.WANDERING_TRADER);


        bodyguardstypes.add(EntityType.CAVE_SPIDER);
        bodyguardstypes.add(EntityType.CREEPER);
        bodyguardstypes.add(EntityType.DROWNED);
        bodyguardstypes.add(EntityType.ELDER_GUARDIAN);
        bodyguardstypes.add(EntityType.ENDERMAN);
        bodyguardstypes.add(EntityType.ENDER_CRYSTAL);
        bodyguardstypes.add(EntityType.ENDER_DRAGON);
        bodyguardstypes.add(EntityType.EVOKER);
        bodyguardstypes.add(EntityType.GHAST);
        bodyguardstypes.add(EntityType.GIANT);
        bodyguardstypes.add(EntityType.HOGLIN);
        bodyguardstypes.add(EntityType.HUSK);
        bodyguardstypes.add(EntityType.ILLUSIONER);
        bodyguardstypes.add(EntityType.PHANTOM);
        bodyguardstypes.add(EntityType.PILLAGER);
        bodyguardstypes.add(EntityType.RAVAGER);
        bodyguardstypes.add(EntityType.SHULKER);
        bodyguardstypes.add(EntityType.SHULKER_BULLET);
        bodyguardstypes.add(EntityType.SILVERFISH);
        bodyguardstypes.add(EntityType.SPIDER);
        bodyguardstypes.add(EntityType.STRAY);
        bodyguardstypes.add(EntityType.VEX);
        bodyguardstypes.add(EntityType.VINDICATOR);
        bodyguardstypes.add(EntityType.WITCH);
        bodyguardstypes.add(EntityType.WITHER);
        bodyguardstypes.add(EntityType.WITHER_SKELETON);
        bodyguardstypes.add(EntityType.ZOGLIN);
        bodyguardstypes.add(EntityType.ZOMBIE_VILLAGER);
        bodyguardstypes.add(EntityType.ZOMBIFIED_PIGLIN);

        System.out.println("[Animals BodyGuards] Plugin ON!");
    }

    @Override
    public void onDisable() {
        System.out.println("[Animals BodyGuards] Plugin OFF!");
    }

    public Entity spawnBodyGuard(Entity damagedentity, Entity target){
        Random r = new Random();
        int creaturetype = r.nextInt(getBodyGuardsTypes().size());

        if(damagedentity.getWorld().getEnvironment() != World.Environment.THE_END){
            while(getBodyGuardsTypes().get(creaturetype) == EntityType.ENDER_DRAGON){
                creaturetype = r.nextInt(getBodyGuardsTypes().size());
            }
        }

        Entity bodyguard = Objects.requireNonNull(damagedentity.getLocation().getWorld()).spawnEntity(damagedentity.getLocation(), getBodyGuardsTypes().get(creaturetype));
        if(getBodyGuardsTypes().get(creaturetype) == EntityType.ENDER_DRAGON){
            EnderDragon dragon = (EnderDragon) bodyguard;
            dragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
        }

        String entityname = damagedentity.getType().name();

        if(damagedentity.getCustomName() == null){
            if(getAnimalstypescount().containsKey(damagedentity.getType())){
                getAnimalstypescount().put(damagedentity.getType(), getAnimalstypescount().get(damagedentity.getType()) + 1);
            } else {
                getAnimalstypescount().put(damagedentity.getType(), 1);
            }

            String prefix = "th";
            switch (getAnimalstypescount().get(damagedentity.getType())){
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
            damagedentity.setCustomName("§a§l" + getAnimalstypescount().get(damagedentity.getType()) + prefix + " §6§l" + damagedentity.getType().toString());
            damagedentity.setCustomNameVisible(true);
        }
        entityname = damagedentity.getCustomName();

        bodyguard.setCustomName("§a§l" + entityname + "§b§l's §6§lBodyGuard");
        bodyguard.setCustomNameVisible(true);
        if(bodyguard instanceof Creature){
            ((Creature) bodyguard).setRemoveWhenFarAway(true);
        }

        if(!getBodyguards().containsKey(damagedentity)){
            getBodyguards().put(damagedentity, new ArrayList<>());
        }
        getBodyguards().get(damagedentity).add(bodyguard);

        boolean cancelled = false;
        if(target instanceof LivingEntity){
            if(!getBodyguards().isEmpty()){
                if(getBodyguards().containsKey(target)){
                    if(getBodyguards().get(target).contains(damagedentity)){
                        cancelled = true;
                    }
                }

                if(!cancelled){
                    for(List<Entity> list : getBodyguards().values()){
                        if (list.contains(damagedentity)) {
                            cancelled = true;
                            break;
                        }
                    }
                }
            }
        }

        if(!cancelled && target instanceof LivingEntity){
            if(bodyguard instanceof Creature){
                ((Creature) bodyguard).setTarget((LivingEntity) target);
            } else {
                if(bodyguard instanceof Slime){
                    ((Slime) bodyguard).setTarget((LivingEntity) target);
                }
            }
        }

        return bodyguard;
    }

    public Map<Player, ScoreboardSign> getBoards(){
        return this.boards;
    }

    public Map<Entity, List<Entity>> getBodyguards() {
        return bodyguards;
    }

    public List<EntityType> getBodyGuardsTypes(){
        return bodyguardstypes;
    }

    public Map<Entity, Entity> getDamagers() {
        return damagers;
    }

    public Map<EntityType, Integer> getAnimalstypescount() {
        return animalstypescount;
    }

    public Map<Entity, List<Entity>> getDefendOwner() {
        return DefendOwner;
    }

    public List<Entity> getTargets() {
        return targets;
    }
}