
package com.example.bibliotheque.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bibliotheque")
public class BibliothequeProperties {

    private int readingAvgTime;

    public int getReadingAvgTime() {
        return readingAvgTime;
    }

    public void setReadingAvgTime(int readingAvgTime) {
        this.readingAvgTime = readingAvgTime;
    }
}
