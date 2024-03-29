package vc;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.Principal;
import java.util.Calendar;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;


/**
 * Servlet is responsible for adding the new post to the pool and give the user feedback about the action.
 * @author jander/andon
 */
public class Post extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Persist information (post the message)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		    boolean isAnon = true;
		    String name = "Anonymous";
			name = request.getParameter("user");
			String text = request.getParameter("post");
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Principal user = request.getUserPrincipal();
			if(text==null||text.trim().length()==0){
				response.sendRedirect("/");
				RequestDispatcher dispatcher = request.getRequestDispatcher("/");
				request.setAttribute("err", "post");
				dispatcher.forward(request, response);
				return;
			}
			
			if (user != null){
				name=user.getName();
				isAnon = false;
				Query q = new Query("User");
				q.addFilter("userName", Query.FilterOperator.EQUAL, name);
				q.setKeysOnly();
				PreparedQuery pq = datastore.prepare(q);
				if(pq.asList(FetchOptions.Builder.withLimit(1)).size()==0){
					Entity euser = new Entity("User",name);
					euser.setProperty("userName", name); 
					datastore.put(euser);
					Entity selfsub = new Entity("Subscription",name+"/"+name);
					selfsub.setProperty("userName", name);
					selfsub.setProperty("to", name);
				}
			}
			
			Entity message = new Entity("Message");
			message.setProperty("userName", name);
			message.setProperty("message", text);
			message.setProperty("date",Calendar.getInstance().getTime());
			message.setProperty("anon", isAnon);
			datastore.put(message);
			request.removeAttribute("err");
			response.sendRedirect("/");
			
			
	}

}
