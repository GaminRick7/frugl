package entity;

import java.time.YearMonth;
import java.util.List;

public class GoalTree {

    private String status;

    private Goal goal;

    private int xCoordinate;

    private int yCoordinate;

    public GoalTree(Goal goal, int x_coordinate, int y_coordinate) {
        this.goal = goal;
        this.xCoordinate = x_coordinate;
        this.yCoordinate = y_coordinate;
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
    /**
     * Returns the x-coordinate of this point.
     *
     * @return the x-coordinate
     */
    public int getxCoordinate() {
        return xCoordinate;
    }

    /**
     * Returns the y-coordinate of this point.
     *
     * @return the y-coordinate
     */
    public int getyCoordinate() {
        return yCoordinate;
    }

    /**
     * Sets the coordinates of this point.
     *
     * @param newxCoordinate the new x-coordinate
     * @param newyCoordinate the new y-coordinate
     */

    public void setCoordinates(int newxCoordinate, int newyCoordinate) {
        this.xCoordinate = newxCoordinate;
        this.yCoordinate = newyCoordinate;
    }
    /**
     * Sets the x-coordinate.
     *
     * @param newxCoordinate the new x-coordinate
     */

    public void setxCoordiante(int newxCoordinate) {
        this.xCoordinate = newxCoordinate;
    }

    /**
     * Sets the y-coordinate.
     *
     * @param newyCoordinate the new y-coordinate
     */

    public void setyCoordinate(int newyCoordinate) {
        this.yCoordinate = newyCoordinate;
    }
    /**
     * Updates the status of this goal based on a list of transactions.
     * @param transactions the list of transactions to evaluate against the goal
     */

    public void updateStatus(List<Transaction> transactions) {
        final YearMonth currentMonth = YearMonth.now();
        final YearMonth goalMonth = goal.getMonth();
        double spent = 0;
        final float goalAmount = goal.getGoalAmount();

        for (Transaction transaction : transactions) {
            spent += transaction.getAmount();
        }

        if (currentMonth.isAfter(goalMonth) || currentMonth.equals(goalMonth)) {
            if (spent <= goalAmount) {
                this.status = "healthy";
                // Goal achieved
            }
            else {
                this.status = "dead";
                // Goal failed
            }
        }
        else if (currentMonth.isBefore(goalMonth)) {
            this.status = "sapling";
        }
    }
}
