package com.gravitext.crawler;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gravitext.crawler.VisitURL.SyntaxException;

public class VisitURLTest
{
    @Test
    public void testHashURL()
    {
        CharSequence hash = VisitURL.hashURL( "http://gravitext.com" );
        assertEquals( 23, hash.length() );

        CharSequence ohash = VisitURL.hashURL( "http://gravitext.com/blog/x/y" );
        assertFalse( hash.toString().equals( ohash.toString() ) );
    }
    
    @Test
    public void testHashDomain()
    {
        CharSequence hash = VisitURL.hashDomain( "gravitext.com" );
        assertEquals( 6, hash.toString().length() );
        CharSequence ohash = VisitURL.hashURL( "other.com" );
        assertFalse( hash.toString().equals( ohash.toString() ) );
    }
    
    @Test
    public void testNormalize() throws SyntaxException
    {
        String[][] sameURLs = new String[][] {
            { "http://h.c/foo", "http://h.c/foo" },
            { "http://h.c/foo", "http://h.c/foo?" },
            { "http://h.c/", "http://h.c" },

            { "http://h.c/foo", " \thttp://h.c/foo\n\r\t" },
            { "http://h.c/foo?q=x%20y", 
                "http://h.c/foo?q=x y", 
                "http://h.c/foo?q=x  y", 
                " http://h.c/foo?q=x\t y#anchor\t" },
                
            { "http://h.c/foo", "HTTP://H.C:80/foo" },
            { "http://h.c/foo", 
              "HTTP://h.c/bar/../foo",
              "http://h.c/./foo" },
              
            { "http://h.c/?x=a%26b", "http://h.c/?x=a%26b" },
            { "http://h.c/a%20b", "http://h.c/a%20b" },
            //FIXME: { "http://h.c/?x=a%3Ab", "http://h.c/?x=a%3ab" },
            //FIXME: { "http://h.c/%C2", "http://h.c/%C2", "http://h.c/%c2" },
            //FIXME: { "http://h.c/foo%20bar", "HTTP://h.c/%66oo%20bar" },
            //FIXME: { "http://h.c/a%5Bb%5D", "http://h.c/a[b]" },
            { "http://h.c/f%C5%8Do", "HTTP://h.c/fōo" },
            { "http://h.c/foo", "http://h.c/foo#anchor" },
            { "http://h.c/foo?query=a+b", "HTTP://h.c/foo?query=a+b#anchor" },
            //FIXME: IDN { "http://xn--bcher-kva.ch/", "http://Bücher.ch" }
        };
        
        for( String[] row : sameURLs ) {
            for( int i = 1; i < row.length; ++i ) {
                assertNormal( row[0], row[i] );
            }
        }
    }
    
    public void assertNormal( String norm, String raw ) 
        throws SyntaxException
    {
        assertEquals( norm, VisitURL.normalize( raw ).toString() );
    }
   
}
    