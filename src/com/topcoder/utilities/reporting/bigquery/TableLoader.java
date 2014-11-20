/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.reporting.bigquery;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.Dataset;
import com.google.api.services.bigquery.model.DatasetReference;
import com.google.api.services.bigquery.model.ErrorProto;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobConfiguration;
import com.google.api.services.bigquery.model.JobConfigurationLoad;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.Table;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.topcoder.utilities.reporting.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * This utility interacts with the Google Big Query service. It provides methods to
 * - create new dataset on the specified project
 * - create new table in the specified dataset
 * - submit the upload job to upload json data to the specified table
 * </p>
 *
 * @author GreatKevin
 * @version 1.0 (Implement loader to load large JSON data into Google Big Query)
 */
public class TableLoader {

    /**
     * The property key to load the Google API service account id.
     */
    private static final String SERVICE_ACCOUNT_ID_PROPERTY_NAME = "service_account_id";

    /**
     * The private key to access the Google API service.
     */
    private static final String P12_PRIVATE_KEY_PROPERTY_NAME = "private_key_file";

    /**
     * The name of the default configuration file.
     */
    private static final String DEFAULT_CONFIGURATION_FILE = "bigquery/TableLoader.properties";

    /**
     * The application name used for Google Service Builder.
     */
    private static final String BIG_QUERY_APP_NAME = "TopCoder Big Query Data Loader";

    /**
     * The upload MIME type for the Big Query data upload.
     */
    private static final String UPLOAD_MIME_TYPE = "application/octet-stream";

    /**
     * The time interval (in milliseconds) for polling the Data Load job status.
     */
    private static final long JOB_POLL_INTERVAL_MILLISECONDS = 3000;

    /**
     * The write disposition of JobConfigurationLoad - Write truncate, if table exists, the BigQuery overwrites
     * the table data.
     */
    public static final String WRITE_TRUNCATE_MODE = "WRITE_TRUNCATE";

    /**
     * The write disposition of JobConfigurationLoad - Write empty, if table exists, the BigQuery return 'duplicate'
     * error if there is existing data in the job result. This one is default mode
     */
    public static final String WRITE_EMPTY_MODE = "WRITE_EMPTY";

    /**
     * The write disposition of JobConfigurationLoad - Write append, if table exists, the BigQuery appends the new data
     * to existing data.
     */
    public static final String WRITE_APPEND_MODE = "WRITE_APPEND";

    /**
     * The gson instance.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The upload source format.
     */
    public static final String JSON_UPLOAD_SOURCE_FORMAT = "NEWLINE_DELIMITED_JSON";

    /**
     * The default create disposition.
     */
    public static final String CREATE_IF_NEEDED = "CREATE_IF_NEEDED";

    /**
     * The logger.
     */
    private static Log log = LogFactory.getLog(TableLoader.class);

    /**
     * The service account id.
     */
    private final String serviceAccountId;

    /**
     * The private key location.
     */
    private final String privateKeyLocation;

    /**
     * The big query service.
     */
    private final Bigquery bigQueryService;


    /**
     * Creates the <code>TableLoader</code> from the specific configuration file. The configuration file
     * should be a existing properties file in the class path.
     *
     * @param configurationFile the configuration file.
     * @throws Exception if any error.
     */
    public TableLoader(String configurationFile) throws Exception {
        if (configurationFile == null || configurationFile.trim().length() == 0) {
            throw new IllegalArgumentException("Please specify the configuration file name for the Big Query Loader");
        }

        Properties config = new Properties();

        // Get file input stream from the classapth with the configuration file name
        InputStream configInput = this.getClass().getClassLoader().getResourceAsStream(configurationFile);

        if (configInput == null) {
            throw new FileNotFoundException("Configuration file '" + configurationFile
                    + "' not found in the classpath");
        }

        config.load(configInput);

        serviceAccountId = config.getProperty(SERVICE_ACCOUNT_ID_PROPERTY_NAME);
        privateKeyLocation = config.getProperty(P12_PRIVATE_KEY_PROPERTY_NAME);

        if (serviceAccountId == null && serviceAccountId.trim().length() == 0) {
            throw new Exception(SERVICE_ACCOUNT_ID_PROPERTY_NAME + " is not configured in " + configurationFile);
        }

        log.debug(String.format("The service account ID configured is: %s", serviceAccountId));

        if (privateKeyLocation == null && privateKeyLocation.trim().length() == 0) {
            throw new Exception(P12_PRIVATE_KEY_PROPERTY_NAME + " is not configured in " + configurationFile);
        }

        log.debug(String.format("The private key file path is: %s", privateKeyLocation));

        this.bigQueryService = buildService();

        if (this.bigQueryService == null || this.bigQueryService.jobs() == null) {
            throw new Exception("The Google Big Query service is not properly setup.");
        }
    }


