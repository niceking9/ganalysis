package cn.yobir.analysis.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.yobir.analysis.service.Service;
import cn.yobir.analysis.serviceimp.Serviceimp;

@SuppressWarnings("serial")
public class MakeChartServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	     Service  service=new Serviceimp();
	      service.MakeChart(100);
	}

}
