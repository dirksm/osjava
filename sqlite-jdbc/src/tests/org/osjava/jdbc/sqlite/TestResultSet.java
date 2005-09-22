/*
 * org.osjava.jdbc.sqlite.TestResultSet
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Sep 21, 2005
 *
 * Copyright (c) 2005, Robert M. Zigweid All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * + Neither the name of the SQLite-JDBC nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without 
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package org.osjava.jdbc.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

/**
 * ResultSet tests.
 * 
 * @author Robert M. Zigweid
 */
public class TestResultSet extends TestCase {
    private Connection con = null;

    /**
     * Setup the Connection.  Table setup and population are done in 
     * individual tests
     */
    protected void setUp() throws Exception {
        super.setUp();
        try {
            Class.forName("org.osjava.jdbc.sqlite.Driver");
            con = DriverManager.getConnection("jdbc:sqlite:local.db");
        } catch (ClassNotFoundException e) {
            fail();
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        con.close();
        /* If the local.db file exists, remove it. */
        File db = new File("local.db");
        if(db.exists()) {
            db.delete();
        }
    }

    /**
     * Get a String from the table base upon the column number.
     */
    public void testGetString1() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE foo (TestCol VARCHAR(10));");
        int count = stmt.executeUpdate("INSERT INTO foo (TestCol) VALUES (\"Test\");");
        /* Make sure that the row was actually added */
        assertEquals(count, 1);
        /* Commit before query */
        java.sql.ResultSet result = stmt.executeQuery("SELECT * FROM foo;");
        result.beforeFirst();
        boolean alreadyPassed = false;
        while(result.next()) {
            if(alreadyPassed) {
                fail("Too many rows in ResultSet");
            }
            alreadyPassed = true;
            String foo = result.getString(1);
            assertEquals("Test", foo);
        }
        if(!alreadyPassed) {
            fail();
        }
    }

    /**
     * Get a String from the table base upon the column name.
     */
    public void testGetString2() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE foo (TestCol VARCHAR(10));");
        int count = stmt.executeUpdate("INSERT INTO foo (TestCol) VALUES (\"Test\");");
        /* Make sure that the row was actually added */
        assertEquals(count, 1);
        /* Commit before query */
        java.sql.ResultSet result = stmt.executeQuery("SELECT * FROM foo;");
        result.beforeFirst();
        boolean alreadyPassed = false;
        while(result.next()) {
            if(alreadyPassed) {
                fail("Too many rows in ResultSet");
            }
            alreadyPassed = true;
            String foo = result.getString("TestCol");
            assertEquals("Test", foo);
        }
        if(!alreadyPassed) {
            fail();
        }
   }
       
    /**
     * Simple test which puts an integer into a column and retrieves it from
     * the ResultSet.  The column number is used instead of the column name.
     * @throws Exception
     */
    public void testGetInt1() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE foo (TestCol INTEGER);");
        int count = stmt.executeUpdate("INSERT INTO foo (TestCol) VALUES (1234)");
        /* Make sure that the row was actually added */
        assertEquals(count, 1);
        /* Commit before query */
        java.sql.ResultSet result = stmt.executeQuery("SELECT * FROM foo;");
        result.beforeFirst();
        boolean alreadyPassed = false;
        while(result.next()) {
            if(alreadyPassed) {
                fail("Too many rows in ResultSet");
            }
            alreadyPassed = true;
            int foo = result.getInt(1);
            assertEquals(1234, foo);
        }
        if(!alreadyPassed) {
            fail();
        }
    }

    /**
     * Same test as testGetInt1, except that it uses the column name instead
     */
    public void testGetInt2() throws Exception {
        java.sql.Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE foo (TestCol INTEGER);");
        int count = stmt.executeUpdate("INSERT INTO foo (TestCol) VALUES (1234)");
        /* Make sure that the row was actually added */
        assertEquals(count, 1);
        /* Commit before query */
        java.sql.ResultSet result = stmt.executeQuery("SELECT * FROM foo;");
        result.beforeFirst();
        boolean alreadyPassed = false;
        while(result.next()) {
            if(alreadyPassed) {
                fail("Too many rows in ResultSet");
            }
            alreadyPassed = true;
            int foo = result.getInt("TestCol");
            assertEquals(1234, foo);
        }
        if(!alreadyPassed) {
            fail();
        }
   }

    /** 
     * Test to put the data into the table as a string, even though it is a number
     * and try to convert it to an int on the way out.
     * 
     * @throws Exception
     */
    public void testGetInt3() throws Exception {
        /* XXX: Confirm with the spec whether or not this behavior is allowed */
        java.sql.Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE foo (TestCol VARCHAR(10));");
        int count = stmt.executeUpdate("INSERT INTO foo (TestCol) VALUES (\"1234\");");
        /* Make sure that the row was actually added */
        assertEquals(count, 1);
        /* Commit before query */
        java.sql.ResultSet result = stmt.executeQuery("SELECT * FROM foo;");
        result.beforeFirst();
        boolean alreadyPassed = false;
        while(result.next()) {
            if(alreadyPassed) {
                fail("Too many rows in ResultSet");
            }
            alreadyPassed = true;
            int foo = result.getInt(1);
            assertEquals(1234, foo);
        }
        if(!alreadyPassed) {
            fail();
        }
    }
    
    /** 
     * Test to put the data into the table as a string.  The data is not able 
     * to be converted to a string, so we are expecting an exception to be 
     * thrown here.
     * 
     * @throws Exception
     */
    public void testGetInt4() throws Exception {
        /* XXX: Confirm with the spec whether or not this behavior is allowed */
        java.sql.Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE foo (TestCol VARCHAR(10));");
        int count = stmt.executeUpdate("INSERT INTO foo (TestCol) VALUES (\"Test\");");
        /* Make sure that the row was actually added */
        assertEquals(count, 1);
        /* Commit before query */
        java.sql.ResultSet result = stmt.executeQuery("SELECT * FROM foo;");
        result.beforeFirst();
        boolean alreadyPassed = false;
        while(result.next()) {
            if(alreadyPassed) {
                fail("Too many rows in ResultSet");
            }
            alreadyPassed = true;
            try {
                int foo = result.getInt(1);
            } catch (SQLiteException e) {
                if(e.getCause() instanceof NumberFormatException) {
                    continue;
                }
            }
        }
        if(!alreadyPassed) {
            fail();
        }
    }
}