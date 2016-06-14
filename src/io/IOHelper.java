package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;


public class IOHelper {
	
	
	 public static char[] readFileByChars(String fileName) {
		 	char[] charstream=new char[100];
		 	int i=0;
	        File file = new File(fileName);
	        Reader reader = null;
	        try {
//	            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
	            // 一次读一个字符
	            reader = new InputStreamReader(new FileInputStream(file));
	            int tempchar;
	            while ((tempchar = reader.read()) != -1) {
	                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
	                // 但如果这两个字符分开显示时，会换两次行。
	                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
	                if (((char) tempchar) != '\r') {
	                	charstream[i]=(char) tempchar;
	                	i++;
//	                    System.out.print((char) tempchar);
	                }
	            }
	            charstream[i]='\0';
	            reader.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (Exception e1) {
	                }
	            }
	        }
	        return charstream;
	 }
	 
	 
	 public static void writeIntoFile(String filename,ArrayList<String> content){
		 try {
			FileOutputStream fileOutputStream=new FileOutputStream(filename);
			PrintStream ps=new PrintStream(fileOutputStream);
			for(int i=0;i<content.size();i++){
				ps.println(content.get(i));
			}
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	 
	 public static void main(String[] args) {
		char[] result=readFileByChars("test.txt");
		System.out.println(result);
	}
}
