import static org.junit.Assert.assertEquals;

import fruitshop.dao.FruitShopStorageDaoImpl;
import fruitshop.db.FruitShopStorage;
import fruitshop.model.FruitTransaction;
import fruitshop.model.Operation;
import fruitshop.service.impl.TransactionRowParserImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionRowParserTest {
    private static final int INDEX_TO_PUT_IN_LIST_MIDDLE_FIRST = 2;
    private static final int INDEX_TO_PUT_IN_LIST_MIDDLE_SECOND = 6;
    private static final int INDEX_TO_PUT_IN_LIST_MIDDLE_THIRD = 8;
    private static final int INDEX_TO_PUT_IN_LIST_START = 0;
    private static final int INDEX_TO_PUT_IN_LIST_AFTER_BALANCE = 3;
    private static final String TEST_FRUIT_AND_COUNT_UNFINISHED_ROW = "apple,32";
    private static List<String> validDataRows;
    private static FruitShopStorageDaoImpl dao;
    private static TransactionRowParserImpl transactionRowParser;
    private List<String> currentDataRows;

    @BeforeClass
    public static void beforeClass() {
        validDataRows = List.of(
                "b,banana,20",
                "b,apple,100",
                "s,banana,100",
                "p,banana,13",
                "r,apple,10",
                "p,apple,20",
                "p,banana,5",
                "s,banana,50"
        );
        dao = new FruitShopStorageDaoImpl();
        transactionRowParser = new TransactionRowParserImpl(dao);
    }

    @Before
    public void setUp() {
        currentDataRows = new ArrayList<>(validDataRows);
    }

    @Test(expected = RuntimeException.class)
    public void parse_incorrectColumnSizeInListEmptyRow_notOk() {
        currentDataRows.add(INDEX_TO_PUT_IN_LIST_MIDDLE_FIRST, "");
        transactionRowParser.parse(currentDataRows);
    }

    @Test(expected = RuntimeException.class)
    public void parse_incorrectColumnSizeInListLess_notOk() {
        currentDataRows.add(INDEX_TO_PUT_IN_LIST_MIDDLE_SECOND,
                TEST_FRUIT_AND_COUNT_UNFINISHED_ROW);
        transactionRowParser.parse(currentDataRows);
    }

    @Test(expected = RuntimeException.class)
    public void parse_incorrectColumnSizeInListExtraColumn_notOk() {
        currentDataRows.add(INDEX_TO_PUT_IN_LIST_MIDDLE_THIRD,
                "p," + TEST_FRUIT_AND_COUNT_UNFINISHED_ROW + ",extra-column"
        );
        transactionRowParser.parse(currentDataRows);
    }

    @Test(expected = RuntimeException.class)
    public void parse_incorrectOperationLength_bigger_notOk() {
        currentDataRows.add(INDEX_TO_PUT_IN_LIST_MIDDLE_THIRD,
                "purchase," + TEST_FRUIT_AND_COUNT_UNFINISHED_ROW
        );
        transactionRowParser.parse(currentDataRows);
    }

    @Test(expected = RuntimeException.class)
    public void parse_incorrectOperationLengthEmpty_notOk() {
        currentDataRows.add(INDEX_TO_PUT_IN_LIST_MIDDLE_THIRD,
                "," + TEST_FRUIT_AND_COUNT_UNFINISHED_ROW
        );
        transactionRowParser.parse(currentDataRows);
    }

    @Test(expected = RuntimeException.class)
    public void parse_wrongOperationOrderFirstOperationIsNotBalance_notOk() {
        currentDataRows.add(INDEX_TO_PUT_IN_LIST_START,
                "s," + TEST_FRUIT_AND_COUNT_UNFINISHED_ROW
        );
        transactionRowParser.parse(currentDataRows);
    }

    @Test(expected = RuntimeException.class)
    public void parse_wrongOperationOrderBalanceInMiddleAfterAlreadyBalanced_notOk() {
        int index = INDEX_TO_PUT_IN_LIST_AFTER_BALANCE + INDEX_TO_PUT_IN_LIST_MIDDLE_FIRST;
        currentDataRows.add(index,
                "b," + TEST_FRUIT_AND_COUNT_UNFINISHED_ROW
        );
        transactionRowParser.parse(currentDataRows);
    }

    @Test()
    public void parse_everythingOk_toBeOk() {
        transactionRowParser.parse(currentDataRows);
        List<FruitTransaction> expected = List.of(
                new FruitTransaction(Operation.getByValue('b'), "banana", 20),
                new FruitTransaction(Operation.getByValue('b'), "apple", 100),
                new FruitTransaction(Operation.getByValue('s'), "banana", 100),
                new FruitTransaction(Operation.getByValue('p'), "banana", 13),
                new FruitTransaction(Operation.getByValue('r'), "apple", 10),
                new FruitTransaction(Operation.getByValue('p'), "apple", 20),
                new FruitTransaction(Operation.getByValue('p'), "banana", 5),
                new FruitTransaction(Operation.getByValue('s'), "banana", 50)
        );
        assertEquals("Expected sizes to be equal to size of incoming list of rows",
                currentDataRows.size(), FruitShopStorage.fruitTransactions.size()
        );
        assertEquals("Expected to add in right order and valid elements of FruitsTransactions"
                        + " to fruit storage",
                expected,
                FruitShopStorage.fruitTransactions
        );
    }

    @After
    public void tearDown() {
        FruitShopStorage.fruitTransactions.clear();
    }
}
