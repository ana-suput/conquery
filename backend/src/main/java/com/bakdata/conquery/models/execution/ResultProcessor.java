package com.bakdata.conquery.models.execution;

import static com.bakdata.conquery.io.result.arrow.ArrowRenderer.renderToStream;
import static com.bakdata.conquery.models.auth.AuthorizationHelper.authorize;
import static com.bakdata.conquery.models.auth.AuthorizationHelper.authorizeDownloadDatasets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.function.Function;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.bakdata.conquery.io.result.ResultUtil;
import com.bakdata.conquery.models.auth.entities.User;
import com.bakdata.conquery.models.auth.permissions.Ability;
import com.bakdata.conquery.models.config.ConqueryConfig;
import com.bakdata.conquery.models.i18n.I18n;
import com.bakdata.conquery.models.identifiable.ids.specific.DatasetId;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedExecutionId;
import com.bakdata.conquery.models.identifiable.mapping.IdMappingConfig;
import com.bakdata.conquery.models.identifiable.mapping.IdMappingState;
import com.bakdata.conquery.models.query.ManagedQuery;
import com.bakdata.conquery.models.query.PrintSettings;
import com.bakdata.conquery.models.worker.DatasetRegistry;
import com.bakdata.conquery.util.io.ConqueryMDC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.dictionary.DictionaryProvider;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;
import org.apache.arrow.vector.ipc.ArrowWriter;

/**
 * Holder for utility methods to obtain an result from an execution.
 * Acts as a bridge between HTTP-requests and {@link ManagedExecution}s.
 */
@Slf4j
@RequiredArgsConstructor
public class ResultProcessor {
	
	private final DatasetRegistry datasetRegistry;
	private final ConqueryConfig config;
	
	public ResponseBuilder getResult(User user, DatasetId datasetId, ManagedExecutionId queryId, String userAgent, String queryCharset, boolean pretty) {
		ConqueryMDC.setLocation(user.getName());
		log.info("Downloading results for {} on dataset {}", queryId, datasetId);
		authorize(user, datasetId, Ability.READ);
		authorize(user, queryId, Ability.READ);

		ManagedExecution<?> exec = datasetRegistry.getMetaStorage().getExecution(queryId);
		
		// Check if user is permitted to download on all datasets that were referenced by the query
		authorizeDownloadDatasets(user, exec);

		IdMappingState mappingState = config.getIdMapping().initToExternal(user, exec);
		
		// Get the locale extracted by the LocaleFilter
		PrintSettings settings = new PrintSettings(pretty, I18n.LOCALE.get(), datasetRegistry);
		Charset charset = determineCharset(userAgent, queryCharset);

		try {
			StreamingOutput out = exec.getResult(
				cer -> ResultUtil.createId(datasetRegistry.get(datasetId), cer, config.getIdMapping(), mappingState),
				settings,
				charset,
				config.getCsv().getLineSeparator());

			return Response.ok(out);
		}
		catch (NoSuchElementException e) {
			throw new WebApplicationException(e, Status.NOT_FOUND);
		}
		finally {
			ConqueryMDC.clearLocation();
		}
	}
	
	public Response getArrowStreamResult(User user, ManagedExecutionId queryId, DatasetId datasetId, boolean pretty) {
		return getArrowResult(
			(output) -> (root) -> new ArrowStreamWriter(root, new DictionaryProvider.MapDictionaryProvider(), output),
			user,
			queryId,
			datasetId,
			datasetRegistry,
			pretty);
	}
	
	public Response getArrowFileResult(User user, ManagedExecutionId queryId, DatasetId datasetId, boolean pretty) {
		return getArrowResult(
			(output) -> (root) -> new ArrowFileWriter(root, new DictionaryProvider.MapDictionaryProvider(), Channels.newChannel(output)),
			user,
			queryId,
			datasetId,
			datasetRegistry,
			pretty);
	}
	
	
	private Response getArrowResult(
		Function<OutputStream, Function<VectorSchemaRoot,ArrowWriter>> writerProducer,
		User user,
		ManagedExecutionId queryId,
		DatasetId datasetId,
		DatasetRegistry datasetRegistry,
		boolean pretty) {
		
		ConqueryMDC.setLocation(user.getName());
		log.info("Downloading results for {} on dataset {}", queryId, datasetId);
		authorize(user, datasetId, Ability.READ);
		authorize(user, queryId, Ability.READ);

		ManagedExecution<?> exec = datasetRegistry.getMetaStorage().getExecution(queryId);
		
		// Check if user is permitted to download on all datasets that were referenced by the query
		authorizeDownloadDatasets(user, exec);
		
		if(!(exec instanceof ManagedQuery)) {
			return Response.notAcceptable(null).build();
		}
		ManagedQuery mquery = (ManagedQuery) exec;

		// Get the locale extracted by the LocaleFilter
		PrintSettings settings = new PrintSettings(pretty, I18n.LOCALE.get(), datasetRegistry);
		
		IdMappingConfig idMappingConf = config.getIdMapping();
		IdMappingState mappingState = config.getIdMapping().initToExternal(user, exec);
		
		StreamingOutput out = new StreamingOutput() {
			
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				renderToStream(writerProducer.apply(output),
					settings, 
					mquery, 
					cer -> ResultUtil.createId(datasetRegistry.get(datasetId), cer, config.getIdMapping(), mappingState).getExternalId(),
					idMappingConf.getPrintIdFields());
				
			}
		};
		return Response.ok(out).build();
	}

	/**
	 * Tries to determine the charset for the result encoding from different request properties.
	 * Defaults to StandardCharsets.UTF_8.
	 */
	private static Charset determineCharset(String userAgent, String queryCharset) {
		if(queryCharset != null) {
			try {
				return Charset.forName(queryCharset);				
			}catch (Exception e) {
				log.warn("Unable to map '{}' to a charset. Defaulting to UTF-8", queryCharset);
				return StandardCharsets.UTF_8;
			}
		}
		if(userAgent != null) {
			return userAgent.toLowerCase().contains("windows") ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;
		}
		return StandardCharsets.UTF_8;
	}
}
