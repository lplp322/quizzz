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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import server.database.ActivityRepository;

import java.io.File;
import java.util.Optional;

@SpringBootApplication
@EntityScan(basePackages = { "commons", "server" })
public class Main {

    @Autowired
    private ActivityRepository repo;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    /**
     * Imports activities into repository on server startup
     */
    @EventListener(ApplicationReadyEvent.class)
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        File rootFolder = new File("./server/src/main/resources/activities");
        for (final File folder : rootFolder.listFiles()){
            for (final File file : folder.listFiles()) {
                if ( file.getName().endsWith(".json") ) {
                    try {
                        JsonActivity temp = mapper.readValue(file, JsonActivity.class);
                        Optional<Activity> repoActivity = repo.findByTitle(temp.getTitle());
                        // Filter activities if won't fit in repository or already in repository
                        if (temp.getConsumption_in_wh() > Integer.MAX_VALUE || temp.getSource().length() > 255 ||
                                repoActivity.isPresent()) {
                            continue;
                        }
                        String ac = file.getPath();
                        ac = ac.substring(0, ac.length() - 5);
                        String image;
                        if (new File(ac + ".jpg").exists()) {
                            image = ac + ".jpg";
                        }
                        else if (new File(ac + ".jpeg").exists()) {
                            image = ac + ".jpeg";
                        }
                        else {
                            image = ac + ".png";
                        }
                        Activity finalActivity = temp.toActivity(image);
                        repo.save(finalActivity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // I was testing adding into the repository
    /*@Bean
    CommandLineRunner commandLineRunner(ActivityRepository activityRepository) {
        return args -> {
            System.out.println(new Question(activityRepository));
        };
    }*/
}