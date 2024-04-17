package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/*
* 过滤敏感词的util
* */
@Component
public class SensitiveFilter {
    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT="***";//替换字符
    //初始化一个根节点
    private TrieNode root=new TrieNode();

    @PostConstruct//在bean构造完自动调用（服务启动之后），只一次
    public void init(){
        //类加载器从类路径下读取文件
        try(InputStream is= this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            //从字节流中读取文字不方便，转成字符流，再转成缓冲流
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;//存从文件中读取到的敏感词
            while((keyword=reader.readLine())!=null){//读到了，就把这个敏感词加到前缀树里
                this.addKeyword(keyword);
            }
        }
        catch (Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());
        }

    }

    //把敏感词添加到前缀树(2.初始化前缀树)
    private void addKeyword(String keyword){
         TrieNode tempNode=root;//临时节点
        for(int i=0;i<keyword.length();i++){//遍历敏感词的每个字符来加到前缀树
            char c=keyword.charAt(i);
            TrieNode subNode=tempNode.getSubNode(c);
            if(subNode==null){//子节点还没有这个字符
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode=subNode;//指针往后移，以便于遍历下一个字符
            //结束标志
            if(i==keyword.length()-1)
                tempNode.setKeyWordEnd(true);
        }
    }
    //判断是否为符号（比如用一些符号防搜也能屏蔽掉
    private boolean isSymbol(Character c){
        //判断是否为普通字符(a,b,c,d)等，取反特殊字符
        //0x2E80~0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }
    /*
    * 参数：被过滤的文本； 返回：过滤后的文本
    *
    * */
    public String filter(String text){
        if(StringUtils.isBlank(text))//判空
            return null;
        //指针1 指向前缀树
        TrieNode tempNode=root;
        //指针2
        int begin=0;
        //指针3
        int position=0;
        //结果
        StringBuffer sb=new StringBuffer();//可变字符串
        while(position<text.length()){//用指针3遍历能减少遍历次数
            char c=text.charAt(position);
            if(isSymbol(c)){
                if(tempNode==root){
                    begin++;
                }
                sb.append(c);
                position++;
                continue;
            }
            //不是特殊符号，那就判断敏感词
            tempNode= tempNode.getSubNode(c);//取下级节点
            if(tempNode==null){//以begin开头到position的不是敏感词
                sb.append(text.charAt(begin));
                begin++;
                position=begin;//后移，判断下一个begin开头的是不是
                tempNode=root;//归位
            }else if(tempNode.isKeyWordEnd()){
                //以begin开头到position的是敏感词,需要替换掉
                sb.append(REPLACEMENT);
                position++;
                begin=position;//后移
                tempNode=root;
            }else{//还没结束
                position++;
            }
        }

        sb.append(text.substring(begin));
        return sb.toString();
    }

    //前缀树 节点(内部类,因为只会在这个类里用到)
    private class TrieNode{
        private boolean isKeyWordEnd=false;//关键字结束标志
        private Map<Character,TrieNode> subNodes=new HashMap<>();//子节点
        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
    }
}
