package use_case.set_goal;

import java.io.IOException;
import java.util.List;

import entity.Goal;

public interface SetGoalDataAccessInterface {
    /**
     * Saves the given goal to the data source.
     * @param goal the goal to save
     * @throws IOException if an I/O error occurs while saving the goal
     */
    void saveGoal(Goal goal) throws IOException;

    /**
     * Retrieves all goals from the data source.
     *
     * @return a list of all goals
     */
    List<Goal> getAll();
}
