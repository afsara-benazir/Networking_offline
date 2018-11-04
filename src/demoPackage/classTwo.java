package demoPackage;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
public class classTwo {

  public static String str = " a get this";

    public static void main(String[] args) {
       StringTokenizer tokens = new StringTokenizer("hello there");
        System.out.println(tokens.nextToken());
    }

}
