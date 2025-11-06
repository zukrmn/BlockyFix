package com.blockycraft.blockyfix;

import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import com.blockycraft.blockyclaim.BlockyClaim;
import com.blockycraft.blockyclaim.data.Claim;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockListener implements Listener {

    private final Map<Integer, Set<Byte>> axeBlockRules;
    private final Map<Integer, Set<Byte>> pickaxeBlockRules;

    public BlockListener(Map<Integer, Set<Byte>> axeBlockRules, Map<Integer, Set<Byte>> pickaxeBlockRules) {
        this.axeBlockRules = axeBlockRules;
        this.pickaxeBlockRules = pickaxeBlockRules;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack toolInHand = player.getItemInHand();

        if (toolInHand == null || toolInHand.getType() == Material.AIR) { return; }

        if (!canBuild(player, block.getLocation())) { return; }

        int toolId = toolInHand.getTypeId();
        int blockId = block.getTypeId();
        byte blockData = block.getData();
        
        boolean isRuleMet = (isAxe(toolId) && axeBlockRules.containsKey(blockId) && axeBlockRules.get(blockId).contains(blockData)) ||
                            (isPickaxe(toolId) && pickaxeBlockRules.containsKey(blockId) && pickaxeBlockRules.get(blockId).contains(blockData));

        if (isRuleMet) {
            event.setCancelled(true);
            handleManualBreak(player, block, toolInHand);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location loc = event.getLocation();
        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    Block block = loc.clone().add(x, y, z).getBlock();
                    if (block.getType() == Material.OBSIDIAN) {
                        if (block.getLocation().distance(loc) <= 4) {
                            event.blockList().add(block);
                        }
                    }
                }
            }
        }
    }

    private void handleManualBreak(Player player, Block block, ItemStack tool) {
        int blockId = block.getTypeId();

        if (blockId == 71) {
            byte data = block.getData();
            Block otherHalf = (data & 8) == 8 ? block.getRelative(BlockFace.DOWN) : block.getRelative(BlockFace.UP);
            if (otherHalf.getTypeId() == blockId) {
                otherHalf.setType(Material.AIR);
            }
        }
        
        ItemStack[] drops = getBlockDrops(block);
        block.setType(Material.AIR);

        if (drops != null) {
            Location blockLocation = block.getLocation();
            for (ItemStack drop : drops) {
                if (drop != null) {
                    block.getWorld().dropItemNaturally(blockLocation, drop);
                }
            }
        }
        
        short newDurability = (short) (tool.getDurability() + 1);
        if (newDurability >= tool.getType().getMaxDurability()) {
            player.setItemInHand(null);
        } else {
            tool.setDurability(newDurability);
        }
    }

    private ItemStack[] getBlockDrops(Block block) {
        int typeId = block.getTypeId();
        byte data = block.getData();
        
        switch (typeId) {
            // Special drops (block breaks into something else)
            case 1: return new ItemStack[]{new ItemStack(4, 1)}; // STONE -> COBBLESTONE
            case 13: return (Math.random() < 0.1) ? new ItemStack[]{new ItemStack(318, 1)} : new ItemStack[]{new ItemStack(13, 1)}; // GRAVEL -> FLINT or GRAVEL
            case 16: return new ItemStack[]{new ItemStack(263, 1)}; // COAL_ORE -> COAL
            case 21: return new ItemStack[]{new ItemStack(351, (int)(Math.random() * 5) + 4, (short) 4)}; // LAPIS_ORE -> INK_SACK (Lapis)
            case 56: return new ItemStack[]{new ItemStack(264, 1)}; // DIAMOND_ORE -> DIAMOND
            case 73: case 74: return new ItemStack[]{new ItemStack(331, (int)(Math.random() * 2) + 4)}; // REDSTONE_ORE -> REDSTONE
            case 71: return new ItemStack[]{new ItemStack(330, 1)}; // IRON_DOOR_BLOCK -> IRON_DOOR item
            case 43: return new ItemStack[]{new ItemStack(44, 2, data)}; // DOUBLE_STEP -> 2x STEP
            
            // Aqui você adicionará o caso para sua laje de madeira quando descobrir o ID e a DATA.
            // Exemplo, se o ID for 126 e a DATA for 2:
            // case 126:
            //     if (data == 2) { // Verificando a DATA/subid
            //         return new ItemStack[]{new ItemStack(126, 1, (short) 2)}; // Dropa 1 laje
            //     }

            // Default case: a maioria dos blocos dropa a si mesmo.
            default:
                return new ItemStack[]{new ItemStack(typeId, 1, data)};
        }
    }
    
    private boolean isAxe(int toolId) {
        // IDs: 271=WOOD, 275=STONE, 258=IRON, 279=DIAMOND, 286=GOLD
        return toolId == 271 || toolId == 275 || toolId == 258 || toolId == 279 || toolId == 286;
    }

    private boolean isPickaxe(int toolId) {
        // IDs: 270=WOOD, 274=STONE, 257=IRON, 278=DIAMOND, 285=GOLD
        return toolId == 270 || toolId == 274 || toolId == 257 || toolId == 278 || toolId == 285;
    }

    private boolean canBuild(Player player, Location location) {
        BlockyClaim blockyClaim = (BlockyClaim) player.getServer().getPluginManager().getPlugin("BlockyClaim");
        if (blockyClaim == null) {
            return true; 
        }
        Claim claim = blockyClaim.getClaimManager().getClaimAt(location);
        return claim == null || claim.hasPermission(player.getName());
    }
}