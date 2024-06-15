package de.yggdrasil.core.util;

import de.yggdrasil.core.dal.DAL;
import de.yggdrasil.core.dal.datasource.models.usertype.ConfigJSON;
import de.yggdrasil.core.dal.request.implementation.ConfigReadRequest;

import java.util.List;

public class ServerConfiguration {

    private final String serverId;
    private final ConfigJSON configuration;

    public ServerConfiguration(String serverId) {
        this.serverId = serverId;
        configuration = DAL.get().read(
                new ConfigReadRequest(ServerConstants.CONFIG_READ_REQUEST_IDENTIFIER + serverId))
                .data();
    }

    public String getHost() {
        return configuration.get(ServerConstants.CONFIGURATION_KEY_NETWORK)
                .getString(ServerConstants.CONFIGURATION_KEY_HOST);
    }

    public int getPort() {
        return Integer.parseInt(configuration.get(ServerConstants.CONFIGURATION_KEY_PORT)
                .getString(ServerConstants.CONFIGURATION_KEY_PORT));
    }

    public List<String> getServerExtensions() {
        return configuration.getList(ServerConstants.CONFIGURATION_EXTENSION_KEY);
    }

}
