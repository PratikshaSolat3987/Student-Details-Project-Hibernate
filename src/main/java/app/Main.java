package app;

import controller.*;

import util.HibernateUtil;
import org.apache.catalina.Context;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        HibernateUtil.getSessionFactory();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8088);

        String projectRoot = new File(".").getCanonicalPath();
        String docBase = new File(projectRoot, "src/main/webapp").getAbsolutePath();
        Context context = tomcat.addContext("", docBase);

        Tomcat.addServlet(context, "default", new DefaultServlet());
        context.addServletMappingDecoded("/*", "default");

        tomcat.addServlet("", "StudentServlet", new StudentServlet());
        tomcat.addServlet("", "loginServlet", new loginServlet());
        tomcat.addServlet("", "SignUpServlet", new SignUpServlet());

        context.addServletMappingDecoded("/students/*", "StudentServlet");
        context.addServletMappingDecoded("/login", "loginServlet");
        context.addServletMappingDecoded("/signup", "SignUpServlet");

        tomcat.getConnector();
        System.out.println("Starting Tomcat...");
        tomcat.start();
        System.out.println("Tomcat started on http://localhost:8088");
        tomcat.getServer().await();
    }
}