    public TableLoader() throws Exception {
        // use the default configuration file name
        this(DEFAULT_CONFIGURATION_FILE);
    }


    /**
     * Builds the Google Big Query Service with the credential information.
     *
     * @return the built big query service.
     * @throws Exception if there is any error.
     */
    private Bigquery buildService() throws Exception {
        GoogleCredential.Builder credBuilder = new GoogleCredential.Builder();
        GsonFactory gsonFactory = new GsonFactory();
        HttpTransport httpTransport = new NetHttpTransport();

        credBuilder.setJsonFactory(gsonFactory);
        credBuilder.setTransport(httpTransport);
        credBuilder.setServiceAccountId(serviceAccountId);

        credBuilder.setServiceAccountPrivateKeyFromP12File(
                new File(this.getClass().getClassLoader().getResource(privateKeyLocation).toURI()));
        credBuilder.setServiceAccountScopes(Collections.singleton(BigqueryScopes.BIGQUERY));

        GoogleCredential credential = credBuilder.build();

        Bigquery.Builder serviceBuilder =
                new Bigquery.Builder(httpTransport,
                        gsonFactory,
                        credential);

        serviceBuilder.setApplicationName(BIG_QUERY_APP_NAME);
        return serviceBuilder.build();
    }


    /**
     * Creates the new dataset.
     *
     * @param dataset the dataset to create.
     * @return the created dataset.
     * @throws Exception if any error happens.
     */
    public Dataset createNewDataSet(Dataset dataset) throws Exception {

        log.info("Enter method createNewDataSet(Dataset dataset)");

        if (dataset == null) {
            throw new IllegalArgumentException("The dataset argument should not be null.");
        }
        if (dataset.getDatasetReference() == null) {
            throw new IllegalArgumentException("The dataset reference is empty in dataset");
        }
        if (dataset.getDatasetReference().getProjectId() == null
                || dataset.getDatasetReference().getProjectId().trim().length() == 0) {
            throw new IllegalArgumentException("The projectId of the dataset reference should be set");
        }
        if (dataset.getDatasetReference().getDatasetId() == null
                || dataset.getDatasetReference().getDatasetId().trim().length() == 0) {
            throw new IllegalArgumentException("The datasetId of the dataset reference should be set");
        }

        Dataset createdDataSet;

        try {

            try {
                // check if there is existing dataset under the same project
                createdDataSet = this.bigQueryService.datasets().get(dataset.getDatasetReference().getProjectId(),
                        dataset.getDatasetReference().getDatasetId()).execute();

                // if flow goes to here, means exception not raise, the dataset specified already exists
                log.debug(String.format("The dataset %s already exists in the project %s",
                        dataset.getDatasetReference().getDatasetId(), dataset.getDatasetReference().getProjectId()));

            } catch (GoogleJsonResponseException grs) {
                log.debug("Need to create new dataset");
                createdDataSet = this.bigQueryService.datasets().insert(dataset.getDatasetReference().getProjectId(),
                        dataset).execute();
            }

            log.debug("Created Data Set - Project ID:" + createdDataSet.getDatasetReference().getProjectId());
            log.debug("Created Data Set - Dataset ID:" + createdDataSet.getDatasetReference().getDatasetId());

        } catch (Exception ex) {
            log.error("Error when creating dataset using Big Query API", ex);
            throw ex;
        }

        log.info("Exit method createNewDataSet(Dataset dataset)");

        return createdDataSet;
    }


