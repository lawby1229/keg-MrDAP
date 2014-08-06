package edu.thu.keg.mrdap.rest.dataset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.json.JSONWithPadding;

import edu.thu.keg.mrdap.rest.classes.JDataset;
import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.Platform;
import edu.thu.keg.mrdap.dataset.Dataset;

@Path("/dsg")
public class DsGetFunctions {
	/**
	 * 
	 */
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	@Context
	ServletContext servletcontext;
	@Context
	HttpServletRequest httpServletRequest;
	@Context
	HttpServletResponse httpServletResponse;

	HttpSession session = null;
	private static Logger log = Logger.getLogger(DsGetFunctions.class
			.getSimpleName());

	// rest/dsg/sql?db=?&user=?&password=?&sql=?

	/**
	 * get all dataset names list
	 * 
	 * @return a list including dataset names
	 */
	@GET
	@Path("/getdss")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getDatasetsNames(
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		List<JDataset> jdatasets = new ArrayList<JDataset>();

		System.out.println(session.getId());
		Platform p = (Platform) servletcontext.getAttribute("platform");
		DatasetManager datasetManager = p.getDatasetManager();
		Collection<Dataset> datasets = datasetManager.getDatasetList();
		for (Dataset dataset : datasets) {
			JDataset dname = new JDataset(dataset);

			jdatasets.add(dname);
		}
		return new JSONWithPadding(
				new GenericEntity<List<JDataset>>(jdatasets) {
				}, jsoncallback);
	}

	@GET
	@Path("/getalldss")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getAllDatasets(
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();

		System.out.println(session.getId());
		Platform p = (Platform) servletcontext.getAttribute("platform");
		DatasetManager datasetManager = p.getDatasetManager();
		Collection<Dataset> datasets = datasetManager.getDatasetList();
		JSONObject JsonBack = new JSONObject();
		try {
			for (Dataset dataset : datasets) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dataset.getDate());
				String year = String.valueOf(calendar.get(Calendar.YEAR));
				String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
				String day = String
						.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

				if (!JsonBack.has(dataset.getType()))
					JsonBack.put(dataset.getType(), new JSONObject());
				JSONObject jserial = (JSONObject) JsonBack.get(dataset
						.getType());
				if (!jserial.has(dataset.getSerial()))
					jserial.put(dataset.getSerial(), new JSONObject());
				JSONObject jyear = (JSONObject) jserial
						.get(dataset.getSerial());
				if (!jyear.has(year))
					jyear.put(year, new JSONObject());
				JSONObject jmonth = (JSONObject) jyear.get(year);
				if (!jmonth.has(month))
					jmonth.put(month, new JSONObject());
				JSONObject jday = (JSONObject) jmonth.get(month);
				if (!jday.has(day))
					jday.put(day, new JSONArray());
				JSONArray jdatasetArray = (JSONArray) jday.get(day);
				JSONObject jobj = new JSONObject();
				jobj.put("id", dataset.getId());
				jobj.put("name", dataset.getName());
				jobj.put("size", dataset.getSizeMb());
				jdatasetArray.put(jobj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONWithPadding(new GenericEntity<String>(
				JsonBack.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/hello")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getString() {
		String a = "{\"city\":\"helloworld_json\"}";
		System.out.println(a);
		return a;
	}

	@GET
	@Path("/hello")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getString2() {
		System.out.println("{helloworld_json}");
		return "{helloworld_json}";
	}

	@GET
	@Path("/jsonp")
	@Produces("application/x-javascript")
	@Consumes({ MediaType.APPLICATION_JSON })
	public JSONWithPadding readAllP(
			@QueryParam("jsoncallback") String jsoncallback,
			@QueryParam("acronym") String acronym,
			@QueryParam("title") String title,
			@QueryParam("competition") String competition) {
		String a = "{\"city\":\"Beijing\",\"street\":\" Chaoyang Road \",\"postcode\":100025}";
		System.out.println(a);
		return new JSONWithPadding(a, jsoncallback);
	}
}
