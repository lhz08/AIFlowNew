package com.bdilab.aiflow.common.utils;

import java.io.BufferedReader;
import java.io.File;

/**
 * @author smile
 * @data 2020/8/31 12:20
 **/
public class RunCommand {
    public static void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
              Process p = Runtime.getRuntime().exec(commandStr);
              p.waitFor();
//            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line = null;
//            StringBuilder sb = new StringBuilder();
//            while ((line = br.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            br.close();
//            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        public static void main(String[] args) {
            String pyFilePath = "E:\\home\\pipelineCode\\c293dd37-dff5-4815-8ce2-9169de69ddc9.py";
            String commandStr = "python "+pyFilePath;
            RunCommand.exeCmd(commandStr);
            String filePath = pyFilePath+".yaml";
            File file = new File(filePath);
            if(!file.exists())
                System.out.println("编译失败");
            else
                System.out.println("编译成功");
            System.out.println(filePath);
        }
}
