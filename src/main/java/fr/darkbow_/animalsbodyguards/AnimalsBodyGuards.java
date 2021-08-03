package fr.darkbow_.animalsbodyguards;

import fr.darkbow_.animalsbodyguards.commands.CommandAnimalsBodyGuards;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AnimalsBodyGuards extends JavaPlugin {
    private Map<Entity, List<Entity>> bodyguards;
    private Map<Entity, Entity> bodyguardsowner;
    private List<EntityType> bodyguardstypes;
    private Map<EntityType, Integer> animalstypescount;
    private Map<Entity, String> previouscustomname;
    private Map<Entity, String> customname;
    private Map<Entity, Boolean> CustomNameWasVisible;

    private Map<Entity, List<Entity>> DefendOwner;
    private Map<Entity, List<Entity>> targets;

    private List<EntityType> PassiveEntityTypes;
    private HashMap<Entity, Entity> lastdamager;

    private Map<String, String> configurationoptions;

    private File savesfile;
    private FileConfiguration savesconfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createSavesFile();

        this.configurationoptions = new HashMap<>();

        this.PassiveEntityTypes = new ArrayList<>();
        this.bodyguards = new HashMap<>();
        this.bodyguardstypes = new ArrayList<>();
        this.animalstypescount = new HashMap<>();
        this.DefendOwner = new HashMap<>();
        this.targets = new HashMap<>();
        this.bodyguardsowner = new HashMap<>();
        this.previouscustomname = new HashMap<>();
        this.CustomNameWasVisible = new HashMap<>();
        this.lastdamager = new HashMap<>();
        this.customname = new HashMap<>();

        getServer().getPluginManager().registerEvents(new AnimalsEvent(this), this);
        Objects.requireNonNull(getCommand("animalsbodyguards")).setExecutor(new CommandAnimalsBodyGuards(this));

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

        if(getSavesConfig().contains("count")){
            for(String typecount : Objects.requireNonNull(getSavesConfig().getConfigurationSection("count")).getKeys(false)){
                getAnimalstypescount().put(EntityType.valueOf(typecount), getSavesConfig().getInt("count." + typecount));
            }
        }

        for(String owner : getSavesConfig().getKeys(false)){
            if(!owner.equals("count")){
                if(Bukkit.getEntity(UUID.fromString(owner)) != null){
                    List<Entity> savedbodyguards = new ArrayList<>();
                    for(String bodyguard : Objects.requireNonNull(getSavesConfig().getConfigurationSection(owner + ".bodyguards")).getKeys(false)){
                        savedbodyguards.add(Bukkit.getEntity(UUID.fromString(bodyguard)));
                        getBodyguardsowner().put(Bukkit.getEntity(UUID.fromString(bodyguard)), Bukkit.getEntity(UUID.fromString(owner)));
                        if(getSavesConfig().contains(owner + ".bodyguards." + bodyguard + ".target") && !Objects.requireNonNull(getSavesConfig().getString(owner + ".bodyguards." + bodyguard + ".target")).isEmpty()){
                            if(Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(getSavesConfig().getString(owner + ".bodyguards." + bodyguard + ".target")))) != null){
                                ((Creature) Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(bodyguard)))).setTarget((LivingEntity) Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(getSavesConfig().getString(owner + ".bodyguards." + bodyguard + ".target")))));
                            }
                        }
                    }

                    if(!savedbodyguards.isEmpty()){
                        getBodyguards().put(Bukkit.getEntity(UUID.fromString(owner)), savedbodyguards);
                    }

                    if(!getSavesConfig().getStringList(owner + ".targets").isEmpty()){
                        List<Entity> savedtargets = new ArrayList<>();
                        List<String> savedtargetslist = getSavesConfig().getStringList(Bukkit.getEntity(UUID.fromString(owner)) + ".targets");
                        for(String target : savedtargetslist){
                            savedtargets.add(Bukkit.getEntity(UUID.fromString(target)));
                            if(!getTargets().containsKey(Bukkit.getEntity(UUID.fromString(target)))){
                                getTargets().put(Bukkit.getEntity(UUID.fromString(target)), new ArrayList<>());
                            }

                            getTargets().get(Bukkit.getEntity(UUID.fromString(target))).add(Bukkit.getEntity(UUID.fromString(owner)));
                        }

                        if(!savedtargets.isEmpty()){
                            getDefendOwner().put(Bukkit.getEntity(UUID.fromString(owner)), savedtargets);
                        }
                    }
                }
            }
        }

        configurationoptions.put("bodyguards_die_with_their_master", getConfig().getString("bodyguards_die_with_their_master"));
        configurationoptions.put("special_names", getConfig().getString("special_names"));

        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 10684; // <-- Replace with the id of your plugin!
        new Metrics(this, pluginId);

        System.out.println("[Animals BodyGuards] Plugin ON!");
    }

    @Override
    public void onDisable() {
        if(getSavesConfig().contains("count")){
            for(String entitytype : Objects.requireNonNull(getSavesConfig().getConfigurationSection("count")).getKeys(false)){
                if(!getAnimalstypescount().containsKey(EntityType.valueOf(entitytype))){
                    getSavesConfig().set("count." + entitytype, null);
                }
            }
        }

        for(Map.Entry<EntityType, Integer> entitytypecountmap : getAnimalstypescount().entrySet()){
            getSavesConfig().set("count." + entitytypecountmap.getKey().toString(), entitytypecountmap.getValue());
        }

        if(!getBodyguards().isEmpty()){
            for(Map.Entry<Entity, List<Entity>> bodyguardsmap : getBodyguards().entrySet()){
                if(bodyguardsmap.getValue().size() > 0){
                    List<String> bodyguardsuuidlist = new ArrayList<>();
                    for(Entity bodyguard : bodyguardsmap.getValue()){
                        bodyguardsuuidlist.add(bodyguard.getUniqueId().toString());
                    }
                    getSavesConfig().set(bodyguardsmap.getKey().getUniqueId() + ".bodyguards", bodyguardsuuidlist);

                    for(String bodyguard : bodyguardsuuidlist){
                        if(Bukkit.getEntity(UUID.fromString(bodyguard)) != null){
                            if(Bukkit.getEntity(UUID.fromString(bodyguard)) instanceof Creature){
                                if(((Creature) Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(bodyguard)))).getTarget() != null){
                                    getSavesConfig().set(bodyguardsmap.getKey().getUniqueId() + ".bodyguards." + bodyguard + ".target", Objects.requireNonNull(((Creature) Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(bodyguard)))).getTarget()).getUniqueId().toString());
                                }
                            }
                        }

                        if(!getSavesConfig().contains(bodyguardsmap.getKey().getUniqueId() + ".bodyguards." + bodyguard + ".target")){
                            getSavesConfig().set(bodyguardsmap.getKey().getUniqueId() + ".bodyguards." + bodyguard + ".target", "");
                        }
                    }

                    List<String> targetsuuidlist = new ArrayList<>();
                    if(getTargets().containsKey(bodyguardsmap.getKey()) && !getTargets().get(bodyguardsmap.getKey()).isEmpty()){
                        for(Entity target : getTargets().get(bodyguardsmap.getKey())){
                            targetsuuidlist.add(target.getUniqueId().toString());
                        }
                    }
                    getSavesConfig().set(bodyguardsmap.getKey().getUniqueId() + ".targets", targetsuuidlist);
                }
            }
        }

        try {
            getSavesConfig().save(getSavesFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[Animals BodyGuards] Plugin OFF!");
    }

    public FileConfiguration getSavesConfig(){
        return this.savesconfig;
    }

    public File getSavesFile(){
        return this.savesfile;
    }

    private void createSavesFile(){
        savesfile = new File(getDataFolder(), "saves.yml");
        if(!savesfile.exists()){
            savesfile.getParentFile().mkdirs();
            saveResource("saves.yml", false);
        }

        savesconfig = new YamlConfiguration();
        try {
            savesconfig.load(savesfile);
        } catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
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
                damagedentity.setCustomName("§a§l" + getAnimalstypescount().get(damagedentity.getType()) + prefix + " §6§l" + damagedentity.getType());
                damagedentity.setCustomNameVisible(true);
            }

            entityname = "§a§l" + getAnimalstypescount().get(damagedentity.getType()) + prefix + " §6§l" + damagedentity.getType();

            if(!customname.containsKey(damagedentity)){
                customname.put(damagedentity, "§a§l" + getAnimalstypescount().get(damagedentity.getType()) + prefix + " §6§l" + damagedentity.getType());
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

    public Map<Entity, List<Entity>> getBodyguards() {
        return bodyguards;
    }

    public List<EntityType> getBodyGuardsTypes(){
        return bodyguardstypes;
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