package com.appspot.getthatpage;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

import entities.ClonedWebSite;
import entities.ClonedWebSiteController;

@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String url = req.getParameter("url");
		String host = req.getParameter("host");
		
		if(url == null || host == null) {
			System.out.println("GET IMAGE ERROR: parameters to get the image are not valid!");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String urlValidate = url.toString();
		if(urlValidate.charAt(0) != '/')
			urlValidate = "/" + urlValidate;
		
		if(!urlValidate.toLowerCase().startsWith("http")){
			urlValidate = "http://" + host + urlValidate;
		}
		
		if(!Utils.isValidUrl(urlValidate)){
			System.out.println("GET IMAGE ERROR: image url not valid: " + urlValidate);
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		ClonedWebSite webSite = ClonedWebSiteController.getClonedWebSite(host);
		
		if(webSite == null) {
			System.out.println("GET IMAGE ERROR: Cannot find webSite: " + host);
			System.out.println("GET IMAGE ERROR image: " + url);
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		Blob img = webSite.getImageBlob(url);
		
		if(img == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			System.out.println("GET IMAGE ERROR: Cannot find image blob " + url);
			return;
		}
		
		byte[] imgData = img.getBytes();
		
		if(url.toLowerCase().endsWith(".png"))
			resp.setContentType("image/png");
		else
			resp.setContentType("image/jpeg");
		
		resp.setContentLength(imgData.length);
		
		OutputStream out = resp.getOutputStream();
		out.write(imgData);
		out.close();
	}
	
}