package org.se.lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class WordGraph {

    // 图结构：每个单词指向下一个单词，记录边的出现次数
    private final Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();

    public void buildGraphFromFile(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        List<String> words = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().toLowerCase();
            line = line.replaceAll("[^a-zA-Z\\s]", " "); // 非字母换为空格
            String[] lineWords = line.trim().split("\\s+");
            words.addAll(Arrays.asList(lineWords));
        }

        for (int i = 0; i < words.size() - 1; i++) {
            String from = words.get(i);
            String to = words.get(i + 1);

            if (from.isEmpty() || to.isEmpty()) continue;

            adjacencyList.putIfAbsent(from, new HashMap<>());
            Map<String, Integer> edges = adjacencyList.get(from);
            edges.put(to, edges.getOrDefault(to, 0) + 1);
        }
    }

    public void showDirectedGraph() {
        System.out.println("当前有向图如下：");
        for (Map.Entry<String, Map<String, Integer>> entry : adjacencyList.entrySet()) {
            String from = entry.getKey();
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                String to = edge.getKey();
                int weight = edge.getValue();
                System.out.printf("%s -> %s [权重: %d]%n", from, to, weight);
            }
        }
    }

    public void saveGraphImage(String filePath) {
        GraphUtils.visualizeGraph(adjacencyList, filePath);
    }


    // 后续功能会用到邻接表结构
    public Map<String, Map<String, Integer>> getAdjacencyList() {
        return adjacencyList;
    }

    public void queryBridgeWords(String word1, String word2) {
        // 检查输入单词是否存在于图中
        if (!adjacencyList.containsKey(word1) || !adjacencyList.containsKey(word2)) {
            System.out.println("No " + word1 + " or " + word2 + " in the graph!");
            return;
        }

        // 用来存储桥接词
        Set<String> bridgeWords = new HashSet<>();

        // 查找word1 -> word3 和 word3 -> word2的路径
        Map<String, Integer> neighborsOfWord1 = adjacencyList.get(word1);
        if (neighborsOfWord1 == null) {
            System.out.println("No \"" + word1 + "\" or \"" + word2 + "\" in the graph!");
            return;
        }

        // 遍历所有word1的邻接词
        for (String word3 : neighborsOfWord1.keySet()) {
            Map<String, Integer> neighborsOfWord3 = adjacencyList.get(word3);
            if (neighborsOfWord3 != null && neighborsOfWord3.containsKey(word2)) {
                bridgeWords.add(word3);
            }
        }

        // 输出结果
        if (bridgeWords.isEmpty()) {
            System.out.println("No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!");
        } else {
            System.out.print("The bridge words from \"" + word1 + "\" to \"" + word2 + "\" is: \"");
            // 输出所有桥接词
            String result = String.join(", ", bridgeWords);
            System.out.println(result + "\".");
        }
    }

    public String generateNewText(String inputText) {
        if (inputText == null || inputText.isEmpty()) return "";

        // 清洗输入文本：小写 + 去除非字母
        String cleaned = inputText.toLowerCase().replaceAll("[^a-zA-Z\\s]", " ");
        String[] words = cleaned.trim().split("\\s+");

        if (words.length < 2) return inputText;

        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            result.append(word1);

            Set<String> bridgeWords = findBridgeWords(word1, word2);
            if (!bridgeWords.isEmpty()) {
                // 随机选一个桥接词
                int idx = random.nextInt(bridgeWords.size());
                String bridge = bridgeWords.stream().toList().get(idx);
                result.append(" ").append(bridge);
            }

            result.append(" ");
        }

        // 添加最后一个单词
        result.append(words[words.length - 1]);
        return result.toString();
    }

    private Set<String> findBridgeWords(String word1, String word2) {
        Set<String> bridgeWords = new HashSet<>();
        Map<String, Integer> neighborsOfWord1 = adjacencyList.get(word1);

        if (neighborsOfWord1 == null) return bridgeWords;

        for (String word3 : neighborsOfWord1.keySet()) {
            Map<String, Integer> neighborsOfWord3 = adjacencyList.get(word3);
            if (neighborsOfWord3 != null && neighborsOfWord3.containsKey(word2)) {
                bridgeWords.add(word3);
            }
        }
        return bridgeWords;
    }

    public void findShortestPath(String start, String end) {
        start = start.toLowerCase();
        end = end.toLowerCase();

        if (!adjacencyList.containsKey(start)) {
            System.out.println("No such word \"" + start + "\" in the graph!");
            return;
        }
        if (!adjacencyList.containsKey(end)) {
            System.out.println("No such word \"" + end + "\" in the graph!");
            return;
        }

        // 最短路径表
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // 初始化
        for (String word : adjacencyList.keySet()) {
            distances.put(word, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.offer(start);

        // Dijkstra 主循环
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (!visited.add(current)) continue;

            Map<String, Integer> neighbors = adjacencyList.get(current);
            if (neighbors == null) continue;

            int currentDist = distances.get(current);

            for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                String neighbor = entry.getKey();
                int weight = entry.getValue();

                int neighborDist = distances.getOrDefault(neighbor, Integer.MAX_VALUE);
                if (currentDist + weight < neighborDist) {
                    distances.put(neighbor, currentDist + weight);
                    previous.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }


        // 若目标单词不可达
        if (!previous.containsKey(end) && !start.equals(end)) {
            System.out.println("No path from \"" + start + "\" to \"" + end + "\"!");
            return;
        }

        // 回溯构造路径
        LinkedList<String> path = new LinkedList<>();
        String current = end;
        while (current != null) {
            path.addFirst(current);
            current = previous.get(current);
        }

        // 输出路径
        System.out.println("Shortest path from \"" + start + "\" to \"" + end + "\":");
        System.out.println(String.join(" -> ", path));
        System.out.println("Total path weight: " + distances.get(end));
    }

    public Map<String, Double> computePageRank(double d, int maxIterations, double tolerance) {
        Map<String, Double> pageRank = new HashMap<>();
        Set<String> nodes = new HashSet<>(adjacencyList.keySet());

        // 添加所有被指向的节点
        for (Map<String, Integer> neighbors : adjacencyList.values()) {
            nodes.addAll(neighbors.keySet());
        }

        int N = nodes.size();
        double initRank = 1.0 / N;

        // 初始化所有节点PageRank
        for (String node : nodes) {
            pageRank.put(node, initRank);
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            Map<String, Double> newRank = new HashMap<>();

            // 初始化为 (1-d)/N
            for (String node : nodes) {
                newRank.put(node, (1 - d) / N);
            }

            // 对每个节点，将其PR值按出边分发
            for (String from : adjacencyList.keySet()) {
                Map<String, Integer> toNodes = adjacencyList.get(from);
                int outDegree = toNodes.size();
                if (outDegree == 0) continue;

                double share = pageRank.get(from) / outDegree;
                for (String to : toNodes.keySet()) {
                    newRank.put(to, newRank.get(to) + d * share);
                }
            }

            // 检查收敛性
            double delta = 0.0;
            for (String node : nodes) {
                delta += Math.abs(newRank.get(node) - pageRank.get(node));
            }

            pageRank = newRank;

            if (delta < tolerance) {
                System.out.println("PageRank converged at iteration " + iter);
                break;
            }
        }

        // 输出结果
        System.out.println("PageRank Results:");
        pageRank.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> System.out.printf("%-15s : %.5f%n", e.getKey(), e.getValue()));

        return pageRank;
    }

    public void randomWalk(String outputFilePath) {
        if (adjacencyList.isEmpty()) {
            System.out.println("图为空，无法进行随机游走！");
            return;
        }

        Random rand = new Random();
        Set<String> nodes = adjacencyList.keySet();
        List<String> nodeList = new ArrayList<>(nodes);
        String current = nodeList.get(rand.nextInt(nodeList.size()));

        Set<String> visitedEdges = new HashSet<>();
        List<String> walk = new ArrayList<>();
        walk.add(current);

        Scanner scanner = new Scanner(System.in);

        System.out.println("开始随机游走，输入 Enter 可随时终止...");
        while (true) {
            Map<String, Integer> neighbors = adjacencyList.get(current);
            if (neighbors == null || neighbors.isEmpty()) {
                System.out.println("节点 [" + current + "] 没有出边，终止游走。");
                break;
            }

            List<String> nextNodes = new ArrayList<>(neighbors.keySet());
            String next = nextNodes.get(rand.nextInt(nextNodes.size()));

            String edge = current + "->" + next;
            if (visitedEdges.contains(edge)) {
                System.out.println("边 [" + edge + "] 重复，终止游走。");
                break;
            }

            visitedEdges.add(edge);
            current = next;
            walk.add(current);

            System.out.println("当前路径：" + String.join(" ", walk));

            // 用户主动中止
            try {
                if (System.in.available() > 0) {
                    String input = scanner.nextLine();
                    if (!input.isEmpty()) {
                        System.out.println("用户中止游走。");
                        break;
                    }
                }
            } catch (IOException e) {
                // 忽略异常
            }
        }

        // 写入文件
        try (PrintWriter writer = new PrintWriter(outputFilePath)) {
            for (String word : walk) {
                writer.print(word + " ");
            }
            System.out.println("游走结果已保存到文件：" + outputFilePath);
        } catch (IOException e) {
            System.err.println("写入文件失败：" + e.getMessage());
        }
    }


}
