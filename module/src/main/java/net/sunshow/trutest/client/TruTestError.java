package net.sunshow.trutest.client;

public enum TruTestError {

    OK(0, "^"),
    Unknown(-1, ""),
    CommandCouldNotBeExecutedAtThisTime(1, "(14)"),
    ;

    private int value;
    private String code;

    TruTestError(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }

}
