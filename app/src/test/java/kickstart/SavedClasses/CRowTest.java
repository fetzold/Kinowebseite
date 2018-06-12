package kickstart.SavedClasses;

import junit.framework.TestCase;

import kickstart.SavedClasses.*;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test class.
 * @author Alexej S.
 * @since 11/27/15
 */

public class CRowTest extends TestCase
{
	CRow c1 = new CRow(21,true);
	CSeat s1 = new CSeat(21, 21, CSeat.Etype.Parkett);
	
	@Before
	public void setUp()
	{
		c1.addSeat(s1);
	}
	
	@Test
	public void testType(){
		//assertTrue("Could not change seat status",c1.changeSeatStatus(21, EStatus.PAID));
		//assertFalse("Seat status should be not changed",c1.changeSeatStatus(666, EStatus.AVAILABLE));
	}
	 
}
