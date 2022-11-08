import fruitshop.model.Operation;
import org.junit.Test;

public class TestOperation {
    private static final char UNEXCITING_OPERATION = 'f';

    @Test(expected = RuntimeException.class)
    public void getInvalidOperation_notOk() {
        Operation.getByValue(UNEXCITING_OPERATION);
    }
}