package org.concentus;
public final class ArrayOps {
 private static void bounds(int size,int from,int to){if(from>to)throw new IllegalArgumentException();if(from<0||to>size)throw new ArrayIndexOutOfBoundsException();}
 public static void fill(byte[] a,int from,int to,byte value){bounds(a.length,from,to);while(from<to)a[from++]=value;}
 public static void fill(short[] a,int from,int to,short value){bounds(a.length,from,to);while(from<to)a[from++]=value;}
 public static void fill(int[] a,int from,int to,int value){bounds(a.length,from,to);while(from<to)a[from++]=value;}
}
