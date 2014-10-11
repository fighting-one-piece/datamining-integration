package org.project.modules.cassandra;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

public class UserTest {

	private Cluster cluster;
	private Session session;

	@Before
	public void init() {
		cluster = Cluster.builder().addContactPoint("192.168.10.20").build();
		session = cluster.connect("tkeyspace");
	}
	
	@After
	public void after() {
		close();
	}
	
	public void close() {
		session.close();
        cluster.close();
	}
	
	@Test
	public void testSave() {
		Mapper<Users> m = new MappingManager(session).mapper(Users.class);
		for (int i = 0; i < 10; i++) {
			Users users = new Users();
			users.setUserId(i);
			users.setFname("usersFName" + i);
			users.setLname("usersLName" + i);
			m.save(users);
		}
	}
	
	@Test
	public void testUpdate() {
		MappingManager manager = new MappingManager(session);
		UsersAccessor usersAccessor = manager.createAccessor(UsersAccessor.class);
		usersAccessor.updateFNameAndLName("usersFName", "usersLName", 1745);
	}
	
	@Test
	public void testDelete() {
		Mapper<Users> m = new MappingManager(session).mapper(Users.class);
		Users users = new Users();
		users.setUserId(0);
		m.delete(users);
		m.delete(1);
	}
	
	@Test
	public void testGetOneById() {
		MappingManager manager = new MappingManager(session);
		UsersAccessor usersAccessor = manager.createAccessor(UsersAccessor.class);
		Users users = usersAccessor.getOneById(1745);
		System.out.println(users.getFname());
	}
	
	@Test
	public void testGetAll() {
		MappingManager manager = new MappingManager(session);
		UsersAccessor usersAccessor = manager.createAccessor(UsersAccessor.class);
		Result<Users> usersList = usersAccessor.getAll();
		for (Users users : usersList.all()) {
			System.out.println(users.getFname());
		}
	}

}
