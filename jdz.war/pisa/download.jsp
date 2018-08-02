<%@page language="java" import="java.net.*" pageEncoding="gb2312"%>
<%@page import="java.io.*"%>
<%@page import="com.cib.pisa.util.*"%>
<%
	response.setContentType("application/x-download"); //设置为下载application/x-download

	String date = request.getParameter("date");
	String freq = request.getParameter("freq");
	date = DateUtil.getSubNo(DateUtil.parseDate(date, "yyyy-MM-dd"), freq);
	
	String filedir = ResourceUtil.getProperty("file_path");
	String filenamedisplay = date + freq + "_pisasdata.zip"; //下载文件时显示的文件保存名称
	filenamedisplay = URLEncoder.encode(filenamedisplay, "UTF-8");
	response.addHeader("Content-Disposition", "attachment;filename=" + filenamedisplay);
	
	File file = new File(filedir + File.separator + date + File.separator + date + freq + "_pisasdata.zip");
	
	OutputStream outputStream = response.getOutputStream();
	InputStream inputStream = new FileInputStream(file);
	byte[] buffer = new byte[1024];
	int i = -1;
	while ((i = inputStream.read(buffer)) != -1) {
	        outputStream.write(buffer, 0, i);
	}
	outputStream.flush();
	outputStream.close();
	inputStream.close();
	//outputStream = null;
	
	out.clear();
	out = pageContext.pushBody();

%>
