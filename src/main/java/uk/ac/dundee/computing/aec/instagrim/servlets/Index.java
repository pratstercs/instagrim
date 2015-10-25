package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import com.datastax.driver.core.Row;

/**
 *
 * @author Phil
 */
public class Index {
    

    public static String getPic() {
        Cluster cluster = CassandraHosts.getCluster();
        Session session = cluster.connect("instagrim_PJP");
        
        PreparedStatement ps = session.prepare("select * from userpiclist LIMIT 1");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute( boundStatement.bind() );
        
        Row row = rs.one();
        Pic pic = new Pic();
        
        session.close();
        return pic.getSUUID();
    }

}