    /**
     * Creates a new dataset with the give project id and dataset id.
     *
     * @param projectId the project id.
     * @param dataSetId the dataset id.
     * @return the created dataset.
     * @throws Exception if any error occurs.
     */
    public Dataset createNewDataSet(String projectId, String dataSetId) throws Exception {
        log.info("Enter method createNewDataSet(String projectId, String dataSetId)");

        DatasetReference datasetRef = new DatasetReference();
        datasetRef.setProjectId(projectId);
        datasetRef.setDatasetId(dataSetId);

        Dataset outputDataset = new Dataset();
        outputDataset.setDatasetReference(datasetRef);

        Dataset createdDataset = createNewDataSet(outputDataset);

        log.info("Exit method createNewDataSet(String projectId, String dataSetId)");

        return createdDataset;
    }


    /**
     * Creates a new table.
     *
     * @param table the <code>Table</code> to create
     * @return the created table.
     * @throws Exception if any error occurs.
     */
    public Table createNewTable(Table table) throws Exception {
        log.info("Enter method createNewTable(Table table)");

        if (table == null) {
            throw new IllegalArgumentException("The table argument should not be null.");
        }

        // check the projectID, datasetID and tableID in the table reference
        TableReference tableRef = table.getTableReference();
        validateTableReference(tableRef);

        if (table.getSchema() == null) {
            log.info(String.format("The table schema in the table %s is empty", tableRef.getTableId()));
        } else {
            log.info(String.format("The table schema is %s", GSON.toJson(table.getSchema())));
        }

        Table createdTable;

        try {
            try {
                createdTable = this.bigQueryService.tables().get(tableRef.getProjectId(),
                        tableRef.getDatasetId(),
                        tableRef.getTableId()).execute();

                // if flow goes here, means the table already exists
                log.debug(String.format("The table %s already exists in dataset %s, project %s",
                        tableRef.getTableId(), tableRef.getDatasetId(), tableRef.getProjectId()));

            } catch (GoogleJsonResponseException grs) {
                // not exist, create new one
                log.debug("Need to create new table");
                createdTable = this.bigQueryService.tables().insert(tableRef.getProjectId(),
                        tableRef.getDatasetId(), table).execute();
            }

            log.debug("Created Table - Project ID:" + createdTable.getTableReference().getProjectId());
            log.debug("Created Table - Dataset ID:" + createdTable.getTableReference().getDatasetId());
            log.debug("Created Table - Table ID:" + createdTable.getTableReference().getTableId());

        } catch (Exception ex) {
            log.error("Error when creating table using Big Query API", ex);
            throw ex;
        }

        log.info("Exit method createNewTable(Table table)");

        return createdTable;
    }

    /**
     * Creates the table from the given projectId, datasetId, tableId and table schema json file.
     *
     * @param projectId          the project id.
     * @param dataSetId          the dataset id.
     * @param tableId            the table id.
     * @param jsonSchemaFileName the file name of the json schema
     * @return the created table.
     * @throws Exception if any error occurs.
     */
    public Table createNewTable(String projectId, String dataSetId, String tableId, String jsonSchemaFileName) throws Exception {
        log.info(
                "Enter method createNewTable(String projectId, String dataSetId, String tableId, String jsonSchemaFileName)");

        Table table = new Table();
        TableReference tableRef = new TableReference();
        tableRef.setProjectId(projectId);
        tableRef.setDatasetId(dataSetId);
        tableRef.setTableId(tableId);
        table.setTableReference(tableRef);

        if (jsonSchemaFileName != null && jsonSchemaFileName.trim().length() > 0) {
            // load the json table schema
            Gson gson = new Gson();
            TableSchema tableSchema = gson.fromJson(
                    new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(jsonSchemaFileName)),
                    TableSchema.class);
            table.setSchema(tableSchema);
        }

