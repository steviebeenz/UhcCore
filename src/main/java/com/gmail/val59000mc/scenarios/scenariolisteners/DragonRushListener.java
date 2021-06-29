package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class DragonRushListener extends ScenarioListener{

    private final List<Block> portalBlocks;

    public DragonRushListener(){
        portalBlocks = new ArrayList<>();
    }

    @Override
    public void onEnable(){
        if (!GameManager.getGameManager().getConfig().get(MainConfig.ENABLE_THE_END)){
            Bukkit.broadcastMessage(ChatColor.RED + "[UhcCore] For DragonRush the end needs to be enabled first!");
            getScenarioManager().disableScenario(Scenario.DRAGON_RUSH);
            return;
        }

        Location portalLoc = getPortalLocation();

        portalBlocks.add(portalLoc.clone().add(1, 0, 2).getBlock());
        portalBlocks.add(portalLoc.clone().add(0, 0, 2).getBlock());
        portalBlocks.add(portalLoc.clone().add(-1, 0, 2).getBlock());

        portalBlocks.add(portalLoc.clone().add(-2, 0, 1).getBlock());
        portalBlocks.add(portalLoc.clone().add(-2, 0, 0).getBlock());
        portalBlocks.add(portalLoc.clone().add(-2, 0, -1).getBlock());

        portalBlocks.add(portalLoc.clone().add(1, 0, -2).getBlock());
        portalBlocks.add(portalLoc.clone().add(0, 0, -2).getBlock());
        portalBlocks.add(portalLoc.clone().add(-1, 0, -2).getBlock());

        portalBlocks.add(portalLoc.clone().add(2, 0, 1).getBlock());
        portalBlocks.add(portalLoc.clone().add(2, 0, 0).getBlock());
        portalBlocks.add(portalLoc.clone().add(2, 0, -1).getBlock());

        int i = 0;
        BlockFace blockFace = BlockFace.NORTH;
        for (Block block : portalBlocks){
            block.setType(UniversalMaterial.END_PORTAL_FRAME.getType());
            VersionUtils.getVersionUtils().setEndPortalFrameOrientation(block, blockFace);
            if (RandomUtils.randomInteger(0, 2) == 1){
                VersionUtils.getVersionUtils().setEye(block, true);
            }
            i++;
            if (i == 3){
                i = 0;
                if (blockFace == BlockFace.NORTH){
                    blockFace = BlockFace.EAST;
                }else if (blockFace == BlockFace.EAST){
                    blockFace = BlockFace.SOUTH;
                }else if (blockFace == BlockFace.SOUTH){
                    blockFace = BlockFace.WEST;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        for (Block block : portalBlocks){
            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntityType() != EntityType.ENDER_DRAGON){
            return;
        }

        if (e.getEntity().getKiller() == null) {
            return;
        }

        Player killer = e.getEntity().getKiller();
        UhcPlayer uhcKiller = getPlayerManager().getUhcPlayer(killer);

        List<UhcPlayer> spectators = new ArrayList<>();

        for (UhcPlayer playingPlayer : getPlayerManager().getAllPlayingPlayers()){

            if (!playingPlayer.isInTeamWith(uhcKiller)){
                spectators.add(playingPlayer);
            }
        }

        for (UhcPlayer spectator : spectators){
            spectator.setState(PlayerState.DEAD);

            try {
                Player all = spectator.getPlayer();
                all.setGameMode(GameMode.SPECTATOR);
                all.teleport(killer);
            }catch (UhcPlayerNotOnlineException exeption){
                // Nothing
            }
        }

        getPlayerManager().checkIfRemainingPlayers();
    }

    private Location getPortalLocation(){
        World world = getGameManager().getMapLoader().getUhcWorld(World.Environment.NORMAL);
        int portalY = 0;

        for (int x = -4; x < 4; x++) {
            for (int z = -4; z < 4; z++) {
                int y = getHighestBlock(world, x, z);
                if (y > portalY){
                    portalY = y;
                }
            }
        }

        return new Location(world, 0, portalY+1, 0);
    }

    private int getHighestBlock(World world, int x, int z){
        int y = 150;
        while (world.getBlockAt(x, y, z).getType() == Material.AIR){
            y--;
        }

        return y;
    }

}