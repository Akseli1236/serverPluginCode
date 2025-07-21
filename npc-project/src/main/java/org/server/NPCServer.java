package org.server;

import org.bukkit.plugin.java.JavaPlugin;

public final class NPCServer extends JavaPlugin
{
    @Override
    public void onEnable(){
        new CreateNPC(this);

    }

    @Override
    public void onDisable(){

    }
    
}
