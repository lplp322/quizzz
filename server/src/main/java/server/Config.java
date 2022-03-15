/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.database.ActivityRepository;

@Configuration
public class Config {
    @Autowired
    private ActivityRepository dtBase;

    /**
     * returns random
     * @return a Random object
     */
    @Bean
    public Random getRandom() {
        return new Random();
    }

    /**
     * Adds 4 activities in case the database is empty
     */
    @Bean
    public void addTempActivities() {
        if(dtBase.getAllActivities().size() < 4) {
            dtBase.save(new Activity("Taking a hot shower for 6 minutes",
                    4000,
                    "https://www.quora.com/How-can-I-estimate-the-kWh-of-electricity-when-I-take-a-shower",
                    "00/shower.png"));
            dtBase.save(new Activity("Charging your smartphone at night",
                    10,
                    "https://9to5mac.com/2021/09/16/iphone-13-battery-life/",
                    "00/smartphone.png"));
            dtBase.save(new Activity("Using a refrigerator for 1 month",
                    40000,
                    "https://www.kompulsa.com/refrigerator-power-consumption-deciphering-the-label/",
                    "00/fridge.png"));
            dtBase.save(new Activity("Vacuuming your home for 30min",
                    900,
                    "https://www.philips.com.sg/c-p/FC9350_61/3000-series-bagless-vacuum-cleaner",
                    "00/vacuuming.png"));
        }
        //System.out.println(dtBase.getAllActivities().size());
    }
}