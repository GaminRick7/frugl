package entity;

import java.time.YearMonth;

public class GoalTree {

    private String status;

    private Goal goal;

    private int xCoordinate;

    private int yCoordinate;

    public GoalTree(Goal goal, int x, int y) {
        this.goal = goal;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.status = "sapling";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Goal getGoal() {
        return goal;
    }

    // we don't want to change goal so it will not have a setter

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setCoordinates(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public void updateStatus() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth goalMonth = goal.getMonth();
        int spent = 0;

        for (Category category : goal.getCategories()) {
            spent += 0;
        }
        // this loop will be updated based on later implementations
        // TODO: Implement loop

        if (currentMonth.equals(goalMonth)) {
            status = "sapling";
            return;
        }

        if (spent > goal.getGoalAmount()) {
            status = "rotten";
        } else {
            status = "healthy";
        }
    }
}
