package de.yggdrasil.core;

import de.yggdrasil.core.extension.ExtensionLoader;
import de.yggdrasil.core.io.ExtensionCopy;
import de.yggdrasil.core.util.*;
import net.minestom.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ServerCore class is the heart of the server. It initializes, starts, and manages the server's lifecycle and its extensions.
 */
public class ServerCore {

    private static final ServerCore servercore = new ServerCore();
    private MinecraftServer server;
    private final ExtensionLoader extensionLoader = new ExtensionLoader();
    private ServerConfiguration configuration;

    private final Logger logger = LoggerFactory.getLogger(ServerCore.class);

    private ServerCore() {}

    /**
     * Main method that initializes, starts, and manage the shutdown of the server.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        servercore.setupPhase();
        servercore.initPhase();
        servercore.prepareShutdown();
        servercore.loadExtensions();
        servercore.start();
    }

    /**
     * Setup phase where the server configuration is loaded and the extensions are copied.
     */
    private void setupPhase() {
        logger.info(LoggingStrings.SERVER_SETUP_PHASE);
        String serverID = System.getenv(ServerConstants.SERVER_ID);
        if (serverID == null) throw new RuntimeException(ExceptionStrings.MISSING_SERVER_ID);
        configuration = new ServerConfiguration(serverID);
        ExtensionCopy.copyExtensions(configuration.getServerExtensions());
    }

    /**
     * Initializes the Minecraft server.
     */
    private void initPhase() {
        logger.info(LoggingStrings.SERVER_INIT_PHASE);
        server = MinecraftServer.init();
    }

    /**
     * Loads the extensions into the server.
     */
    private void loadExtensions() {
        logger.info(LoggingStrings.SERVER_LOAD_EXTENSIONS_PHASE);
        extensionLoader.loadExtensions();
    }

    /**
     * Starts the server with the configured network settings.
     */
    private void start() {
        logger.info(LoggingStrings.SERVER_START_PHASE);
        server.start(configuration.getHost(), configuration.getPort());
    }

    /**
     * Prepares the server for shutdown by adding a shutdown hook.
     */
    private void prepareShutdown() {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
    }
}
