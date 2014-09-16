package jp.gr.java_conf.star_diopside.spark.commons.test.dataset.csv;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import jp.gr.java_conf.star_diopside.spark.commons.test.util.TestUtils;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.junit.Test;

public class CsvProducerExTest {

    private static final int ORDERS_ROWS_NUMBER = 5;
    private static final int ORDERS_COLUMNS_NUMBER = 2;
    private static final int ORDERS_ROW_ROWS_NUMBER = 3;
    private static final int ORDERS_ROW_COLUMNS_NUMBER = 3;
    private static final Path THE_DIRECTORY = TestUtils.findTestDataFile(CsvProducerExTest.class, "csv/orders");

    @Test
    public void testProduceFromFolder() throws DataSetException {
        CsvProducerEx producer = new CsvProducerEx(THE_DIRECTORY, StandardCharsets.UTF_8);
        CachedDataSet consumer = new CachedDataSet();

        producer.setConsumer(consumer);
        producer.produce();
        final ITable[] tables = consumer.getTables();
        assertThat("expected 2 tables", tables, arrayWithSize(2));

        final ITable orders = consumer.getTable("orders");
        assertThat("orders table not found", orders, is(notNullValue()));
        assertThat("wrong number of rows", orders.getRowCount(), is(ORDERS_ROWS_NUMBER));
        assertThat("wrong number of columns", orders.getTableMetaData().getColumns(), arrayWithSize(ORDERS_COLUMNS_NUMBER));

        final ITable ordersRow = consumer.getTable("orders_row");
        assertThat("orders_row table not found", ordersRow, is(notNullValue()));
        assertThat("wrong number of rows", ordersRow.getRowCount(), is(ORDERS_ROW_ROWS_NUMBER));
        assertThat("wrong number of columns", ordersRow.getTableMetaData().getColumns(), arrayWithSize(ORDERS_ROW_COLUMNS_NUMBER));
    }
}
