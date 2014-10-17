package org.project.modules.cassandra;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class SimpleTest {

	private Cluster cluster = null;
	private Session session = null;
	private TTransport transport = null;  
	private TProtocol protocol = null;  

	@Before
	public void init() {
//		cluster = Cluster.builder().addContactPoint("192.168.10.20").build();
		cluster = Cluster.builder().addContactPoint("220.181.29.16").build();
		session = cluster.connect("tkeyspace");
		transport = new TFramedTransport(new TSocket("220.181.29.16", 9160));  
		protocol = new TBinaryProtocol(transport);  
	}
	
	@After
	public void after() {
		session.close();
        cluster.close();
        transport.close();  
	}
	
	@Test
    public void select() {
    	ResultSet rows = session.execute("SELECT * FROM users");
    	for(Row row: rows){
    		System.out.println(row.getString("fname"));
    	}
    }
	
	@Test
	public void thriftInsert() throws Exception {
        Cassandra.Client client = new Cassandra.Client(protocol);  
        transport.open();  
           
        client.set_keyspace("tkeyspace");  
        ColumnParent parent = new ColumnParent("Student");  
          
        ByteBuffer rowid = ByteBuffer.wrap("100".getBytes());  
          
        Column column = new Column();  
        column.setName("name".getBytes());  
        column.setValue("zhangsan".getBytes());  
        column.setTimestamp(System.currentTimeMillis());  
        
        client.insert(rowid, parent, column, ConsistencyLevel.ONE); 

        try {
        	transport.flush();
        } catch (TTransportException e) {
        	e.printStackTrace();
        }  
	}
	
	@Test
	public void thriftSelect01() throws Exception {
		Cassandra.Client client = new Cassandra.Client(protocol);  
        transport.open();  
        
        ByteBuffer keyspace = ByteBuffer.wrap("tkeyspace".getBytes());  
        
        ColumnPath columnPath = new ColumnPath();
        columnPath.setColumn("name".getBytes());
        
        Column column = client.get(keyspace, columnPath, ConsistencyLevel.ONE).getColumn();
	
        System.out.println("column name: " + column.getName().toString());
        System.out.println("column value: " + column.getValue().toString());
        System.out.println("column timestamp: " + column.getTimestamp());
	}
    
	@Test
	public void thriftSelect02() throws Exception {
		Cassandra.Client client = new Cassandra.Client(protocol);  
        transport.open(); 
        
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange();
        sliceRange.setStart(new byte[0]);
        sliceRange.setFinish(new byte[0]);
        predicate.setSlice_range(sliceRange);

        System.out.println("/nrow:");

        ColumnParent parent = new ColumnParent("Student");

        ByteBuffer keyspace = ByteBuffer.wrap("tkeyspace".getBytes());  
        
        List<ColumnOrSuperColumn> results = client.get_slice(keyspace, 
        		parent, predicate, ConsistencyLevel.ONE);    

        for (ColumnOrSuperColumn result : results) {
        	Column column = result.column;
        	System.out.println(column.getName().toString() + " -> "
        			+ column.getValue().toString());
        }
	}
    
}
