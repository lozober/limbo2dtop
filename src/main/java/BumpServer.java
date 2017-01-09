

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class BumpServer extends HttpServlet {
	
	
	private static ArrayList<User> showList;
	private static QueueControl controller;
	private static LinkedList<String> log;
	private static final String DIVIDER = "\n--------------------------------------------------------\n\n";
	private static final String ERROR = "WRONG REQUEST RECEIVED";
	
	public BumpServer() {}
	
	 @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	            throws ServletException, IOException {
		 	String answer = ""; String newAction;
		 	final String username = req.getParameter("user");
		 	final String pw = req.getParameter("pw");
		 	final String site = req.getParameter("site");
		 	final String action = (req.getParameter("action") == null)? "view" : req.getParameter("action");
		 	switch(action){
		 	case "add":
		 		if(username!=null && pw!=null){
		 			newAction ="Trying to ADD "+username+" ...";
		 			answer = answer.concat(newAction+"\n");
		 			answer = answer.concat(DIVIDER);
		 			answer = answer.concat(this.printList());
		 			answer = answer.concat(DIVIDER);
		 			answer = answer.concat(this.printLog());
		 			resp.getWriter().print(answer);
		 			this.writeLog(newAction);
		 			getController().addUser(new User(username, pw, Domain.parse(LimudNaim.domain)));
		 			this.updateList();
		 		}else{
		 			resp.getWriter().print(ERROR);
		 		}
		 		break;
		 	case "remove":
		 		if(username!=null && site!=null){
		 			newAction ="Trying to REMOVE "+username+" ...";
		 			answer = answer.concat(newAction+"\n");
		 			answer = answer.concat(DIVIDER);
		 			answer = answer.concat(this.printList());
		 			answer = answer.concat(DIVIDER);
		 			answer = answer.concat(this.printLog());
		 			resp.getWriter().print(answer);
		 			this.writeLog(newAction);
		 			getController().removeUser(username,LimudNaim.domain);
		 			this.updateList();
		 		}else{
		 			resp.getWriter().print(ERROR);
		 		}
		 		break;
		 	case "view":
		 		answer = answer.concat(DIVIDER);
	 			answer = answer.concat(this.printList());
	 			answer = answer.concat(DIVIDER);
	 			answer = answer.concat(this.printLog());
	 			resp.getWriter().print(answer);
	 			this.updateList();
		 		break;
		 	default: 
		 		resp.getWriter().print("TESTING\n");break;
		 	}
	    }

	    public static void main(String[] args) throws Exception{
	    	Thread thread = new Thread(getController());
			thread.start();
	        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
	        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	        context.setContextPath("/");
	        server.setHandler(context);
	        context.addServlet(new ServletHolder(new BumpServer()),"/*");
	        server.start();
	        server.join();   
	    }
	    
	    private static QueueControl getController() {
			if (controller == null) {controller = new QueueControl(getLog());}
			return controller;
		}
		
		private static ArrayList<User> getList() {
			if (showList == null) {showList = new ArrayList<User>();}
			return showList;
		}
		
		private static LinkedList<String> getLog() {
			if (log == null) {log = new LinkedList<String>();}
			return log;
		}

		private String printLog(){
			String answer = "";
			for(String line : getLog()){
				 answer = answer.concat(line+"\n");
			}
			return answer;
		}
		
		private String printList(){
			String answer = ""; String username;
			for(User user : getList()){
				username = (user.getName()==null)? user.getLogin() : user.getName();
				answer = answer.concat(username+"\t"+user.getSite().toString()+
						"\t"+Domain.formatTime(new Date(user.getTime()))+"\n");
			}
			return answer;
		}
		
		private void updateList(){
			ArrayList<User> list = getList();
				synchronized(list){
					list.clear();
					list.addAll(getController().getList());
				}
		}
		
		private void writeLog(String line){
			LinkedList<String> log = getLog();
				synchronized(log){
					if (log != null){
						log.addFirst(Domain.formatTime(new Date())+ " - " + line);
						if(log.size()>50){
							log.removeLast();
						}
					}
				}
		}

}
