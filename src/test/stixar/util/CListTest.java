package stixar.util;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Iterator;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import junit.framework.TestCase;

public class CListTest extends TestCase
{
    int size = 100;
    protected CList<Integer> cl;

    public CListTest()
    {
        super("CList");
        cl = new CList<Integer>();
        for (int i=0; i<100; ++i) 
            cl.add(i);
    }

    public void testCoMod()
    {
        try {
            for (Iterator<Integer> it = cl.iterator(); it.hasNext(); ) {
                cl.add(it.next());
            }
        } catch (ConcurrentModificationException e) {
            return;
        }
        assertTrue(false);
    }

    public void testRemoveFirst()
    {
        CList<Integer> c = new CList<Integer>();
        int i;
        try {
            i = c.removeFirst();
        } catch (NoSuchElementException e) {
            return;
        }
        c.addFirst(i);
        assertTrue(false);
    }

    public void testListCell()
    {
        int i=0;
        for (ListCell<Integer> c = cl.firstCell(); c != null; c = c.next()) {
            i++;
        }
        assertTrue(i == cl.size());
        i = 0;
        for (ListCell<Integer> c = cl.lastCell(); c != null; c = c.prev()) {
            c.value(c.value() - 1);
            i++;
        }
        assertTrue(i == cl.size());
    }

    public void testAppend()
    {
        CList<Integer> tmp = new CList<Integer>();
        tmp.add(42);
        int osz = cl.size();
        cl.append(tmp);
        assertTrue(tmp.size() == 0);
        assertTrue(osz == cl.size() - 1);
        assertTrue(cl.last() == 42);
        try {
            System.out.println("about to self append");
            cl.append(cl);
            System.out.println("done self append");
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }

    public void testPrepend()
    {
        CList<Integer> tmp = new CList<Integer>();
        tmp.add(42);
        int osz = cl.size();
        cl.prepend(tmp);
        assertTrue(tmp.size() == 0);
        assertTrue(osz == cl.size() - 1);
        assertTrue(cl.first() == 42);
        try {
            cl.prepend(cl);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }

    @SuppressWarnings("unchecked")
    public void testSerial()
        throws IOException
    {
        String tmpDirName = System.getProperty("stixar.test.tmpdir", "tmp");
        System.out.println("tmpDirName: " + tmpDirName);
        File tmpDir = new File(tmpDirName);
        if (!tmpDir.exists()) {
            throw new IllegalStateException("tmp dir doesn't exist, build fukt.");
        }
        File serFile = new File(tmpDir, "CListTestSerialize.ser");
        FileOutputStream out = new FileOutputStream(serFile);
        ObjectOutputStream oOut = new ObjectOutputStream(out);
        oOut.writeObject(cl);
        oOut.close();
        CList<Integer> inList = null;
        try {
            FileInputStream in = new FileInputStream(serFile);
            ObjectInputStream oIn = new ObjectInputStream(in);
            inList = (CList<Integer>) oIn.readObject();
        } catch (ClassNotFoundException e) {
            throw new Error();
        }
        assertTrue(inList.size() == cl.size());
        ListCell<Integer> inCell = inList.firstCell();
        ListCell<Integer> clCell = cl.firstCell();
        while(inCell != null) {
            System.out.println("inCell: " + inCell.value() + 
                               "clCell: " + clCell.value());
            assertTrue(inCell.value().equals(clCell.value()));
            inCell = inCell.next();
            clCell = clCell.next();
        }
    }


    public static void main(String[] args)
    {
        CListTest t = new CListTest();
        System.out.println("comod:");
        t.testCoMod();
        System.out.println("rmhd:");
        t.testRemoveFirst();
        System.out.println("cel:");
        t.testListCell();
        System.out.println("append:");
        t.testAppend();
        System.out.println("prepend:");
        t.testPrepend();
    }

}
