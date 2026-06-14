package io.github.mchubtransfer;

public record HubConfig(HubEndpoint java, HubEndpoint bedrock) {
    public HubConfig withJavaHost(String host) {
        return new HubConfig(new HubEndpoint(host, java.port()), bedrock);
    }

    public HubConfig withJavaPort(int port) {
        return new HubConfig(new HubEndpoint(java.host(), port), bedrock);
    }

    public HubConfig withBedrockHost(String host) {
        return new HubConfig(java, new HubEndpoint(host, bedrock.port()));
    }

    public HubConfig withBedrockPort(int port) {
        return new HubConfig(java, new HubEndpoint(bedrock.host(), port));
    }
}
