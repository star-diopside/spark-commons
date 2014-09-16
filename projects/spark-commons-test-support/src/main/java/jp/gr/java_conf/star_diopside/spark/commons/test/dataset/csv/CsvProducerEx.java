/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package jp.gr.java_conf.star_diopside.spark.commons.test.dataset.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.common.handlers.IllegalInputCharacterException;
import org.dbunit.dataset.common.handlers.PipelineException;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.csv.CsvDataSetWriter;
import org.dbunit.dataset.csv.CsvParserException;
import org.dbunit.dataset.csv.CsvParserImpl;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fork {@link CsvDataSet} from DbUnit version 2.5.0
 * @author Federico Spinazzi
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.5 (Sep 17, 2003)
 */
public class CsvProducerEx implements IDataSetProducer {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CsvProducerEx.class);

    private static final IDataSetConsumer EMPTY_CONSUMER = new DefaultConsumer();
    private IDataSetConsumer _consumer = EMPTY_CONSUMER;
    private Path _theDirectory;
    private Charset _charset;

    public CsvProducerEx(String theDirectory, Charset charset) {
        _theDirectory = Paths.get(theDirectory);
        _charset = charset;
    }

    public CsvProducerEx(Path theDirectory, Charset charset) {
        _theDirectory = theDirectory;
        _charset = charset;
    }

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException {
        logger.debug("setConsumer(consumer) - start");

        _consumer = consumer;
    }

    public void produce() throws DataSetException {
        logger.debug("produce() - start");

        Path dir = _theDirectory;

        if (!Files.isDirectory(dir)) {
            throw new DataSetException("'" + dir.toAbsolutePath().toString() + "' should be a directory");
        }

        _consumer.startDataSet();
        try {
            List<String> tableSpecs = CsvProducerEx.getTables(dir, CsvDataSet.TABLE_ORDERING_FILE, _charset);
            for (String table : tableSpecs) {
                try {
                    produceFromFile(dir.resolve(table + ".csv"));
                } catch (CsvParserException e) {
                    throw new DataSetException("error producing dataset for table '" + table + "'", e);
                } catch (DataSetException e) {
                    throw new DataSetException("error producing dataset for table '" + table + "'", e);
                }

            }
            _consumer.endDataSet();
        } catch (IOException e) {
            throw new DataSetException("error getting list of tables", e);
        }
    }

    private void produceFromFile(Path theDataFile) throws DataSetException, CsvParserException {
        logger.debug("produceFromFile(theDataFile={}) - start", theDataFile);

        try {
            CsvParserImpl parser = new CsvParserImpl();
            List<?> readData;
            try (BufferedReader br = Files.newBufferedReader(theDataFile, _charset)) {
                readData = parser.parse(br, theDataFile.toString());
            }
            List<?> readColumns = ((List<?>) readData.get(0));
            Column[] columns = new Column[readColumns.size()];

            for (int i = 0; i < readColumns.size(); i++) {
                String columnName = (String) readColumns.get(i);
                columnName = columnName.trim();
                columns[i] = new Column(columnName, DataType.UNKNOWN);
            }

            String theDataFileName = theDataFile.getFileName().toString();
            String tableName = theDataFileName.substring(0, theDataFileName.indexOf(".csv"));
            ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
            _consumer.startTable(metaData);
            for (int i = 1; i < readData.size(); i++) {
                List<?> rowList = (List<?>) readData.get(i);
                Object[] row = rowList.toArray();
                for (int col = 0; col < row.length; col++) {
                    row[col] = row[col].equals(CsvDataSetWriter.NULL) ? null : row[col];
                }
                _consumer.row(row);
            }
            _consumer.endTable();
        } catch (PipelineException e) {
            throw new DataSetException(e);
        } catch (IllegalInputCharacterException e) {
            throw new DataSetException(e);
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }

    /**
     * Get a list of tables that this producer will create
     * @return a list of Strings, where each item is a CSV file relative to the base URL
     * @throws IOException when IO on the base URL has issues.
     */
    public static List<String> getTables(Path base, String tableList, Charset charset) throws IOException {
        logger.debug("getTables(base={}, tableList={}) - start", base, tableList);

        List<String> orderedNames = new ArrayList<String>();
        try (BufferedReader reader = Files.newBufferedReader(base.resolve(tableList), charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String table = line.trim();
                if (table.length() > 0) {
                    orderedNames.add(table);
                }
            }
        }
        return orderedNames;
    }

}
