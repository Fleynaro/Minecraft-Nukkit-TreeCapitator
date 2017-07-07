/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fleynaro.treecap;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;

/**
 *
 * @author user
 */
public class EventListener implements Listener {
    
    EventListener() {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        boolean axeInHand = TreeEntity.IsPlayerWithAxInHand(player);
        if ( (player != null && (!Main.config.getBoolean("only-axe", true) || axeInHand)) && TreeEntity.IsBlockOfTree(block) ) {
            TreeEntity tree = TreeEntity.GetTreeOfBlock(block);
            if ( tree.isTree() ) {
                if ( tree.canBeCutDown() ) {
                    if ( tree.hitToCutDown() ) {
                        tree.removeBlock(block);
                        tree.cutDown();
                    } else {
                        event.setCancelled();
                    }
                    if ( axeInHand ) {
                        TreeEntity.BreakPlayerAxe(player);
                    }
                } else {
                    tree.removeBlock(block);
                }
            }
        }
    }
    
    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        for (Block block : event.getBlockList()) {
            //todo
        }
    }
}
