package edu.thu.keg.mrdap.rest.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import com.sun.jersey.api.json.JSONWithPadding;

import edu.thu.keg.link.hdfs.MFile;
import edu.thu.keg.link.taskTypeQuery.TaskTypeField;
import edu.thu.keg.link.taskTypeQuery.TaskTypeQuery;
import edu.thu.keg.link.taskTypeQuery.TaskTypeQuery.TaskType;
import edu.thu.keg.mrdap.rest.classes.JTask;
import edu.thu.keg.mrdap.rest.classes.JTaskType;
import edu.thu.keg.mrdap.task.Task;
import edu.thu.keg.mrdap.task.impl.TaskStatus;
import edu.thu.keg.mrdap.Platform;
import edu.thu.keg.mrdap.TaskManager;

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

	@GET
	@Path("/dl")
	// @Produces({ MediaType.TEXT_PLAIN })
	public Response downloadFile(@QueryParam("id") String id) {
		Platform p = (Platform) servletcontext.getAttribute("platform");
		TaskManager taskManager = p.getTaskManager();

		try {
			Task task = taskManager.getTask(id);
			if (task == null)
				return Response.status(Status.NOT_FOUND).build();
			MFile mf = new MFile(task.getOutputPath() + "/part-00000");

			System.out.println("文件下载中：" + task.getOutputPath());
			System.out.println("mf：" + mf.toString());
			if (!mf.exists()) {
				// httpServletResponse.setStatus(Status.NOT_FOUND.getStatusCode());
				return Response.status(Status.NOT_FOUND).build();
			}
			httpServletResponse.setHeader("content-type", MediaType.TEXT_PLAIN);
			httpServletResponse.addHeader("content-disposition",
					"attachment;filename=" + task.getId() + ".csv");
			InputStream is;
			is = mf.open();
			TaskType ty = TaskTypeQuery.getTaskTypeById(task.getTypeId());
			String outputFileds = ty.getValue(TaskTypeField.OUTPUT_META);
			String fileds = outputFileds.split(":")[1].trim() + "\n";
			int read = 0;
			byte[] bytes = new byte[1024];
			OutputStream os = httpServletResponse.getOutputStream();
			os.write(fileds.getBytes());
			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			os.flush();
			os.close();
			is.close();
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.OK).build();
		// return Response.status(Status.OK).build();
		// return Response.created(uriInfo.getAbsolutePath()).build();
	}

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
			taskManager.getTaskInfo(task.getId());
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
		taskManager.getTaskInfo(task.getId());
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
	public JSONWithPadding setAndRunTask(@QueryParam("typeId") String typeId,
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
		JSONObject job = new JSONObject();
		try {
			jdatasets = new JSONArray(datasets);

			for (int i = 0; i < jdatasets.length(); i++) {

				allPaths.add(jdatasets.getString(i));
			}

			Task task = taskManager.setTask(typeId, TaskTypeQuery
					.getTaskTypeById(typeId).getValue(TaskTypeField.NAME),
					name, "admin", allPaths);
			String status = taskManager.runTask(task);

			job.put("status", status);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
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
		List<JTaskType> jtypes = new ArrayList<JTaskType>();
		try {
			for (TaskType ts : TaskTypeQuery.getTaskTypeList()) {
				jtypes.add(new JTaskType(ts));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONWithPadding(new GenericEntity<List<JTaskType>>(jtypes) {
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
		JSONObject job = new JSONObject();
		try {
			job.put("id", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/rmts")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding removeTask(@QueryParam("id") String id,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		System.out.println(session.getId());
		Platform p = (Platform) servletcontext.getAttribute("platform");
		TaskManager taskManager = p.getTaskManager();
		taskManager.removeTask(id);
		JSONObject job = new JSONObject();
		try {
			job.put("id", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/tsstatus")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	public JSONWithPadding getTaskState(@QueryParam("id") String id,
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		log.info(uriInfo.getAbsolutePath());
		session = httpServletRequest.getSession();
		System.out.println(session.getId() + "1");
		Platform p = (Platform) servletcontext.getAttribute("platform");
		System.out.println(session.getId() + "2");
		TaskManager taskManager = p.getTaskManager();
		System.out.println(session.getId() + "3");
		TaskStatus ts = taskManager.getTaskInfo(id);
		JSONObject job = new JSONObject();
		try {
			job.put("taskstatus", ts.name());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
	}

	@GET
	@Path("/refresh")
	@Produces({ "application/javascript", MediaType.APPLICATION_JSON })
	// @Produces({ MediaType })
	public JSONWithPadding refresh(
			@QueryParam("jsoncallback") @DefaultValue("fn") String jsoncallback) {
		String re = TaskTypeQuery.update();
		JSONObject job = new JSONObject();
		try {
			job.put("status", re);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONWithPadding(new GenericEntity<String>(job.toString()) {
		}, jsoncallback);
		// return Response.status(Status.OK).build();
		// return Response.created(uriInfo.getAbsolutePath()).build();
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
