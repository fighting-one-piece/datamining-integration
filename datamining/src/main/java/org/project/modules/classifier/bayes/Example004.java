package org.project.modules.classifier.bayes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/** 文本分词--正向最大匹配*/
public class Example004 {

	private static final List<String> DIC = new ArrayList<String>();  
    private static int MAX_LENGTH = 0;  
    
    static{  
        try {  
            System.out.println("开始初始化词典");  
            int max = 1;  
            int count = 0;  
            InputStream in = Example004.class.getClassLoader().getResourceAsStream("dic/dic.txt");
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		String line = br.readLine();
    		while (line != null) {
    			DIC.add(line);  
                count++;  
                if(line.length() > max){  
                    max = line.length();  
                }  
    			line = br.readLine();
    		}
    		 MAX_LENGTH = max;
            System.out.println("完成初始化词典，词数目：" + count);  
            System.out.println("最大分词长度：" + MAX_LENGTH);  
        } catch (IOException ex) {  
            System.err.println("词典装载失败:" + ex.getMessage());  
        }  
          
    }  
   
    public static List<String> seg(String text){          
        List<String> result = new ArrayList<String>();  
        while(text.length()>0){  
            int len=MAX_LENGTH;  
            if(text.length()<len){  
                len=text.length();  
            }  
            //取指定的最大长度的文本去词典里面匹配  
            String tryWord = text.substring(0, 0+len);  
            while(!DIC.contains(tryWord)){  
                //如果长度为一且在词典中未找到匹配，则按长度为一切分  
                if(tryWord.length()==1){  
                    break;  
                }  
                //如果匹配不到，则长度减一继续匹配  
                tryWord=tryWord.substring(0, tryWord.length()-1);  
            }  
            result.add(tryWord);  
            //从待分词文本中去除已经分词的文本  
            text=text.substring(tryWord.length());  
        }  
        return result;  
    }  
    
    public static void main(String[] args) throws Exception {  
        String text = "应用级产品开发平台";    
        System.out.println(seg(text));  
    }  
}
