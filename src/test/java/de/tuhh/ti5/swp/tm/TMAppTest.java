package de.tuhh.ti5.swp.tm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class TMAppTest extends TestCase {

  /**
   * Create the test case.
   * 
   * @param testName
   *            name of the test case
   */
  public TMAppTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(TMAppTest.class);
  }
  
  /**
   * Rigourous Test. :-)
   */
  public void testApp()
  {
      assertTrue( true );
  }

}
