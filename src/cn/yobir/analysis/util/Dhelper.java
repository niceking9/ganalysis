package cn.yobir.analysis.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class Dhelper {
	
	
	
	   private static final String DRIVER="com.mysql.jdbc";
	    private static final String URL="jdbc:mysql://localhost:3306/dvd";
	    private static final String USER="root";
	    private static final String PASSWORD="root";
	      private static   Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);  
       
	     
	    /**
	     * 连接数据库
	     * @return 链接数据库对象
	     */
	    public  static Connection getConnection(){
	        Connection conn=null;
	        try {
	            Class.forName(DRIVER);
	        } catch (ClassNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        try {
	            conn=DriverManager.getConnection(URL, USER, PASSWORD);
	        } catch (SQLException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            
	            logger.fatal("databse connection fail");
	        }
	        return conn;
	    }

}
