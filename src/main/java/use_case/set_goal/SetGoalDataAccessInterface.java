package use_case.set_goal;

import java.util.List;

import entity.Goal;


public interface SetGoalDataAccessInterface {
    void saveGoal(Goal goal);

    List<Goal> getAll();
}
