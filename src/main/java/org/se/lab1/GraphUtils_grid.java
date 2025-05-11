package org.se.lab1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GraphUtils_grid {

    public static void visualizeGraph(Map<String, Map<String, Integer>> graph, String outputPath) {
        int width = 1200;
        int height = 800;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // 1. 统计所有节点
        Set<String> allNodes = new HashSet<>(graph.keySet());
        for (Map<String, Integer> edges : graph.values()) {
            allNodes.addAll(edges.keySet());
        }

        // 2. 分配节点坐标（优化排布）
        Map<String, Point> nodePositions = computeGridLayout(allNodes, 200, 100, 5, width, height);

        // 3. 计算节点位置的范围，确定缩放比例
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Point p : nodePositions.values()) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        // 计算缩放比例
        double scaleX = (width - 40) / (double) (maxX - minX);
        double scaleY = (height - 40) / (double) (maxY - minY);
        double scale = Math.min(scaleX, scaleY);

        // 4. 缩放节点位置
        Map<String, Point> scaledPositions = new HashMap<>();
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String node = entry.getKey();
            Point p = entry.getValue();
            int newX = (int) ((p.x - minX) * scale + 20);
            int newY = (int) ((p.y - minY) * scale + 20);
            scaledPositions.put(node, new Point(newX, newY));
        }

        // 5. 绘制图形
        for (String from : graph.keySet()) {
            Point fromPos = scaledPositions.get(from);
            for (String to : graph.get(from).keySet()) {
                Point toPos = scaledPositions.get(to);
                if (fromPos == null || toPos == null) continue;

                g2.setColor(Color.BLACK);
                drawArrow(g2, fromPos.x, fromPos.y, toPos.x, toPos.y);

                // 权重
                int midX = (fromPos.x + toPos.x) / 2;
                int midY = (fromPos.y + toPos.y) / 2;
                g2.setColor(Color.RED);
                g2.drawString(String.valueOf(graph.get(from).get(to)), midX, midY);
            }
        }

        // 6. 绘制节点
        for (Map.Entry<String, Point> entry : scaledPositions.entrySet()) {
            String word = entry.getKey();
            Point p = entry.getValue();
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - 20, p.y - 20, 40, 40);  // 空心圆圈
            g2.drawString(word, p.x - word.length() * 4, p.y + 5);  // 黑色字体居中
        }

        try {
            ImageIO.write(image, "PNG", new File(outputPath));
            System.out.println("图像保存成功：" + outputPath);
        } catch (IOException e) {
            System.out.println("保存图像失败：" + e.getMessage());
        }

        g2.dispose();
    }



    private static Map<String, Point> computeGridLayout(Set<String> nodes, int xSpacing, int ySpacing, int columns, int width, int height) {
        Map<String, Point> positions = new HashMap<>();
        int index = 0;
        int startX = 100;
        int startY = 100;
        for (String node : nodes) {
            int row = index / columns;
            int col = index % columns;
            int x = startX + col * xSpacing;
            int y = startY + row * ySpacing;
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
