package org.se.lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

//        System.out.print("请输入文本文件路径（例如：data/input.txt）：");
//        String filePath = input.nextLine();
        String filePath = "C:\\Users\\17293\\Documents\\IDEA\\Software Engineering\\lab1\\data\\test\\Easy Test.txt";
//        String filePath = "C:\\Users\\17293\\Documents\\IDEA\\Software Engineering\\lab1\\data\\test\\Cursed Be The Treasure.txt";
//        String filePath = "C:\\Users\\17293\\Documents\\IDEA\\Software Engineering\\lab1\\data\\test\\Code Test.txt";

        WordGraph graph = new WordGraph();
        try {
            graph.buildGraphFromFile(filePath);
        } catch (FileNotFoundException e) {
            System.out.println("找不到文件：" + filePath);
            return;
        }

        // 展示图
        graph.showDirectedGraph();
        System.out.print("是否将图保存为图片？(y/n)：");
//        String save = inputScanner.nextLine();
        String save = "y";
        if (save.equalsIgnoreCase("y")) {
            graph.saveGraphImage("output_graph.png");
        }

        graph.queryBridgeWords("the", "data");
        graph.queryBridgeWords("shared", "report");
        graph.queryBridgeWords("team", "more");
        graph.queryBridgeWords("look", "and");
//        graph.queryBridgeWords("and", "exciting");
//        graph.queryBridgeWords("exciting", "synergies");

        System.out.print("请输入一行文本：");
//        String userInput = input.nextLine();
//        String userInput = "The scientist analyzed the data, wrote a report, " +
//                "and shared report with the team, but the team more data, so the scientist analyzed again.";
        String userInput = "but team requested more data, so scientist it again";
        String result = graph.generateNewText(userInput);
        System.out.println("生成的新文本为：\n" + result);

        System.out.print("请输入起点单词：");
//        String from = input.nextLine();
        String from = "analyzed";
        System.out.print("请输入终点单词：");
//        String to = input.nextLine();
        String to = "team";
        graph.findShortestPath(from, to);

        graph.computePageRank(0.85, 100, 1e-6);

        graph.randomWalk("random_walk_output.txt");

        System.out.println("Finish!");


        // This is use for git change test.

        // This is use for git change test2.

        // git change on B2
    }
}
