
package cn.yobir.ganalysis.dao;

import java.sql.SQLException;
import java.util.*;

public interface NumberDao {

	public boolean add(Map map) throws SQLException;

	public int delete(Map map)throws SQLException;

	public int update(Map map)throws SQLException;

	public List<Map> findAsLimit(int start_index, int end_index)throws SQLException;
	public int getCount()throws SQLException;

}
