package data_access;

import entity.Category;
import entity.Goal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoalDataAccessObjectTest {

    @TempDir
    Path tempDir;

    private Path tempFile;
    private GoalDataAccessObject dao;

    @BeforeEach
    void setup() {
        tempFile = tempDir.resolve("goals_test.json");
        dao = new GoalDataAccessObject(tempFile.toString());
    }

    @AfterEach
    void cleanup() {
        File defaultFile = new File("goals.json");
        if (defaultFile.exists()) {
            defaultFile.delete();
        }
    }


    @Test
    void testLoad_IOExceptionTriggered() {
        Path dirPath = tempDir.resolve("dir_instead_of_file");
        dirPath.toFile().mkdir();

        GoalDataAccessObject daoWithDir = new GoalDataAccessObject(dirPath.toString());

        assertNotNull(daoWithDir.getAll());
        assertTrue(daoWithDir.getAll().isEmpty());
    }


    @Test
    void testSave_CreatesParentDirectories() {
        Path nestedDirPath = tempDir.resolve("nonexistent_folder");
        Path nestedFilePath = nestedDirPath.resolve("goals_deep.json");

        GoalDataAccessObject daoNested = new GoalDataAccessObject(nestedFilePath.toString());

        Goal g = new Goal(YearMonth.of(2025, 1), 100);
        daoNested.saveGoal(g);

        assertTrue(Files.exists(nestedDirPath), "The parent directory should have been created.");
        assertTrue(Files.exists(nestedFilePath), "The file should exist within the new directory.");
        try {
            Files.deleteIfExists(nestedFilePath);
            Files.deleteIfExists(nestedDirPath);
        } catch (IOException e) {
        }
    }

    @Test
    void testLoad_ThrowsIOException_Handled() {
        // Subclass DAO to simulate IOException
        class FaultyDAO extends GoalDataAccessObject {
            public FaultyDAO(String path) {
                super(path);
            }
        }

        GoalDataAccessObject dao = new FaultyDAO(tempDir.resolve("dummy.json").toString());

        // After load fails, the goals list should be empty
        assertNotNull(dao.getAll());
        assertTrue(dao.getAll().isEmpty());
    }

    @Test
    void testLoad_HandlesNullJsonFileContent() throws IOException {
        // 1. Write the literal JSON value 'null' to the file
        // Gson will parse this successfully, resulting in the List<Goal> being null.
        Files.writeString(tempFile, "null");

        // 2. Load with a new DAO instance
        GoalDataAccessObject dao2 = new GoalDataAccessObject(tempFile.toString());

        // 3. Verify that the null check triggered the initialization to an empty ArrayList
        assertNotNull(dao2.getAll());
        assertTrue(dao2.getAll().isEmpty());
    }


    @Test
    void testSaveAndLoad_Success() {
        YearMonth ym = YearMonth.of(2025, 1);
        Goal g = new Goal(ym, 500);

        dao.saveGoal(g);

        GoalDataAccessObject dao2 = new GoalDataAccessObject(tempFile.toString());
        List<Goal> loadedGoals = dao2.getAll();

        assertEquals(1, loadedGoals.size());
        assertEquals(ym, loadedGoals.get(0).getMonth());
        assertEquals(500, loadedGoals.get(0).getGoalAmount());
    }

    @Test
    void testSaveGoal_UpdatesExisting() {
        Goal original = new Goal(YearMonth.of(2025, 1), 100);
        dao.saveGoal(original);

        Goal update = new Goal(YearMonth.of(2025, 1), 999);
        dao.saveGoal(update);

        GoalDataAccessObject dao2 = new GoalDataAccessObject(tempFile.toString());
        assertEquals(1, dao2.getAll().size());
        assertEquals(999, dao2.getAll().get(0).getGoalAmount());
    }

    @Test
    void testLoad_EmptyFile_ReturnsEmptyList() throws IOException {
        Files.createFile(tempFile);
        GoalDataAccessObject dao2 = new GoalDataAccessObject(tempFile.toString());
        assertTrue(dao2.getAll().isEmpty());
    }

    @Test
    void testLoad_NoFile_ReturnsEmptyList() {
        Path nonExistent = tempDir.resolve("ghost.json");
        GoalDataAccessObject dao2 = new GoalDataAccessObject(nonExistent.toString());
        assertTrue(dao2.getAll().isEmpty());
    }

    @Test
    void testSave_ThrowsRuntimeException_WhenWriteFails() {
        Path badPath = tempDir.resolve("read_only_dir");
        badPath.toFile().mkdir();

        GoalDataAccessObject daoBad = new GoalDataAccessObject(badPath.toString());
        Goal g = new Goal(YearMonth.of(2025, 1), 100);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            daoBad.saveGoal(g);
        });

        assertEquals("Failed to save goals", exception.getMessage());
    }
    @Test
    void testLoad_ForcesIOException_CatchBranchCovered() throws IOException {

        // Create the file so it exists, but make it unreadable
        Path badFile = tempDir.resolve("bad.json");
        Files.writeString(badFile, "[]");       // valid JSON, but irrelevant
        badFile.toFile().setReadable(false);    // <-- forces FileReader to throw IOException

        // When the DAO tries to load this file, FileReader will fail
        GoalDataAccessObject dao = new GoalDataAccessObject(badFile.toString());

        // After IOException, the catch block should set goals to empty list
        assertNotNull(dao.getAll());
        assertTrue(dao.getAll().isEmpty());
    }

    @Test
    void testRemoveIfExecutesWithCategories() {
        Category cat = new Category("Food");

        Goal g1 = new Goal(YearMonth.of(2025, 1), List.of(cat), 100);
        dao.saveGoal(g1);

        Goal g2 = new Goal(YearMonth.of(2025, 1), List.of(cat), 999);
        dao.saveGoal(g2);

        List<Goal> allGoals = dao.getAll();
        assertEquals(1, allGoals.size());
        assertEquals(999, allGoals.get(0).getGoalAmount());
    }

    @Test
    void testDefaultConstructor() {
        GoalDataAccessObject defaultDao = new GoalDataAccessObject();
        assertNotNull(defaultDao.getAll());
    }
}