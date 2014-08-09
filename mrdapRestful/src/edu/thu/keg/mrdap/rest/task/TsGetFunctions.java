package edu.thu.keg.mrdap.rest.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.mortbay.util.ajax.JSON;

import com.sun.jersey.api.json.JSONWithPadding;

import edu.thu.keg.mrdap.rest.classes.JTask;
import edu.thu.keg.mrdap.task.Task;
import edu.thu.keg.mrdap.task.impl.TaskStatus;
import edu.thu.keg.mrdap.task.impl.TaskType;
import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.Platform;
import edu.thu.keg.mrdap.TaskManager;
import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.impl.Config;

@Path("/tsg")
public class TsGetFunctions {
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
	private static Logger log = Logger.getLogger(TsGetFunctions.class
			.getSimpleName());

	// rest/dsg/sql?db=?&user=?&password=?&sql=?

	/**
	 * get all tasks names list
	 * 
	 * @return a list including dataset names
	 */
	@GET
	@Path("/gettss")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getHistoryTasks(
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		System.out.println(session.getId());
		Platform p = (Platform) servletcontext.getAttribute("platform");
		TaskManager taskManager = p.getTaskManager();
		Collection<Task> tasks = taskManager.getTaskList();
		List<JTask> jtasks = new ArrayList<JTask>();
		for (Task task : tasks) {

			JTask jtask = new JTask(task);
			jtasks.add(jtask);
		}
		return new JSONWithPadding(new GenericEntity<List<JTask>>(jtasks) {
		}, jsoncallback);
	}

	/**
	 * get the id task
	 * 
	 * @param id
	 * @param jsoncallback
	 * @return
	 */
	@GET
	@Path("/getts")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getTask(@QueryParam("id") String id,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		System.out.println(session.getId());
		Platform p = (Platform) servletcontext.getAttribute("platform");
		TaskManager taskManager = p.getTaskManager();
		Task task = taskManager.getTask(id);

		JTask jtask = new JTask(task);

		return new JSONWithPadding(new GenericEntity<JTask>(jtask) {
		}, jsoncallback);
	}

	/**
	 * get the id task
	 * 
	 * @param id
	 * @param jsoncallback
	 * @return
	 */
	@GET
	@Path("/runtask")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding setAndRunTask(@QueryParam("type") String type,
			@QueryParam("name") String name,
			@QueryParam("datasets") String datasets,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		System.out.println(session.getId());
		Platform p = (Platform) servletcontext.getAttribute("platform");
		TaskManager taskManager = p.getTaskManager();
		List<String> allPaths = new ArrayList<String>();
		JSONArray jdatasets;
		try {
			jdatasets = new JSONArray(datasets);

			for (int i = 0; i < jdatasets.length(); i++) {

				allPaths.add(Config.getHadoopRoot() + jdatasets.getString(i));

			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Task task = taskManager.setTask(TaskType.valueOf(type), name, "admin",
				allPaths);
		String id = taskManager.runTask(task);

		JTask jtask = new JTask(task);
		return new JSONWithPadding(new GenericEntity<JTask>(jtask) {
		}, jsoncallback);
	}

	@GET
	@Path("/tstypes")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getTaskTypes(
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		System.out.println(session.getId());
		List<String> types = new ArrayList<String>();
		for (TaskStatus ts : TaskStatus.values()) {
			types.add(ts.name());
		}
		System.out.println(types);
		JSONArray ja = new JSONArray(types);
		return new JSONWithPadding(new GenericEntity<String>(ja.toString()) {
		}, jsoncallback);
	}

	/**
	 * get the id task
	 * 
	 * @param id
	 * @param jsoncallback
	 * @return
	 */
	@GET
	@Path("/killts")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding killTask(@QueryParam("id") String id,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		System.out.println(session.getId());
		Platform p = (Platform) servletcontext.getAttribute("platform");
		TaskManager taskManager = p.getTaskManager();
		taskManager.killTask(id);
		return new JSONWithPadding(new GenericEntity<Object>(null) {
		}, jsoncallback);
	}

	@GET
	@Path("/tsstate")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getTaskState(@QueryParam("id") String id,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		System.out.println(session.getId());
		Platform p = (Platform) servletcontext.getAttribute("platform");
		TaskManager taskManager = p.getTaskManager();
		TaskStatus ts = taskManager.getTaskInfo(id);
		return new JSONWithPadding(new GenericEntity<String>(ts.name()) {
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
