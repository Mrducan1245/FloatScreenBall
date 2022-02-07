package com.mrducan.floatscreenball;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ReadeFramBuffer {

    public static InputStream getInputStream() throws Exception {

        FileInputStream buf = new  FileInputStream(

                new File("/dev/graphics/fb0"));

        return buf;

    }//get the InputStream from framebuffer


}
