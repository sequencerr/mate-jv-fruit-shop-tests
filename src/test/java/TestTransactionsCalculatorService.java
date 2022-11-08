import static org.junit.Assert.assertEquals;

import fruitshop.dao.FruitShopStorageDaoImpl;
import fruitshop.db.FruitShopStorage;
import fruitshop.model.FruitReport;
import fruitshop.model.FruitTransaction;
import fruitshop.model.Operation;
import fruitshop.service.impl.TransactionsCalculatorServiceImpl;
import fruitshop.strategy.operation.handlers.impl.BalanceOperation;
import fruitshop.strategy.operation.handlers.impl.PurchaseOperation;
import fruitshop.strategy.operation.handlers.impl.ReturnOperation;
import fruitshop.strategy.operation.handlers.impl.SupplyOperation;
import fruitshop.strategy.operation.impl.OperationStrategyImpl;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class TestTransactionsCalculatorService {
    private static final TransactionsCalculatorServiceImpl transactionsCalculator =
            new TransactionsCalculatorServiceImpl(
                    new OperationStrategyImpl(
                            Map.of(
                                    Operation.BALANCE, new BalanceOperation(),
                                    Operation.PURCHASE, new PurchaseOperation(),
                                    Operation.RETURN, new ReturnOperation(),
                                    Operation.SUPPLY, new SupplyOperation()
                            )),
                    new FruitShopStorageDaoImpl());

    @Before
    public void setUp() {
        FruitShopStorage.fruitTransactions.clear();
    }

    @Test
    public void calculate_negativeAndEmptyAmountInReport_isOk() {
        FruitShopStorage.fruitTransactions.addAll(List.of(
                new FruitTransaction(Operation.getByValue('b'), "banana", 3682),
                new FruitTransaction(Operation.getByValue('b'), "apple", 100),
                new FruitTransaction(Operation.getByValue('b'), "pineapple", 1427),
                new FruitTransaction(Operation.getByValue('b'), "apple", 10040),
                new FruitTransaction(Operation.getByValue('b'), "peach", 13),
                new FruitTransaction(Operation.getByValue('s'), "banana", 100),
                new FruitTransaction(Operation.getByValue('s'), "peach", 145),
                new FruitTransaction(Operation.getByValue('s'), "pineapple", 3466),
                new FruitTransaction(Operation.getByValue('r'), "peach", 42),
                new FruitTransaction(Operation.getByValue('s'), "banana", 50),
                new FruitTransaction(Operation.getByValue('p'), "peach", 31),
                new FruitTransaction(Operation.getByValue('p'), "peach", 169),
                new FruitTransaction(Operation.getByValue('r'), "pineapple", 123),
                new FruitTransaction(Operation.getByValue('p'), "apple", 20),
                new FruitTransaction(Operation.getByValue('p'), "pineapple", 657575)
        ));
        List<FruitReport> actual = transactionsCalculator.calculate();
        List<FruitReport> expected = List.of(
                new FruitReport("banana",3832),
                new FruitReport("apple",10020),
                new FruitReport("pineapple",-652559),
                new FruitReport("peach",0)
        );
        assertEquals("Expected sizes to be equal", expected.size(), actual.size());
        assertEquals("Expected report to write is not correct", expected, actual);
    }
}
