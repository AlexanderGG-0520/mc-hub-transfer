package io.github.mchubtransfer;

public record HubEndpoint(String host, int port) {
    public String asHostPort() {
        return host + ":" + port;
    }
}
