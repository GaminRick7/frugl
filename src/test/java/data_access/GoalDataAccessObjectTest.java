package data_access;

import entity.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

public class GoalDataAccessObjectTest {

    private Path tempFile;
    private GoalDataAccessObject dao;

    @BeforeEach
    void setup() throws Exception {
        // Create a temporary JSON file for testing

        tempFile = Files.createTempFile("goals_test", ".json");
        dao = new GoalDataAccessObject(tempFile.toString());
    }

    @Test
    void testSaveAndLoadGoal() throws IOException {
        YearMonth ym = YearMonth.of(2025, 1);   // January 2025
        Goal g = new Goal(ym, 500);

        dao.saveGoal(g);
        GoalDataAccessObject dao2 = new GoalDataAccessObject(tempFile.toString());

        assertEquals(1, dao2.getAll().size());
        assertEquals(YearMonth.of(2025, 1) , dao2.getAll().get(0).getMonth());
        assertEquals(500, dao2.getAll().get(0).getGoalAmount());
    }

    @Test
    void testEmptyFileLoadsAsEmptyList() throws Exception {
        Files.write(tempFile, new byte[0]);

        GoalDataAccessObject dao2 = new GoalDataAccessObject(tempFile.toString());

        assertTrue(dao2.getAll().isEmpty());
    }
}
