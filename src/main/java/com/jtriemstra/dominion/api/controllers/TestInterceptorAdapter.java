package com.jtriemstra.dominion.api.controllers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestInterceptorAdapter extends HandlerInterceptorAdapter {
	@Override
	public void postHandle(
	  HttpServletRequest request, 
	  HttpServletResponse response,
	  Object handler, 
	  ModelAndView modelAndView) throws Exception {
		
		try {
			System.out.println("TestInterceptorAdapter");
			System.out.println(request.getServletPath());
			System.out.println(request.getParameter("playerName"));
			System.out.println(handler != null);
			
			if (handler != null) {
				System.out.println(handler.toString());
			}
			
			
			
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final ByteArrayPrintWriter pw = new ByteArrayPrintWriter(baos);

			HttpServletResponse wrappedResp = new HttpServletResponseWrapper((HttpServletResponse) response) {
			    @Override
			    public PrintWriter getWriter() {
			        return pw;
			    }

			    @Override
			    public ServletOutputStream getOutputStream() {
			        return new ServletOutputStream() {
			            @Override
			            public void write(int b) {
			                baos.write(b);
			            }

						@Override
						public boolean isReady() {
							// TODO Auto-generated method stub
							return false;
						}

						@Override
						public void setWriteListener(WriteListener listener) {
							// TODO Auto-generated method stub
							
						}
			        };
			    }
			};

			byte[] bytes = baos.toByteArray();
			 String responseStr = new String(bytes);
			 response.getOutputStream().write(bytes);
			
			System.out.println(responseStr);	
		}
		catch(Exception e) {
			log.error("Error in interceptor", e);
		}
	}
	
	public static class ByteArrayPrintWriter extends PrintWriter {
		 public ByteArrayPrintWriter(OutputStream out) {
		    super(out);
		 }
		 }
}
