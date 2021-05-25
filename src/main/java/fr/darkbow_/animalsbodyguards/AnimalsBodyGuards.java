package fr.darkbow_.animalsbodyguards;

import fr.darkbow_.animalsbodyguards.commands.CommandAnimalsBodyGuards;
import fr.darkbow_.animalsbodyguards.scoreboard.ScoreboardSign;
import org.bstats.bukkit.Metrics;
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
    private Map<Entity, Entity> bodyguardsowner;
    private List<EntityType> bodyguardstypes;
    private Map<Entity, Entity> damagers;
    private Map<EntityType, Integer> animalstypescount;
    private Map<Entity, String> previouscustomname;
    private Map<Entity, String> customname;
    private Map<Entity, Boolean> CustomNameWasVisible;

    private Map<Entity, List<Entity>> DefendOwner;
    private Map<Entity, List<Entity>> targets;

    private List<EntityType> PassiveEntityTypes;
    private HashMap<Entity, Entity> lastdamager;

    private Map<String, String> configurationoptions;

    public AnimalsBodyGuards getInstance() {
        return this.instance;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        this.boards = new HashMap<>();
        this.configurationoptions = new HashMap<>();

        this.PassiveEntityTypes = new ArrayList<>();
        this.bodyguards = new HashMap<>();
        this.bodyguardstypes = new ArrayList<>();
        this.damagers = new HashMap<>();
        this.animalstypescount = new HashMap<>();
        this.DefendOwner = new HashMap<>();
        this.targets = new HashMap<>();
        this.bodyguardsowner = new HashMap<>();
        this.previouscustomname = new HashMap<>();
        this.CustomNameWasVisible = new HashMap<>();
        this.lastdamager = new HashMap<>();
        this.customname = new HashMap<>();

        getServer().getPluginManager().registerEvents(new AnimalsEvent(this), this);
        getCommand("animalsbodyguards").setExecutor(new CommandAnimalsBodyGuards(this));

        PassiveEntityTypes.add(EntityType.HORSE);
        PassiveEntityTypes.add(EntityType.MULE);
        PassiveEntityTypes.add(EntityType.TURTLE);
        PassiveEntityTypes.add(EntityType.PIG);
        PassiveEntityTypes.add(EntityType.BAT);
        PassiveEntityTypes.add(EntityType.CAT);
        PassiveEntityTypes.add(EntityType.CHICKEN);
        PassiveEntityTypes.add(EntityType.COD);
        PassiveEntityTypes.add(EntityType.BEE);
        PassiveEntityTypes.add(EntityType.COW);
        PassiveEntityTypes.add(EntityType.DOLPHIN);
        PassiveEntityTypes.add(EntityType.DONKEY);
        PassiveEntityTypes.add(EntityType.FOX);
        PassiveEntityTypes.add(EntityType.LLAMA);
        PassiveEntityTypes.add(EntityType.MUSHROOM_COW);
        PassiveEntityTypes.add(EntityType.OCELOT);
        PassiveEntityTypes.add(EntityType.PANDA);
        PassiveEntityTypes.add(EntityType.PARROT);
        PassiveEntityTypes.add(EntityType.PUFFERFISH);
        PassiveEntityTypes.add(EntityType.RABBIT);
        PassiveEntityTypes.add(EntityType.SALMON);
        PassiveEntityTypes.add(EntityType.SHEEP);
        PassiveEntityTypes.add(EntityType.SNOWMAN);
        PassiveEntityTypes.add(EntityType.SQUID);
        PassiveEntityTypes.add(EntityType.STRIDER);
        PassiveEntityTypes.add(EntityType.TRADER_LLAMA);
        PassiveEntityTypes.add(EntityType.TROPICAL_FISH);
        PassiveEntityTypes.add(EntityType.VILLAGER);
        PassiveEntityTypes.add(EntityType.WANDERING_TRADER);


        bodyguardstypes.add(EntityType.CAVE_SPIDER);
        bodyguardstypes.add(EntityType.CREEPER);
        bodyguardstypes.add(EntityType.DROWNED);
        bodyguardstypes.add(EntityType.ELDER_GUARDIAN);
        bodyguardstypes.add(EntityType.ENDERMAN);
        bodyguardstypes.add(EntityType.ENDER_CRYSTAL);
        bodyguardstypes.add(EntityType.ENDER_DRAGON);
        bodyguardstypes.add(EntityType.EVOKER);
        bodyguardstypes.add(EntityType.GHAST);
        bodyguardstypes.add(EntityType.HOGLIN);
        bodyguardstypes.add(EntityType.HUSK);
        bodyguardstypes.add(EntityType.ILLUSIONER);
        bodyguardstypes.add(EntityType.IRON_GOLEM);
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

        configurationoptions.put("bodyguards_die_with_their_master", getConfig().getString("bodyguards_die_with_their_master"));
        configurationoptions.put("special_names", getConfig().getString("special_names"));

        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 10684; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        System.out.println("[Animals BodyGuards] Plugin ON!");
    }

    @Override
    public void onDisable() {
        for(Map.Entry<Entity, List<Entity>> bodyguardsmap : getBodyguards().entrySet()){
            if(previouscustomname.containsKey(bodyguardsmap.getKey())){
                bodyguardsmap.getKey().setCustomName(previouscustomname.get(bodyguardsmap.getKey()));
            }

            if(CustomNameWasVisible.containsKey(bodyguardsmap.getKey())){
                bodyguardsmap.getKey().setCustomNameVisible(CustomNameWasVisible.get(bodyguardsmap.getKey()));
            }

            if(bodyguardsmap.getValue().size() > 0){
                for(Entity bodyguard : bodyguardsmap.getValue()){
                    bodyguard.remove();
                }
            }
        }

        System.out.println("[Animals BodyGuards] Plugin OFF!");
    }

    public Entity spawnBodyGuard(Entity damagedentity, Entity target){
        if(!previouscustomname.containsKey(damagedentity)){
            previouscustomname.put(damagedentity, damagedentity.getCustomName());
        }

        if(!CustomNameWasVisible.containsKey(damagedentity)){
            CustomNameWasVisible.put(damagedentity, damagedentity.isCustomNameVisible());
        }

        if(!getDefendOwner().containsKey(damagedentity)){
            getDefendOwner().put(damagedentity, new ArrayList<>());
        }

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

        String entityname;

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
            if(Boolean.parseBoolean(getConfigurationoptions().get("special_names"))){
                damagedentity.setCustomName("§a§l" + getAnimalstypescount().get(damagedentity.getType()) + prefix + " §6§l" + damagedentity.getType().toString());
                damagedentity.setCustomNameVisible(true);
            }

            entityname = "§a§l" + getAnimalstypescount().get(damagedentity.getType()) + prefix + " §6§l" + damagedentity.getType().toString();

            if(!customname.containsKey(damagedentity)){
                customname.put(damagedentity, "§a§l" + getAnimalstypescount().get(damagedentity.getType()) + prefix + " §6§l" + damagedentity.getType().toString());
            }
        } else {
            entityname = damagedentity.getCustomName();
        }

        previouscustomname.put(bodyguard, bodyguard.getCustomName());

        if(Boolean.parseBoolean(getConfigurationoptions().get("special_names"))){
            bodyguard.setCustomName("§a§l" + entityname + "§b§l's §6§lBodyGuard");
            bodyguard.setCustomNameVisible(true);
        }

        if(!customname.containsKey(bodyguard)){
            customname.put(bodyguard, "§a§l" + entityname + "§b§l's §6§lBodyGuard");
        }

        if(bodyguard instanceof Creature){
            ((Creature) bodyguard).setRemoveWhenFarAway(true);
        }

        if(!getBodyguards().containsKey(damagedentity)){
            getBodyguards().put(damagedentity, new ArrayList<>());
        }
        getBodyguards().get(damagedentity).add(bodyguard);

        getBodyguardsowner().put(bodyguard, damagedentity);

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

    public Map<Entity, List<Entity>> getTargets() {
        return targets;
    }

    public List<EntityType> getPassiveEntityTypes() {
        return PassiveEntityTypes;
    }

    public Map<Entity, Entity> getBodyguardsowner() {
        return bodyguardsowner;
    }

    public Map<Entity, Boolean> getCustomNameWasVisible() {
        return CustomNameWasVisible;
    }

    public Map<Entity, String> getPreviousCustomName() {
        return previouscustomname;
    }

    public HashMap<Entity, Entity> getLastdamager() {
        return lastdamager;
    }

    public Map<String, String> getConfigurationoptions() {
        return configurationoptions;
    }

    public Map<Entity, String> getCustomname() {
        return customname;
    }
}