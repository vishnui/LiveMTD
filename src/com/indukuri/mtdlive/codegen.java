package com.indukuri.mtdlive ;

import java.io.File;

public class codegen{
    public static void main(String[] args){
        File folder = new File("./");
		File[] listOfFiles = folder.listFiles();
		// Read each file in the directory
		for (File file : listOfFiles) {
			if (file.isFile()) {
			    String name = file.getName() ;
			    int buspngIndex = name.indexOf("bus.png") ;
			    if(buspngIndex == -1) continue ;
			    name = name.substring(0, buspngIndex) ;
				System.out.println("else if(route.contains(\""+name+"\")) ret = R.drawable."+name+"bus ;") ;
			}
		}
	}
}
