package fruitshop;

import fruitshop.dao.FruitShopStorageDaoImpl;
import fruitshop.model.FruitReport;
import fruitshop.model.Operation;
import fruitshop.service.files.impl.CsvFileReaderServiceImpl;
import fruitshop.service.files.impl.CsvFileWriterServiceImpl;
import fruitshop.service.impl.ReportGeneratorServiceImpl;
import fruitshop.service.impl.TransactionRowParserImpl;
import fruitshop.service.impl.TransactionsCalculatorServiceImpl;
import fruitshop.strategy.operation.handlers.impl.BalanceOperation;
import fruitshop.strategy.operation.handlers.impl.PurchaseOperation;
import fruitshop.strategy.operation.handlers.impl.ReturnOperation;
import fruitshop.strategy.operation.handlers.impl.SupplyOperation;
import fruitshop.strategy.operation.impl.OperationStrategyImpl;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FruitShop {
    private static final String RESOURCES_DIRECTORY = "src/main/resources";
    private static final Path INPUT_CSV_FILE = Path.of(RESOURCES_DIRECTORY, "/input_basic.csv");
    private static final Path OUTPUT_CSV_FILE = Path.of(RESOURCES_DIRECTORY, "/report.csv");

    public static void main(String[] args) {
        FruitShopStorageDaoImpl storageDao = new FruitShopStorageDaoImpl();
        List<String> data = new CsvFileReaderServiceImpl().readFromFile(INPUT_CSV_FILE);
        TransactionRowParserImpl transactionRowParser = new TransactionRowParserImpl(storageDao);
        transactionRowParser.parse(data);
        OperationStrategyImpl operationStrategy = new OperationStrategyImpl(Map.of(
                Operation.BALANCE, new BalanceOperation(),
                Operation.PURCHASE, new PurchaseOperation(),
                Operation.RETURN, new ReturnOperation(),
                Operation.SUPPLY, new SupplyOperation()
        ));
        List<FruitReport> calculatedReport =
                new TransactionsCalculatorServiceImpl(operationStrategy, storageDao).calculate();
        String generatedReportOutput = new ReportGeneratorServiceImpl().generate(calculatedReport);
        CsvFileWriterServiceImpl fileWriterService = new CsvFileWriterServiceImpl();
        fileWriterService.writeToFile(OUTPUT_CSV_FILE, generatedReportOutput);
    }
}