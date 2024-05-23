package de.yggdrasil.core;

import de.yggdrasil.core.extension.ExtensionLoader;
import net.minestom.server.MinecraftServer;

public class ServerCore {

    private final static ServerCore servercore = new ServerCore();
    private static MinecraftServer server;
    private final ExtensionLoader extensionLoader = new ExtensionLoader();

    private ServerCore() {}

    public static void main(String[] args) {
        servercore.setupPhase();
        servercore.startPhase();
        servercore.prepareShutdown();
        servercore.loadExtensions();
        servercore.start();
    }

    private void setupPhase() {

    }

    private void startPhase() {
        server = MinecraftServer.init();
    }

    private void loadExtensions(){
        extensionLoader.loadExtensions();
    }

    private void start(){
        server.start("0.0.0.0",25565);
    }

    private void prepareShutdown() {
        Runtime.getRuntime().addShutdownHook(Thread.startVirtualThread(() -> {
            extensionLoader.terminate();
            servercore.Shutdown();
        }));
    }

    private void Shutdown (){
        MinecraftServer.stopCleanly();
    }

}
