package org.project.modules.cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface UsersAccessor {
	
	@Query("update users set fname=?, lname=? where user_id=?")
	public ResultSet updateFNameAndLName(String fname, String lname, int userId);

	@Query("select * from users where user_id=?")
	public Users getOneById(int userId);
	
	@Query("select * from users")
	public Result<Users> getAll();
	
}
