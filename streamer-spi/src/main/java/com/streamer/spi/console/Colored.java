package com.streamer.spi.console;

public interface Colored {

    Colored fontColor(final String colorName);

    Colored borderColor(final String colorName);
}
