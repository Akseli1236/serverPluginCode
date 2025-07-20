package org.server.mapcrates;

import java.util.Objects;

import org.bukkit.plugin.java.JavaPlugin;

public final class MapCrates extends JavaPlugin
{

    @Override
    public void onEnable(){
        Commands airdropCommands = new Commands(this);

        Objects.requireNonNull(this.getCommand("airdrops")).setExecutor(airdropCommands);

        new Airdrop(this);

    }

    @Override
    public void onDisable(){

    }
}
