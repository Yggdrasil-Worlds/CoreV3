package de.yggdrasil.core;

import de.yggdrasil.core.dal.DAL;
import de.yggdrasil.core.dal.datasource.models.usertype.ConfigJSON;
import de.yggdrasil.core.dal.request.implementation.ConfigReadRequest;
import de.yggdrasil.core.dal.util.profile.ProfileProduction;
import de.yggdrasil.core.extension.ExtensionLoader;
import de.yggdrasil.core.io.ExtensionCopy;
import de.yggdrasil.core.util.ExceptionStrings;
import de.yggdrasil.core.util.ServerConstants;
import de.yggdrasil.core.util.ShutdownThread;
import net.minestom.server.MinecraftServer;

/**
 * The ServerCore class is the heart of the server. It initializes, starts, and manages the server's lifecycle and its extensions.
 */
public class ServerCore {

    private static final ServerCore servercore = new ServerCore();
    private MinecraftServer server;
    private final ExtensionLoader extensionLoader = new ExtensionLoader();
    private ConfigJSON configuration;

    private ServerCore() {}

    /**
     * Main method that initializes, starts, and manage the shutdown of the server.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        servercore.setupPhase();
        servercore.startPhase();
        servercore.prepareShutdown();
        servercore.loadExtensions();
        servercore.start();
    }

    /**
     * Setup phase where the server configuration is loaded and the extensions are copied.
     */
    private void setupPhase() {
        String serverID = System.getenv(ServerConstants.SERVER_ID);
        if (serverID == null) throw new RuntimeException(ExceptionStrings.MISSING_SERVER_ID);
        configuration = DAL.get(new ProfileProduction()).read(
                new ConfigReadRequest(ServerConstants.CONFIG_READ_REQUEST_IDENTIFIER + serverID)).data();
        ExtensionCopy.copyExtensions(configuration.getList(ServerConstants.CONFIGURATION_EXTENSION_KEY));
    }

    /**
     * Initializes the Minecraft server.
     */
    private void startPhase() {
        server = MinecraftServer.init();
    }

    /**
     * Loads the extensions into the server.
     */
    private void loadExtensions() {
        extensionLoader.loadExtensions();
    }

    /**
     * Starts the server with the configured network settings.
     */
    private void start() {
        server.start(configuration.get(ServerConstants.CONFIGURATION_KEY_NETWORK)
                        .getString(ServerConstants.CONFIGURATION_KEY_HOST),
                Integer.parseInt(configuration.get(ServerConstants.CONFIGURATION_KEY_PORT)
                        .getString(ServerConstants.CONFIGURATION_KEY_PORT)));
    }

    /**
     * Prepares the server for shutdown by adding a shutdown hook.
     */
    private void prepareShutdown() {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
    }
}
