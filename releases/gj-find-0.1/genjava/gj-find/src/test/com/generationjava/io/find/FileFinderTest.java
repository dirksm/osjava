package com.generationjava.io.find;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.IOException;
import java.io.File;

import java.util.HashMap;

public class FileFinderTest extends TestCase {

    private HashMap options;
    private FileFinder finder;
    private String dirStr = "src/test/find-data/";
    private File dir = new File(dirStr);

    public FileFinderTest(String name) {
        super(name);
    }

    public void setUp() {
        finder = new FileFinder();
        options = new HashMap();
        options.put(Finder.NOT+Finder.PATH, "*.svn*");
        // uncomment for debugging
//        finder.addFindListener( new DebugListener() );
    }

    //-----------------------------------------------------------------------
    // To test: 
    // find(File, Map)

    public void testFindName() {
        options.put(Finder.NAME, "file");
        File[] files = finder.find(new File(dir, "name"), options);
        assertEquals(1, files.length);
    }

    public void testFindIName() {
        options.put(Finder.INAME, "FiLe");
        File[] files = finder.find(new File(dir, "name"), options);
        assertEquals(1, files.length);
    }

    public void testFindPath() {
        options.put(Finder.PATH, dirStr+"path/dir/file");
        File[] files = finder.find(new File(dir, "path"), options);
        assertEquals(1, files.length);
    }

    public void testFindIPath() {
        options.put(Finder.IPATH, dirStr+"PAth/dIR/fILe");
        File[] files = finder.find(new File(dir, "path"), options);
        assertEquals(1, files.length);
    }

    public void testFindNotPath() {
        options.put(Finder.NOT+Finder.PATH, dirStr+"*");
        File[] files = finder.find(new File(dir, "path"), options);
        assertEquals(0, files.length);
    }

    public void testFindRegex() {
        options.put(Finder.REGEX, dirStr+"regex/f.*");
        File[] files = finder.find(new File(dir, "regex"), options);
        assertEquals(3, files.length);
    }

    public void testFindIRegex() {
        options.put(Finder.IREGEX, dirStr+"REgeX/F.*");
        File[] files = finder.find(new File(dir, "regex"), options);
        assertEquals(3, files.length);
    }

    public void testFindEmpty() {
        options.put(Finder.EMPTY, "true");
        File[] files = finder.find(new File(dir, "empty"), options);
        assertEquals(1, files.length);
    }

    public void testFindSize() {
        options.put(Finder.SIZE, "1");
        File[] files = finder.find(new File(dir, "size"), options);
        assertEquals(1, files.length);
    }

    public void testFindTypeF() {
        options.put(Finder.TYPE, "f");
        File[] files = finder.find(new File(dir, "type"), options);
        assertEquals(1, files.length);
    }

    public void testFindTypeD() {
        options.put(Finder.TYPE, "d");
        File[] files = finder.find(new File(dir, "type"), options);
        assertEquals(2, files.length);
    }

    public void testCanReadTrue() {
        options.put(Finder.CAN_WRITE, "true");
        File[] files = finder.find(new File(dir, "can_write"), options);
        assertEquals(2, files.length);
    }

    public void testCanWriteTrue() {
        options.put(Finder.CAN_READ, "true");
        File[] files = finder.find(new File(dir, "can_read"), options);
        assertEquals(2, files.length);
    }

    public void testCanReadFalse() {
        options.put(Finder.CAN_WRITE, "false");
        File[] files = finder.find(new File(dir, "can_write"), options);
        assertEquals(0, files.length);
    }

    public void testCanWriteFalse() {
        options.put(Finder.CAN_READ, "false");
        File[] files = finder.find(new File(dir, "can_read"), options);
        assertEquals(0, files.length);
    }

}

class DebugListener implements FindListener {
    public void fileFound(FindEvent fe) {
        System.out.println("EVENT: "+fe);
    }
    public void directoryStarted(FindEvent fe) {
        System.out.println("EVENT: "+fe);
    }
    public void directoryFinished(FindEvent fe) {
        System.out.println("EVENT: "+fe);
    }
}
