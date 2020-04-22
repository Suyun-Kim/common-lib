package com.nx.lib.api.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String rootCauseClass = "";
        String rootCauseMessage = "";
        if (event.getFormattedMessage().startsWith("Template-LOG")) {
            String[] msgArr = event.getFormattedMessage().split("\\|");
            if(event.getLevel().equals(Level.ERROR)) {
                rootCauseClass = msgArr[2];
                rootCauseMessage = msgArr[3];
            } else if (event.getLevel().equals(Level.WARN)) {
                rootCauseClass = msgArr[2];
                rootCauseMessage = msgArr[4];
            } else {

            }
        } else {

        }

        return rootCauseClass + "#" + rootCauseMessage;
    }
}
