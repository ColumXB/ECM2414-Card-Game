package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junit.runner.Version;

public class Version_Test {

    @Test
    public void testJUnitVersion() {
        assertEquals("4.13.2", Version.id());
    }
}