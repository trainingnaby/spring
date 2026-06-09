package com.formation.config;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.ServletContext;

public class AppMainWithTomcat {

	public static void main(String[] args) throws LifecycleException {

		// création d'une instance de tomcat
		Tomcat tomcat = new Tomcat();
		// port d'écoute
		tomcat.setPort(8080);
		// initialise le moteur tomcat
		tomcat.getConnector();

		// contexte path de notre application
		Context contexteTomcat = tomcat.addContext("", null);

		// création de l'application context de type web (voir méthode plus bas)
		WebApplicationContext appCtx = creationApplicationContext(contexteTomcat.getServletContext());

		// Initialisation du servlet frontal DispatcherServlet, a besoin de connaitre le
		// contexte web donc des controlleurs existants
		DispatcherServlet dispatcherServlet = new DispatcherServlet(appCtx);

		// Enregistrement de la servlet sur tomcat
		Wrapper servlet = Tomcat.addServlet(contexteTomcat, "dispatcherServlet", dispatcherServlet);
		servlet.setLoadOnStartup(1);
		servlet.addMapping("/*");

		tomcat.start();
	}

	/**
	 * Création de l'application contexte
	 * 
	 * @param servletContext
	 * @return
	 */
	public static WebApplicationContext creationApplicationContext(ServletContext servletContext) {

		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(AppConfig.class); // classe de configuration
		ctx.setServletContext(servletContext);
		ctx.refresh();
		ctx.registerShutdownHook();
		return ctx;
	}
}
