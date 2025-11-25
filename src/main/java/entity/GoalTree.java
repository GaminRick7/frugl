package entity;

import java.time.YearMonth;
import java.util.List;

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

    public void updateStatus(List<Transaction> transactions) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth goalMonth = goal.getMonth();
        double spent = 0;
        float goalAmount = goal.getGoalAmount();

        for (Transaction transaction : transactions) {
            spent += transaction.getAmount();
        }

        if (currentMonth.isAfter(goalMonth) || currentMonth.equals(goalMonth)) {
            if (spent <= goalAmount) {
                this.status = "healthy"; // Goal achieved
            } else {
                this.status = "dead"; // Goal failed
            }
        } else if (currentMonth.isBefore(goalMonth)) {
            this.status = "sapling";
        }
    }
}
