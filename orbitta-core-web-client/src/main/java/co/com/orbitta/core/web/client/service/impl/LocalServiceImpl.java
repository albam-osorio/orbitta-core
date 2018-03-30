package co.com.orbitta.core.web.client.service.impl;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import co.com.orbitta.core.dto.IdentifiedDomainObject;
import co.com.orbitta.core.web.client.components.RestClient;
import co.com.orbitta.core.web.client.configuration.RestProperties;
import co.com.orbitta.core.web.client.service.api.LocalService;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LocalServiceImpl<M extends IdentifiedDomainObject<ID>, ID extends Serializable>
		implements LocalService<M, ID> {

	abstract protected RestProperties getProperties();

	abstract protected String getResourceName();

	abstract protected Class<M> getResponseType();

	abstract protected Class<M[]> getArrayReponseType();

	@Autowired
	private RestClient restClient;

	protected RestClient getRestClient() {
		return restClient;
	}

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// --
	// ------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public List<M> findAll() {
		val response = getRestClient().getAllQuery(getResourcePath(), "", getArrayReponseType());
		val result = asList(response.getBody());
		return result;
	}

	@Override
	public M get(ID id) {
		val response = getRestClient().get(getResourcePath(), getResponseType(), id);
		val result = response.getBody();
		return result;
	}

	@Override
	public List<M> getAllByIdIn(List<ID> ids) {
		val query = "?" + buildMatrixVariable("id", ids);
		val response = getRestClient().getAllQuery(getResourcePath(), query, getArrayReponseType());
		val result = asList(response.getBody());
		return result;
	}

	@Override
	public M create(M model) {
		val url = getResourcePath(getProperties().getBasePath(), getResourceName());
		val response = getRestClient().post(url, model, getResponseType());
		val result = response.getBody();
		return result;
	}

	@Override
	public M update(M model) {
		val url = getResourcePath(getProperties().getBasePath(), getResourceName());
		val response = getRestClient().put(url, model, getResponseType());
		val result = response.getBody();
		return result;
	}

	@Override
	public void delete(ID id, int version) {
		String url = getResourcePath() + "/{id}?version={version}";
		getRestClient().delete(url, id, version);
	}

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// --
	// ------------------------------------------------------------------------------------------------------------------------------------
	protected String getResourcePath() {
		val result = getResourcePath(getProperties().getBasePath(), getResourceName());
		return result;
	}

	private String getResourcePath(String basePath, String resourcePath) {

		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}

		if (resourcePath.startsWith("/")) {
			resourcePath = resourcePath.substring(1);
		}

		val result = basePath + resourcePath;
		return result;
	}

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// --
	// ------------------------------------------------------------------------------------------------------------------------------------
	public String encodeValue(String value) {
		String encoded = null;
		try {
			encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			log.error("Error encoding parameter {}", e.getMessage(), e);
		}
		return encoded;
	}

	public String decode(String value) {
		String decoded = null;
		try {
			decoded = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			log.error("Error encoding parameter {}", e.getMessage(), e);
		}
		return decoded;
	}

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// --
	// ------------------------------------------------------------------------------------------------------------------------------------
	protected String buildMatrixVariable(String key, List<?> values) {
		return buildMatrixVariable(key, values, ';');
	}

	protected String buildMatrixVariable(String key, List<?> values, char separator) {
		val sb = new StringBuilder();

		for (val e : values) {
			sb.append(key).append("=").append(e).append(separator);
		}

		val result = sb.toString();
		return result;
	}
}
