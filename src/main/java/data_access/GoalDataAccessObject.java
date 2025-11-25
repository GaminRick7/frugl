package data_access;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import entity.Goal;
import use_case.set_goal.SetGoalDataAccessInterface;

public class GoalDataAccessObject implements SetGoalDataAccessInterface {
    private final File jsonFile;

    private final Gson gson;

    private List<Goal> goals;

    public GoalDataAccessObject(String jsonFilePath) {
        this.jsonFile = new File(jsonFilePath);
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.goals = new ArrayList<>();
        load();
    }

    public GoalDataAccessObject() {
        this("goals.json");
    }

    private void load() {
        if (jsonFile.exists() && jsonFile.length() > 0) {
            try (FileReader reader = new FileReader(jsonFile)) {
                Type listType = new TypeToken<List<Goal>>() {
                }.getType();

                this.goals = gson.fromJson(reader, listType);
                if (this.goals == null) {
                    this.goals = new ArrayList<>();
                }
            } catch (IOException e) {
                System.err.println("Error loading goals from JSON: " + e.getMessage());
                this.goals = new ArrayList<>();
            }
        } else {
            this.goals = new ArrayList<>();
        }
    }

    private void save() {
        try {
            if (jsonFile.getParentFile() != null && !jsonFile.getParentFile().exists()) {
                jsonFile.getParentFile().mkdirs();
            }

            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(goals, writer);
            }
        } catch (IOException e) {
            System.err.println("Error saving goals to JSON: " + e.getMessage());
            throw new RuntimeException("Failed to save goals", e);
        }
    }

    @Override
    public void saveGoal(Goal goal) {
        goals.removeIf(g -> g.getMonth().equals(goal.getMonth()));
        goals.add(goal);
        save();
    }

    public List<Goal> getAll() {
        return new ArrayList<>(goals);
    }
}
