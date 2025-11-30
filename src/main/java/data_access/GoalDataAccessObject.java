package data_access;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entity.Goal;
import use_case.autosave.AutosaveDataAccessInterface;
import use_case.set_goal.SetGoalDataAccessInterface;

public class GoalDataAccessObject implements SetGoalDataAccessInterface, AutosaveDataAccessInterface {
    private final File jsonFile;

    private final Gson gson;

    private List<Goal> goals;

    public GoalDataAccessObject(String jsonFilePath) {
        this.jsonFile = new File(jsonFilePath);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(YearMonth.class, new YearMonthAdapter())
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
                final Type listType = new TypeToken<List<Goal>>() {
                }.getType();

                this.goals = gson.fromJson(reader, listType);
                if (this.goals == null) {
                    this.goals = new ArrayList<>();
                }
            }
            catch (IOException ex) {
                System.err.println("Error loading goals from JSON: " + ex.getMessage());
                this.goals = new ArrayList<>();
            }
        }
        else {
            this.goals = new ArrayList<>();
        }
    }

    @Override
    public void save() {
        try {
            if (jsonFile.getParentFile() != null && !jsonFile.getParentFile().exists()) {
                jsonFile.getParentFile().mkdirs();
            }

            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(goals, writer);
            }
        }
        catch (IOException ex) {
            System.err.println("Error saving goals to JSON: " + ex.getMessage());
            throw new RuntimeException("Failed to save goals", ex);
        }
    }

    @Override
    public void saveGoal(Goal goal) {
        goals.removeIf(geo -> geo.getMonth().equals(goal.getMonth()));
        goals.add(goal);
        save();
    }

    public List<Goal> getAll() {
        return new ArrayList<>(goals);
    }
}
