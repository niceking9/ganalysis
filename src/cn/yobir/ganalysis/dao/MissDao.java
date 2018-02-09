package cn.yobir.ganalysis.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface MissDao {
	public boolean  add(Map map)throws SQLException;
	public int  update(Map map)throws SQLException;
	public List<Map> findAll(); 
	

}
