package net.sunshow.trutest.client;

public enum TruTestCommand {

    // Set acknowledge on, so onReceive can always be triggered.
    SetAcknowledgeOn("{ZA1}"),

    // Clears All Session Files. (Empty default session file is created)
    ClearAllSessionFiles("{CL}"),

    // Clears life data or currently selected session.
    ResetCurrentSessionData("{FC}"),

    ;

    private final String code;

    TruTestCommand(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name();
    }

}
