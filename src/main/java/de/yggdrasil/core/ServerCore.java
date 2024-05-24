package de.yggdrasil.core;

import de.yggdrasil.core.dal.DAL;
import de.yggdrasil.core.dal.datasource.models.usertype.ConfigJSON;
import de.yggdrasil.core.dal.request.implementation.ConfigReadRequest;
import de.yggdrasil.core.dal.util.profile.ProfileProduction;
import de.yggdrasil.core.extension.ExtensionLoader;
import de.yggdrasil.core.io.ExtensionCopy;
import de.yggdrasil.core.util.ShutdownThread;
import net.minestom.server.MinecraftServer;

public class ServerCore {

    private final static ServerCore servercore = new ServerCore();
    private MinecraftServer server;
    private final ExtensionLoader extensionLoader = new ExtensionLoader();
    private ConfigJSON configuration;

    private ServerCore() {}

    public static void main(String[] args) {
        servercore.setupPhase();
        servercore.startPhase();
        servercore.prepareShutdown();
        servercore.loadExtensions();
        servercore.start();
    }

    private void setupPhase() {
        String serverID = System.getenv("serverid");
        if (serverID == null) throw new RuntimeException("Missing Server ID in enviorment");
        configuration = DAL.get(new ProfileProduction()).read(
                new ConfigReadRequest("serverconfiguration."+serverID))
                .data();
        ExtensionCopy.copyExtensions(configuration.getList("extensions"));
    }

    private void startPhase() {
        server = MinecraftServer.init();
        
    }

    private void loadExtensions(){
        extensionLoader.loadExtensions();
    }

    private void start(){
        server.start(configuration.get("network").getString("host"),
                Integer.parseInt(configuration.get("network").getString("port")));
    }

    private void prepareShutdown() {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
    }

}