        Table createdTable = createNewTable(table);

        log.info(
                "Exit method createNewTable(String projectId, String dataSetId, String tableId, String jsonSchemaFileName)");

        return createdTable;
    }


    /**
     * Creates the new empty table without any schema.
     *
     * @param projectId the project id.
     * @param dataSetId the dataset id.
     * @param tableId   the table id.
     * @return the created empty table.
     * @throws Exception if any error occurs.
     */
    public Table createNewTable(String projectId, String dataSetId, String tableId) throws Exception {
        log.info("Enter method createNewTable(String projectId, String dataSetId, String tableId)");

        Table createdTable = createNewTable(projectId, dataSetId, tableId, null);

        log.info("Exit method createNewTable(String projectId, String dataSetId, String tableId)");

        return createdTable;
    }


    /**
     * Submits the load data BigQuery job with the given data.
     *
     * @param reference            the table reference, should not be null
     * @param tableSchema          the table schema, can be null
     * @param dataFileName         the name of the data file, should not be null.
     * @param writeDispositionMode the write disposition mode, one value of {@link #WRITE_APPEND_MODE}
     *                             {@link #WRITE_EMPTY_MODE} and {@link #WRITE_TRUNCATE_MODE}
     * @param needCompress         whether the data file needs to compress to gzip if it's not
     * @return the job submitted.
     * @throws Exception if any error.
     */
    public Job submitLoadJob(TableReference reference, TableSchema tableSchema,
                             String dataFileName, String writeDispositionMode, boolean needCompress) throws Exception {
        validateTableReference(reference);

        JobConfigurationLoad jobLoad = new JobConfigurationLoad();

        if (tableSchema != null) {
            jobLoad.setSchema(tableSchema);
        }

        jobLoad.setSourceFormat(JSON_UPLOAD_SOURCE_FORMAT);
        jobLoad.setDestinationTable(reference);

        // default to create disposition "CREATE_IF_NEEDED"
        jobLoad.setCreateDisposition(CREATE_IF_NEEDED);

        jobLoad.setWriteDisposition(writeDispositionMode);

        JobConfiguration jobConfig = new JobConfiguration();
        jobConfig.setLoad(jobLoad);

        JobReference jobRef = new JobReference();
        jobRef.setProjectId(reference.getProjectId());

        Job uploadJob = new Job();
        uploadJob.setConfiguration(jobConfig);
        uploadJob.setJobReference(jobRef);

        String inputFileURIString = this.getClass().getClassLoader().getResource(dataFileName).toURI().toString();

        String extension = FilenameUtils.getExtension(inputFileURIString);

        if (!(extension.equalsIgnoreCase("json") || extension.equalsIgnoreCase("gz"))) {
            throw new IllegalArgumentException(
                    String.format("The data file %s should be in json or gz format", dataFileName));
        }

        String uploadFileURIString = inputFileURIString;

        if (extension.equalsIgnoreCase("json") && needCompress) {
            // the data file is json and the needCompress flag is true
            String compressedFileURIString =
                    FilenameUtils.getPath(inputFileURIString) + FilenameUtils.getBaseName(inputFileURIString) + ".gz";

            // compress the json file into gz format automatically
            Utils.compressGzipFile(new File(new URI(inputFileURIString)),
                    new File(new URI(compressedFileURIString)));

            uploadFileURIString = compressedFileURIString;
        }

        File fileToUpload = new File(new URI(uploadFileURIString));


        log.info(String.format("The file : %s to upload has size %s", fileToUpload.getName(), FileUtils.byteCountToDisplaySize(
                FileUtils.sizeOf(fileToUpload))));


        FileContent contents = new FileContent(UPLOAD_MIME_TYPE, fileToUpload);

        long count = System.currentTimeMillis();

        Job job = this.bigQueryService.jobs().insert(reference.getProjectId(),
                uploadJob,
                contents).execute();

        log.info(String.format("The job creation and upload data file took %d seconds", (System.currentTimeMillis() - count) / 1000));

        if (job == null) {
            throw new Exception("The load Job is null, it's failed to submit the job via BigQuery service");
        }

        return job;
    }

    /**
     * Submits the load data BigQuery job.
     *
     * @param reference    the table reference.
     * @param tableSchema  the table schema.
     * @param dataFileName the upload data file name.
     * @return the submitted job.
     * @throws Exception if any error occurs.
     */
    public Job submitLoadJob(TableReference reference, TableSchema tableSchema, String dataFileName) throws Exception {
        return submitLoadJob(reference, tableSchema, dataFileName, WRITE_TRUNCATE_MODE, true);
    }

    /**
     * Submits the load data BigQuery job.
     *
     * @param projectId    the project id.
     * @param dataSetId    the dataset id.
     * @param tableId      the table id.
     * @param dataFileName the data file name.
     * @return the submitted job.
     * @throws Exception if any error occurs.
     */
    public Job submitLoadJob(String projectId, String dataSetId, String tableId, String dataFileName) throws Exception {
        TableReference tableReference = new TableReference();
        tableReference.setProjectId(projectId);
        tableReference.setDatasetId(dataSetId);
        tableReference.setTableId(tableId);

        return submitLoadJob(tableReference, null, dataFileName);
    }


    /**
     * Traces the submitted job status. It polls the server for job status in the given pollInternal (in milliseconds)
     *
     * @param job          the job to check status
     * @param pollInterval the poll interval (in milliseconds)
     * @throws Exception if any error.
     */
    public void traceJobStatus(Job job, long pollInterval) throws Exception {
        String jobId = job.getJobReference().getJobId();

        long startTime = System.currentTimeMillis();

        while (!job.getStatus().getState().equals("DONE")) {
            // Pause execution for ten seconds before polling job status again
            Thread.sleep(pollInterval);

            long elapsedTime = System.currentTimeMillis() - startTime;
            String jobStatusMessage = String.format("Job status (%dms) %s: %s\n", elapsedTime,
                    jobId, job.getStatus().getState());
            log.info(jobStatusMessage);

            // Poll the server for job completion state.
            job = this.bigQueryService.jobs().get(job.getJobReference().getProjectId(), jobId).execute();
        }

        if (job.getStatus().getErrorResult() != null) {
            // The job ended with an error.
            String errorResultMessage = String.format("Job %s ended with error %s\n", job.getJobReference().getJobId(),
                    job.getStatus().getErrorResult().getMessage());

            log.error(errorResultMessage);

            List<ErrorProto> errors = job.getStatus().getErrors();

            if (errors != null) {
                for (ErrorProto e : errors) {
                    log.error(e.toString());
                }
            }
        } else {
            log.info("The job is done, detailed job information:\n" + job.toPrettyString());
        }
    }

    /**
     * Traces the submitted job status. It polls the server for job status in the default pollInternal (3s)
     *
     * @param job the job to check status
     * @throws Exception if any error.
     */
    public void traceJobStatus(Job job) throws Exception {
        traceJobStatus(job, JOB_POLL_INTERVAL_MILLISECONDS);
    }

    /**
     * Helper method to validate the <code>TableReference</code>
     *
     * @param tableReference the table reference to check.
     * @throws IllegalArgumentException if any of projectId, datasetId and tableId is null.
     */
    private static void validateTableReference(TableReference tableReference) throws IllegalArgumentException {
        if (tableReference == null) {
            throw new IllegalArgumentException("The table reference is empty in table");
        }
        if (tableReference.getProjectId() == null
                || tableReference.getProjectId().trim().length() == 0) {
            throw new IllegalArgumentException("The projectId of the table reference should be set");
        }
        if (tableReference.getDatasetId() == null
                || tableReference.getDatasetId().trim().length() == 0) {
            throw new IllegalArgumentException("The datasetId of the table reference should be set");
        }
        if (tableReference.getTableId() == null
                || tableReference.getTableId().trim().length() == 0) {
            throw new IllegalArgumentException("The tableId of the table reference should be set");
        }
    }



}
