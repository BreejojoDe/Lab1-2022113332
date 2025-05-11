package org.se.lab1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GraphUtils {

    public static void visualizeGraph(Map<String, Map<String, Integer>> graph, String outputPath) {
        int width = 1200;
        int height = 800;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // 设置抗锯齿和字体
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.BLACK);

        // 生成布局
        Set<String> allNodes = new HashSet<>(graph.keySet());
        for (Map<String, Integer> edges : graph.values()) {
            allNodes.addAll(edges.keySet());
        }
        Map<String, Point> nodePositions = computeCircularLayout(allNodes, width / 2, height / 2, 300);


        // 画边
        for (String from : graph.keySet()) {
            Point fromPos = nodePositions.get(from);
            for (String to : graph.get(from).keySet()) {
                Point toPos = nodePositions.get(to);

                g2.setColor(Color.GRAY);
                drawArrow(g2, fromPos.x, fromPos.y, toPos.x, toPos.y);

                // 画权重
                int weight = graph.get(from).get(to);
                int midX = (fromPos.x + toPos.x) / 2;
                int midY = (fromPos.y + toPos.y) / 2;
                g2.setColor(Color.RED);
                g2.drawString(String.valueOf(weight), midX, midY);
            }
        }

        // 画节点
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String word = entry.getKey();
            Point p = entry.getValue();
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - 20, p.y - 20, 40, 40);  // 空心圆圈
            g2.drawString(word, p.x - word.length() * 4, p.y + 5);  // 黑色字体居中
        }

        // 保存图片
        try {
            ImageIO.write(image, "PNG", new File(outputPath));
            System.out.println("图像保存成功：" + outputPath);
        } catch (IOException e) {
            System.out.println("保存图像失败：" + e.getMessage());
        }

        g2.dispose();
    }

    private static Map<String, Point> computeCircularLayout(Set<String> nodes, int centerX, int centerY, int radius) {
        Map<String, Point> positions = new HashMap<>();
        int total = nodes.size();
        int index = 0;
        for (String node : nodes) {
            double angle = 2 * Math.PI * index / total;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            positions.put(node, new Point(x, y));
            index++;
        }
        return positions;
    }

    private static void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        // 计算从 (x1, y1) 到 (x2, y2) 的向量
        double dx = x2 - x1;
        double dy = y2 - y1;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // 单位向量
        double ux = dx / distance;
        double uy = dy / distance;

        // 计算圆的半径，半径是20
        int radius = 20;

        // 起点：将箭头起点移到源圆的边缘
        int startX = (int) (x1 + ux * radius);
        int startY = (int) (y1 + uy * radius);

        // 终点：将箭头终点移到目标圆的边缘
        int endX = (int) (x2 - ux * radius);
        int endY = (int) (y2 - uy * radius);

        // 绘制线
        g2.drawLine(startX, startY, endX, endY);

        // 箭头的大小和角度
        int arrowLength = 10;
        int arrowWidth = 5;

        // 计算箭头指向的位置
        int arrowX = endX - (int) (arrowLength * ux);
        int arrowY = endY - (int) (arrowLength * uy);

        // 计算箭头两边的偏移量
        int arrowLeftX = (int) (arrowX + arrowWidth * uy);
        int arrowLeftY = (int) (arrowY - arrowWidth * ux);
        int arrowRightX = (int) (arrowX - arrowWidth * uy);
        int arrowRightY = (int) (arrowY + arrowWidth * ux);

        // 绘制箭头
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(endX, endY);  // 箭头尖端
        arrowHead.addPoint(arrowLeftX, arrowLeftY);  // 左侧箭头
        arrowHead.addPoint(arrowRightX, arrowRightY);  // 右侧箭头
        g2.fill(arrowHead);
    }
}
