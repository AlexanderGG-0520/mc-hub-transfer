package io.github.mchubtransfer;

public record HubTransferConfig(
        HubConfig hub,
        String javaTransferFailureMessage,
        String bedrockReconnectMessage
) {
    public static HubTransferConfig defaultConfig() {
        return new HubTransferConfig(
                new HubConfig(
                        new HubEndpoint("play.alec-ofc.com", 25565),
                        new HubEndpoint("play.alec-ofc.com", 19132)
                ),
                "Unable to transfer you to the Java hub at {endpoint}. Please try again later.",
                "Bedrock players must reconnect to {endpoint}; automatic transfer is not supported."
        );
    }

    public HubTransferConfig withJavaHost(String host) {
        return new HubTransferConfig(hub.withJavaHost(host), javaTransferFailureMessage, bedrockReconnectMessage);
    }

    public HubTransferConfig withJavaPort(int port) {
        return new HubTransferConfig(hub.withJavaPort(port), javaTransferFailureMessage, bedrockReconnectMessage);
    }

    public HubTransferConfig withBedrockHost(String host) {
        return new HubTransferConfig(hub.withBedrockHost(host), javaTransferFailureMessage, bedrockReconnectMessage);
    }

    public HubTransferConfig withBedrockPort(int port) {
        return new HubTransferConfig(hub.withBedrockPort(port), javaTransferFailureMessage, bedrockReconnectMessage);
    }

    public HubTransferConfig withJavaTransferFailureMessage(String message) {
        return new HubTransferConfig(hub, message, bedrockReconnectMessage);
    }

    public HubTransferConfig withBedrockReconnectMessage(String message) {
        return new HubTransferConfig(hub, javaTransferFailureMessage, message);
    }

    public String toDisplayString() {
        return "Java hub: " + hub.java().asHostPort()
                + "\nBedrock hub: " + hub.bedrock().asHostPort()
                + "\nJava transfer failure message: " + javaTransferFailureMessage
                + "\nBedrock reconnect message: " + bedrockReconnectMessage;
    }
}
