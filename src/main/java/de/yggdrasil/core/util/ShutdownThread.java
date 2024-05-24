package de.yggdrasil.core.util;

import net.minestom.server.MinecraftServer;

public class ShutdownThread extends Thread{

    @Override
    public void run(){
        MinecraftServer.stopCleanly();
    }

}
