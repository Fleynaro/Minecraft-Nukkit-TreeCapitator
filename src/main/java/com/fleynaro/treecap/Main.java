/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fleynaro.treecap;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

/**
 *
 * @author user
 */
public class Main extends PluginBase {
    
    public String VERSION = "v1.0";
    public static Config config;
    
    @Override
    public void onEnable() {
        this.getDataFolder().mkdirs();
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getLogger().info(TextFormat.WHITE +"The plugin "+ TextFormat.GREEN +"TreeCapitator "+ VERSION + TextFormat.WHITE +" has been loaded.");
        Main.config = this.getConfig();
    }
}
