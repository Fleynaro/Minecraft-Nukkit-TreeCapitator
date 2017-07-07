/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fleynaro.treecap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockWood;
import cn.nukkit.block.BlockWood2;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public class TreeEntity {
    
    enum Type {
        OAK("Дуб",                  new int[]{Block.WOOD, BlockWood.OAK},           new int[]{Block.LEAVE, BlockLeaves.OAK},            70.0),
        SPRUCE("Ель",               new int[]{Block.WOOD, BlockWood.SPRUCE},        new int[]{Block.LEAVE, BlockLeaves.SPRUCE},         50.0),
        BIRCH("Береза",             new int[]{Block.WOOD, BlockWood.BIRCH},         new int[]{Block.LEAVE, BlockLeaves.BRICH},          70.0),
        JUNGLE("Секвойя",           new int[]{Block.WOOD, BlockWood.JUNGLE},        new int[]{Block.LEAVE, BlockLeaves.JUNGLE},         70.0),
        ACACIA("Акация",            new int[]{Block.WOOD2, BlockWood2.ACACIA},      new int[]{Block.LEAVE2, 0},                         60.0),
        DARKOAK("Темный дуб",       new int[]{Block.WOOD2, BlockWood2.DARK_OAK},    new int[]{Block.LEAVE2, 1},                         60.0);
        private int[] wood;
        private int[] leave;
        private double leavePercent;
        
        Type(String name, int[] wood, int[] leave, double leavePercent) {
            this.wood = wood;
            this.leave = leave;
            this.leavePercent = leavePercent;
        }    
        public int getWood() {
            return this.wood[0];
        } 
        public int getWoodType() {
            return this.wood[1];
        }
        public int getLeave() {
            return this.leave[0];
        }
        public int getLeaveType() {
            return this.leave[1];
        }
        public double getLeavePercent() {
            return this.leavePercent;
        }
    }
    static public Map<Block, TreeEntity> trees = new HashMap<>();
    
    public Type tree;
    public List<Block> blocks = new ArrayList<>();
    public List<Block> leaves = new ArrayList<>();
    public Level level;
    public int downHeight;
    public int upHeight;
    public Block brokenBlock;
    public int cutDownCount = 0;
    
    TreeEntity(Block block) {
        this.level = block.getLevel();
        this.tree = TreeEntity.getTree(block);
        this.brokenBlock = block;
        this.getBlocks(block);
        
        if ( this.isTree() ) {
            if ( this.isValid() ) {
                for ( Block wood : this.blocks ) {
                    TreeEntity.trees.put(wood, this);
                }
            } else {
                this.tree = null;
            }
        }
    }
    
    public void setBrokenBlock(Block block) {
        if ( block == this.brokenBlock ) return;
        int differ = block.getFloorZ() - this.brokenBlock.getFloorZ();
        this.brokenBlock = block;
        this.upHeight -= differ;
        this.downHeight += differ;
    }
    
    public boolean isValid() {
        int sum = this.leaves.size() + this.blocks.size();
        //System.out.println("sum = " + sum + ", this.leaves.size() = " + this.leaves.size() + ", соотношение = " + (this.leaves.size() * 100.0 / sum));
        return (sum > 10 && this.leaves.size() * 100.0 / sum >= this.tree.getLeavePercent());
    }
    
    public boolean isTree() {
        return (this.tree != null);
    }
    
    public boolean hitToCutDown() {
        return ( ++ this.cutDownCount >= Math.sqrt(this.blocks.size()) * Main.config.getDouble("hit-to-cut-down", 1.0) );
    }
    
    public boolean canBeCutDown() {
        return (this.downHeight <= Main.config.getInt("count-down-blocks", 1));
    }
    
    public void cutDown() {
        this.tree = null;
        for ( Block block : this.blocks ) {
            this.level.dropItem(block.getLocation(), Item.get(block.getId(), block.getDamage()));
            block.onBreak(null);
            TreeEntity.trees.remove(block);
        }
        for ( Block block : this.leaves ) {
            int[][] items = block.getDrops(new ItemBlock(new BlockAir(), 0, 0));
            for ( int i = 0; i < items.length; i ++ ) {
                if ( items[i][0] == Item.SAPLING ) continue; //todo: remove it soon
                this.level.dropItem(block.getLocation(), Item.get(items[i][0], items[i][1], items[i][2]));
            }
            block.onBreak(null);
        }
        this.blocks.clear();
        this.leaves.clear();
    }
    
    public void removeBlock(Block block) {
        this.blocks.remove(block);
        TreeEntity.trees.remove(block);
    }
    
    public void getBlocks(Block block) {
        int step = 0;
        int size = 0;
        this.getBlocksOnHight(block, null);
        while ( true ) {
            int curSize = this.blocks.size();
            if ( size == curSize ) break;
            //System.out.println("====================== step = " + step);
            while ( size != curSize ) {
                //System.out.println("------------------ size = " + size + ", curSize = " + curSize);
                this.getBlocksOnHight(this.blocks.get(size).down(), null);
                size ++;
            }
            step ++;
        }
        this.downHeight += step;

        if ( Main.config.getBoolean("check-dirt-down", true) ) {
            int Y = this.blocks.get(size - 1).getFloorY();
            while ( -- size != -1 && this.blocks.get(size).getFloorY() == Y ) {
                Block curBlock = this.blocks.get(size).down();
                if ( !(curBlock.getId() == Block.GRASS || curBlock.getId() == Block.DIRT) ) {
                    this.tree = null;
                    return;
                }
            }
        }
        //System.out.println("************************** this.downHeight = " + this.downHeight);
        step = 0;
        size = this.blocks.size();
        this.getBlocksOnHight(block.up(), null);
        while ( true ) {
            int curSize = this.blocks.size();
            if ( size == curSize ) break;
            //System.out.println("====================== step = " + step);
            while ( size != curSize ) {
                //System.out.println("------------------ size = " + size + ", curSize = " + curSize);
                this.getBlocksOnHight(this.blocks.get(size).up(), null);
                size ++;
            }
            step ++;
        }
        this.upHeight = step;
    }
    
    public void getBlocksOnHight(Block block, BlockFace face) {
        //System.out.println("PLUGIN DEBUG: block.getId() = "+ block.getId());
        boolean valid = this.isWoodBlock(block);
        if ( valid ) {
            if ( !this.blocks.contains(block) ) {
                this.blocks.add(block);
            } else {
                return;
            }
        } else if ( this.isLeaveBlock(block) ) {
            this.getLeavesOnHight(block, null);
            if ( face != null ) {
                return;
            }
        }
        if ( valid || face == null ) {
            for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
                if ( blockFace == face ) continue;
                this.getBlocksOnHight(block.getSide(blockFace), blockFace.getOpposite());
            }
        }
    }
    
    public void getLeavesOnHight(Block block, BlockFace face) {
        //System.out.println("PLUGIN DEBUG: !!! leaves.getId() = "+ block.getId());
        this.leaves.add(block);
        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            if ( blockFace == face ) continue;
            Block newBlock = block.getSide(blockFace);
            if ( this.isLeaveBlock(newBlock) ) {
                if ( !this.leaves.contains(newBlock) ) {
                    this.getLeavesOnHight(newBlock, blockFace.getOpposite());
                }
            }
        }
    }
    
    private boolean isWoodBlock(Block block) {
        return (TreeEntity.getTree(block) == this.tree);
    }
    
    private boolean isLeaveBlock(Block block) {
        return (TreeEntity.getTreeByLeave(block) == this.tree);
    }
   
    public static Type getTree(Block block) {
        //System.out.println("PLUGIN DEBUG: block.getId() = "+ block.getId() + ", treeType.getDamage() = "+ (block.getDamage() & 0x03) + ", " + block.getDamage());
        for ( Type treeType : Type.values() ) {
            if ( block.getId() == treeType.getWood() && (block.getDamage() & 0x03) == treeType.getWoodType() ) {
                return treeType;
            }
        }
        return null;
    }
    
    public static Type getTreeByLeave(Block block) {
        //System.out.println("PLUGIN DEBUG: !!! block.getId() = "+ block.getId() + ", treeType.getDamage() = "+ (block.getDamage() & 0x03));
        for ( Type treeType : Type.values() ) {
            if ( block.getId() == treeType.getLeave() && (block.getDamage() & 0x03) == treeType.getLeaveType() ) {
                return treeType;
            }
        }
        return null;
    }
    
    public static TreeEntity GetTreeOfBlock(Block block) {
        if ( TreeEntity.trees.containsKey(block) ) {
            TreeEntity tree = TreeEntity.trees.get(block);
            tree.setBrokenBlock(block);
            return tree;
        }
        
        return new TreeEntity(block);
    }
    
    public static boolean IsBlockOfTree(Block block) {
        return ( block.getId() == Block.WOOD || block.getId() == Block.WOOD2 );
    }
    
    public static boolean IsPlayerWithAxInHand(Player player) {
        return player.getInventory().getItemInHand().isAxe();
    }
    
    public static void BreakPlayerAxe(Player player) {
        PlayerInventory pInventory = player.getInventory();
        Item axe = pInventory.getItemInHand();
        if ( axe.isAxe() ) {
            axe.setDamage(axe.getDamage() + Main.config.getInt("axe-durability-damage", 5));
            if ( axe.getDamage() < axe.getMaxDurability() ) {
                pInventory.setItemInHand(axe);
            } else {
                pInventory.setItemInHand(new ItemBlock(new BlockAir(), 0, 0));
            }
        }
    }
}
