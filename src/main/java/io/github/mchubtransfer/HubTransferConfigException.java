package io.github.mchubtransfer;

public final class HubTransferConfigException extends RuntimeException {
    public HubTransferConfigException(String message) {
        super(message);
    }

    public HubTransferConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
