package use_case;

import entity.Category;
import entity.Source;
import entity.Transaction;
import use_case.import_statement.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportStatementInteractorTest {
    final String api_key = System.getenv("API_KEY");

    class InMemoryImportDAO implements ImportStatementDataAccessInterface {

        final Map<Source, Category> sources = new HashMap<>();
        final List<Transaction> transactions = new ArrayList<>();

        @Override
        public void addTransaction(Transaction transaction) {
            transactions.add(transaction);
        }

        @Override
        public boolean sourceExists(Source sourceName) {
            return sources.containsKey(sourceName);
        }

        @Override
        public void addSourceCategory(Source source, Category category) {
            sources.put(source, category);
        }
    }

    class Message {
        String message;
        public void setMessage(String message) {
            this.message = message;
        }
    }

    class MockPresenter implements ImportStatementOutputBoundary {

        Message message;
        public MockPresenter(Message message) {
            this.message = message;
        }
        @Override
        public void prepareSuccessView(ImportStatementOutputData outputData) {

            final YearMonth month = outputData.getStatementMonth();
            final String message = "Importing Successful for " + month.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
            this.message.setMessage(message);

        }

        @Override
        public void prepareFailView(String errorMessage) {
            message.setMessage(errorMessage);

        }
    }

    @Test
    void blankFilePath() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer(api_key));
        ImportStatementInputData input = new ImportStatementInputData("");

        interactor.execute(input);
        assertEquals("blank file path", message.message);
        assertTrue(dao.transactions.isEmpty());
        assertTrue(dao.sources.isEmpty());
    }

    @Test
    void invalidFilePath() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer(api_key));
        ImportStatementInputData input = new ImportStatementInputData("dhhdhd");

        interactor.execute(input);
        assertEquals("file does not exist", message.message);
        assertTrue(dao.transactions.isEmpty());
        assertTrue(dao.sources.isEmpty());
    }

    @Test
    void notJsonArray() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer(api_key));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data1.json");

        interactor.execute(input);
        assertEquals("file does not contain a JSON array", message.message);
        assertTrue(dao.transactions.isEmpty());
        assertTrue(dao.sources.isEmpty());
    }

    @Test
    void noTransactions () {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer(api_key));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data2.json");

        interactor.execute(input);
        assertEquals("no transactions found", message.message);
        assertTrue(dao.transactions.isEmpty());
        assertTrue(dao.sources.isEmpty());
    }

    @Test
    void transactionMissingSource() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer(api_key));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data3.json");

        interactor.execute(input);
        assertEquals("unsupported file", message.message);
        assertTrue(dao.transactions.isEmpty());
        assertTrue(dao.sources.isEmpty());
    }

    @Test
    void uncategorizedTransactionMissingAnotherField() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer(api_key));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data4.json");

        interactor.execute(input);
        assertEquals("unsupported file", message.message);
        assertTrue(dao.transactions.isEmpty());
        assertTrue(dao.sources.isEmpty());
    }

    @Test
    void categorizedTransactionMissingAnotherField() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        dao.addSourceCategory(new Source("Employer"), new Category("Income"));
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer(api_key));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data4.json");

        interactor.execute(input);
        assertEquals("unsupported file", message.message);
        assertTrue(dao.transactions.isEmpty());
        dao.sources.remove(new Source("Employer"));
        assertTrue(dao.sources.isEmpty());
    }

    @Test
    void troubleWithAPI() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer("123"));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data5.json");

        interactor.execute(input);
        assertEquals("could not categorize transactions", message.message);
        assertTrue(dao.transactions.isEmpty());
        assertTrue(dao.sources.isEmpty());
    }

    @Test
    void successTestAllUncategorized() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer("123"));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data5.json");

        interactor.execute(input);
        assertEquals("Importing Successful for March 2024", message.message);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(new Source("Uber"), -12.50, LocalDate.parse("2024-03-05")));
        transactions.add(new Transaction(new Source("Starbucks"), -8.75,
            LocalDate.parse("2024-03-07")));
        transactions.add(new Transaction(new Source("Amazon"), -49.99,
            LocalDate.parse("2024-03-10")));
        transactions.add(new Transaction(new Source("Employer"), 100, LocalDate.parse("2024-03-11")));
        assertTrue(dao.transactions.containsAll(transactions));
        assertTrue(dao.sources.containsKey(new Source("Uber")));
        assertTrue(dao.sources.containsKey(new Source("Starbucks")));
        assertTrue(dao.sources.containsKey(new Source("Amazon")));
        assertTrue(dao.sources.containsKey(new Source("Employer")));
    }

    @Test
    void successTestAllCategorized() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer("123"));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data5.json");
        dao.sources.put(new Source("Uber"), new Category("Transportation"));
        dao.sources.put(new Source("Starbucks"), new Category("Food and Dining"));
        dao.sources.put(new Source("Amazon"), new Category("Shopping"));
        dao.sources.put(new Source("Employer"), new Category("Income"));

        interactor.execute(input);
        assertEquals("Importing Successful for March 2024", message.message);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(new Source("Uber"), -12.50, LocalDate.parse("2024-03-05")));
        transactions.add(new Transaction(new Source("Starbucks"), -8.75,
            LocalDate.parse("2024-03-07")));
        transactions.add(new Transaction(new Source("Amazon"), -49.99,
            LocalDate.parse("2024-03-10")));
        transactions.add(new Transaction(new Source("Employer"), 100,
            LocalDate.parse("2024-03-11")));
        assertTrue(dao.transactions.containsAll(transactions));
        assertTrue(dao.sources.containsKey(new Source("Uber")));
        assertTrue(dao.sources.containsKey(new Source("Starbucks")));
        assertTrue(dao.sources.containsKey(new Source("Amazon")));
        assertTrue(dao.sources.containsKey(new Source("Employer")));
    }

    @Test
    void successTestMixed() {
        InMemoryImportDAO dao = new InMemoryImportDAO();
        Message message = new Message();
        MockPresenter presenter = new MockPresenter(message);
        ImportStatementInteractor interactor = new ImportStatementInteractor(dao, presenter,
            new GeminiCategorizer("123"));
        ImportStatementInputData input = new ImportStatementInputData("src/test/java/mock_data5.json");
        dao.sources.put(new Source("Uber"), new Category("Transportation"));
        dao.sources.put(new Source("Starbucks"), new Category("Food and Dining"));

        interactor.execute(input);
        assertEquals("Importing Successful for March 2024", message.message);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(new Source("Uber"), -12.50, LocalDate.parse("2024-03-05")));
        transactions.add(new Transaction(new Source("Starbucks"), -8.75,
            LocalDate.parse("2024-03-07")));
        transactions.add(new Transaction(new Source("Amazon"), -49.99,
            LocalDate.parse("2024-03-10")));
        transactions.add(new Transaction(new Source("Employer"), 100,
            LocalDate.parse("2024-03-11")));
        assertTrue(dao.transactions.containsAll(transactions));
        assertTrue(dao.sources.containsKey(new Source("Uber")));
        assertTrue(dao.sources.containsKey(new Source("Starbucks")));
        assertTrue(dao.sources.containsKey(new Source("Amazon")));
        assertTrue(dao.sources.containsKey(new Source("Employer")));
    }

}