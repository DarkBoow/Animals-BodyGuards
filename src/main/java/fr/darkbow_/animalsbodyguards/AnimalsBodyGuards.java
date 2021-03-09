package fr.darkbow_.animalsbodyguards;

import fr.darkbow_.animalsbodyguards.scoreboard.ScoreboardSign;
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

    public void spawnBodyGuard(Entity entity){
        Creature bodyguard = null;

        if(entity != null){
            Random r = new Random();
            int creaturetype = r.nextInt(bodyguardstypes.size());
            if(bodyguardstypes != null){
                if(bodyguardstypes.get(creaturetype) != null){
                    bodyguard = (Creature) Objects.requireNonNull(entity.getLocation().getWorld()).spawnEntity(entity.getLocation(), bodyguardstypes.get(creaturetype));
                    bodyguard.setRemoveWhenFarAway(true);
                    if(!bodyguards.containsKey(entity)){
                        bodyguards.put(entity, new ArrayList<>());
                    }

                    bodyguards.get(entity).add(bodyguard);
                }
            }
        }
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
}