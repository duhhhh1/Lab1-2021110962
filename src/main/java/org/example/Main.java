package org.example;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.*;
import org.graphstream.stream.file.FileSinkImages;


class Type {
    String word;
    Map<Type, Integer> outgoingEdges;

    Type(String word) {
        this.word = word;
        outgoingEdges = new HashMap<>();
    }
}

public class Main {
    private static Map<String, Type> nodes = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static Graph graph = new SingleGraph("WordGraph");
    private static String getFilePath() {
        System.out.print("Enter file path (or 'q' to quit): ");
        String filePath = scanner.nextLine();
        if (filePath.equalsIgnoreCase("q")) {
            System.exit(0);
        }
        return filePath;
    }

    public static String processText(String filePath) throws IOException {
        StringBuilder cleanedText = new StringBuilder();
        String text = new String(Files.readAllBytes(Paths.get(filePath)));

        // 将换行/回车符替换为空格
        text = text.replaceAll("\\r?\\n", " ");

        // 将标点符号替换为空格
        String punctuationRegex = "[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]";
        text = text.replaceAll(punctuationRegex, " ");

        // 删除非字母字符
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) || Character.isWhitespace(c)) {
                cleanedText.append(c);
            }
        }
        // 将所有大写字母转换为小写
        cleanedText = new StringBuilder(cleanedText.toString().toLowerCase());
        // 将多个连续空格替换为单个空格
        return cleanedText.toString().replaceAll(" +", " ").trim();
    }

    public static void showDirectedGraph(Map<String, Type> G)//展示有向图
    {
        graph.addAttribute("ui.stylesheet",
                "node { shape:box;fill-color: cyan; size: 60px; text-size: 20; text-alignment: center;} " +
                "edge { shape: line; arrow-shape: arrow; size: 2px; text-size: 16;arrow-size: 8px;text-alignment: center;}");

        // 添加节点
        for (Map.Entry<String, Type> entry : G.entrySet()) {
            Node node = graph.addNode(entry.getKey());
            node.addAttribute("ui.label", entry.getKey());
            //node.addAttribute("ui.layout", "center");
            node.addAttribute("ui.style", "text-alignment: center;");
        }

        // 添加边
        for (Map.Entry<String, Type> entry : G.entrySet()) {
            Type sourceNode = entry.getValue();
            for (Map.Entry<Type, Integer> edge : sourceNode.outgoingEdges.entrySet()) {
                Type targetNode = edge.getKey();
                Edge e = graph.addEdge(sourceNode.word + "->" + targetNode.word, sourceNode.word, targetNode.word, true);
                e.addAttribute("ui.label", edge.getValue().toString()); // 为边添加权重标签
            }
        }
        Viewer viewer=graph.display();
    }

    public static String queryBridgeWords(String word1, String word2)//查询桥接词
    {
        String bridge_words="";
        Type f1 = nodes.get(word1);//检查word1是否在图中
        Type f2 = nodes.get(word2);//检查word2是否在图中
        for (Map.Entry<String, Type> entry : nodes.entrySet()) {
            if (entry.getValue().word.equals(word1)){
                for (Map.Entry<Type, Integer> edge : entry.getValue().outgoingEdges.entrySet()) {
                    for(Map.Entry<Type, Integer> bridge : edge.getKey().outgoingEdges.entrySet()) {
                        if (bridge.getKey().word.equals(word2)){
                            bridge_words += edge.getKey().word;
                            bridge_words += ' ';
                        }
                    }
                }
            }
        }
        // 去除最后一个字符
        if(bridge_words.length()!=0) {
            bridge_words = bridge_words.substring(0, bridge_words.length() - 1);
        }
        if (f1 == null && f2 == null){
            System.out.println("No \""+word1+"\" and \""+word2+"\" in the graph !");
            bridge_words="1";
        }else if (f1 == null && f2 != null){
            System.out.println("No \""+word1+"\" in the graph !");
            bridge_words="1";
        }else if (f1 != null && f2 == null){
            System.out.println("No \""+word2+"\" in the graph !");
            bridge_words="1";
        }
        return bridge_words;
    }

    public static String generateNewText(String inputText)//根据bridge word生成新文本
    {
        // 使用 split() 方法将字符串按空格分割成数组
        String[] parts = inputText.split("\\s+"); // 使用正则表达式 \\s+ 匹配一个或多个空格
        String word ;
        String[] newArray;
        for (int i = 0; i < parts.length-1; i++) {
            word = queryBridgeWords(parts[i], parts[i+1]);
            if (!word.isEmpty()&&!word.equals("1")) {
                // 创建一个新的数组，长度比原始数组大 1
                newArray = new String[parts.length + 1];
                // 将原始数组中插入位置之前的元素复制到新数组中
                System.arraycopy(parts, 0, newArray, 0, i+1);
                // 在指定位置插入字符串
                newArray[i+1] = word;
                // 将原始数组中插入位置之后的元素复制到新数组中
                System.arraycopy(parts, i+1, newArray, i + 2, parts.length - i - 1);
                parts = newArray;
                i++;
            }
        }
        String result;
        result = String.join(" ", parts);
        System.out.println("生成新文本："+result);
        return result;
    }

    public static String shortestPath(String start, String end) {
        // 用于存储节点到起始点的距离
        Map<String, Integer> distances = new HashMap<>();
        // 用于存储节点的前驱节点
        Map<String, Type> previousNodes = new HashMap<>();

        // 初始化距离和前驱节点
        for (String node : nodes.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previousNodes.put(node, null);
        }

        distances.put(start, 0); // 起始点到自身的距离为0

        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        queue.add(start);

        while (!queue.isEmpty()) {
            String currentNode = queue.poll();

            if (currentNode.equals(end)) {
                // 构建最短路径
                StringBuilder shortestPath = new StringBuilder();
                while (previousNodes.get(currentNode) != null) {
                    shortestPath.insert(0, currentNode + " ");
                    currentNode = previousNodes.get(currentNode).word;
                }
                shortestPath.insert(0, start + " ");
                return shortestPath.toString();
            }

            Type currentType = nodes.get(currentNode);
            for (Map.Entry<Type, Integer> entry : currentType.outgoingEdges.entrySet()) {
                Type neighborType = entry.getKey();
                int weight = entry.getValue();
                int distanceThroughCurrent = distances.get(currentNode) + weight;
                if (distanceThroughCurrent < distances.get(neighborType.word)) {
                    distances.put(neighborType.word, distanceThroughCurrent);
                    previousNodes.put(neighborType.word, currentType);
                    queue.add(neighborType.word);
                }
            }
        }

        return "No path found";
    }

    public static String randomWalk()//随机游走
    {
        Type currentNode = new Type("Word");
        List<String> traversal = new ArrayList<>();
        List<String> visitedEdges = new ArrayList<>();
        // 从 nodes 中随机选择一个对象的方法
        Function<Type, Type> getRandom = (map) -> {
                Type result = nodes.get(map.word);
                Random random = new Random();
                if(result!=null){
                int index = random.nextInt(result.outgoingEdges.size());
                int i = 0;
                for (Type entry : result.outgoingEdges.keySet()) {
                    if (index == i) {
                        return entry;
                    }
                    i++;
                }}
                return null;
        };
        //随机选择起始节点
        Random random = new Random();
        int index = random.nextInt(nodes.values().size());
        int i = 0;
        for (Map.Entry<String, Type> entry : nodes.entrySet()) {
            if (index == i) {
               currentNode = entry.getValue();
            }
            i++;
        }
        System.out.println("起始节点为："+currentNode.word);
        //循环遍历随机游走
        while (true) {
            traversal.add(currentNode.word);
            System.out.println("当前路径："+traversal+"(输入1继续游走，输入0停止游走)");
            int k=scanner.nextInt();
            scanner.nextLine();
            if(k==0){
                break;
            }
            Type currentNodeType = nodes.get(currentNode.word);
            if (currentNodeType == null || currentNodeType.outgoingEdges.isEmpty()){
                System.out.println("当前节点"+currentNode.word+"无出边");
                break;
            }
            //随机选择一个节点
            Type nextNode = getRandom.apply(currentNodeType);
            // 如果边已经走过
            if (nextNode == null || visitedEdges.contains(currentNodeType.word+nextNode.word)) {
                System.out.println("当前路径"+currentNodeType.word+" -> "+nextNode.word+"已经走过");
                break;
            }
            visitedEdges.add(currentNodeType.word+nextNode.word);
            currentNode = nextNode;
        }
        // 使用 String.join() 方法将列表转换为字符串
        String joinedString = String.join(" ",traversal);
        // 指定输出文件路径
        String filePath = "./output.txt";

        try {
            // 创建 BufferedWriter 对象
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(joinedString);
            // 关闭 writer
            writer.close();
            System.out.println("字符串列表已成功写入到文件 ：" + joinedString);
        } catch (IOException e) {
            System.err.println("写入文件时出现错误：" + e.getMessage());
            e.printStackTrace();
        }
        return joinedString;

    }

    public static void buildGraph(String processedText) throws IOException {
        String[] words = processedText.split("\\s+");
        String prevWord = null;
        for (String word : words) {
            Type node = nodes.computeIfAbsent(word, Type::new);//如果node在图中不存在创建一个新的节点
            if (prevWord != null) {
                Type prevNode = nodes.get(prevWord);
                prevNode.outgoingEdges.merge(node, 1, Integer::sum);//将当前单词节点node添加到前一个单词节点prevNode的出边集合中。如果出边集合中已经存在到node的边，则将它们的权重加一；否则，创建一条新的边，权重为1。
            }
            prevWord = word;
        }

    }

    public static void showPath(String Path,int k) {
        for (Edge edge : graph.getEachEdge()) {
            edge.addAttribute("ui.style", "fill-color: black;");
        }
        String[] colors = {
                "rgb(255, 0, 0)",    // 红色
                "rgb(0, 255, 0)",    // 绿色
                "rgb(0, 0, 255)",    // 蓝色
                "rgb(255, 165, 0)",  // 橙色
                "rgb(255, 192, 203)", // 粉色
                "rgb(165, 42, 42)",  // 棕色
                "rgb(0, 0, 139)",    // 深蓝色
                "rgb(173, 216, 230)", // 浅蓝色
                "rgb(255, 215, 0)",  // 金色
                "rgb(192, 192, 192)", // 银色
                "rgb(128, 128, 0)",  // 橄榄绿
                "rgb(0, 128, 128)",  // 海蓝色
                "rgb(147, 112, 219)", // 淡紫色
                "rgb(128, 128, 128)", // 灰色
                "rgb(255, 0, 255)",  // 洋红色
                "rgb(135, 206, 235)", // 天蓝色
                "rgb(210, 105, 30)"   // 巧克力色
        };
        if(Path!=null){
            List<String> shortestPath;
            String[] strList = Path.split("\\s+");
            shortestPath = Arrays.asList(strList);
            for (int i = 0; i < shortestPath.size() - 1; i++) {
                String currentNode = shortestPath.get(i);
                String nextNode = shortestPath.get(i + 1);
                Edge edge = graph.getEdge(currentNode + "->" + nextNode);
                String color = colors[k];
                edge.addAttribute("ui.style", "fill-color: " + color + ";"); // 标识最短路径的边为红色
            }
            //graph.display();
        } else {
            System.out.println("No path found.");
        }
    }

    public static void main(String[] args) {
        try {
            String file_path = getFilePath();
            String processedText = processText(file_path);
            System.out.println(processedText);
            buildGraph(processedText);
            showDirectedGraph(nodes);
            while(true){
                System.out.println("请选择以下功能:\n1、查询桥接词\n2、根据bridge word生成新文本\n3、计算两个单词之间的最短路径\n4、随机游走\n5、保存当前图片\n6、输入其他退出");
                int work = scanner.nextInt();
                scanner.nextLine();
                if(work==1){
                    System.out.print("Enter word1 : ");
                    String word1 = scanner.nextLine();
                    System.out.print("Enter word2 : ");
                    String word2 = scanner.nextLine();
                    String bridge_words = queryBridgeWords(word1, word2);
                    if (!bridge_words.isEmpty()&&!bridge_words.equals("1")) {
                        System.out.println("The bridge words from \""+word1+"\" to \""+word2+"\" is: "+bridge_words);
                    }else if(!bridge_words.equals("1")){
                        System.out.println("No bridge words from \""+word1+"\" and \""+word2+"\" in the graph !");
                    }
                }else
                if(work==2){
                    System.out.print("请输入句子:");
                    String word1 = scanner.nextLine();
                    word1 = word1.toLowerCase();
                    System.out.println("当前文本："+word1);
                    generateNewText(word1);
                }else
                if(work==3){
                    System.out.println("请输入最短路径模式:\n1、计算两点之间最短距离\n2、遍历一个词到其他词的最短路径");
                    int mode = scanner.nextInt();
                    scanner.nextLine();
                    for (Edge edge : graph.getEachEdge()) {
                        edge.addAttribute("ui.style", "fill-color: black;");
                    }
                    String Path="";
                    int k=0;
                    if(mode==1){
                        System.out.print("Enter word1 : ");
                        String startWord = scanner.nextLine();
                        startWord =startWord.toLowerCase();
                        System.out.print("Enter word2 : ");
                        String endWord = scanner.nextLine();
                        endWord = endWord.toLowerCase();
                        if(nodes.get(startWord)==null)
                            System.out.println("No \""+startWord+"\" in the graph !");
                        else if(nodes.get(endWord)==null)
                            System.out.println("No \""+endWord+"\" in the graph !");
                        else {
                            Path = shortestPath(startWord, endWord);
                            System.out.println(Path);
                            showPath(Path,k);
                        }
                    }else if(mode==2){
                        System.out.print("Enter word1 : ");
                        String startWord = scanner.nextLine();
                        startWord =startWord.toLowerCase();
                        if(nodes.get(startWord)==null)
                            System.out.println("No \""+startWord+"\" in the graph !");
                        else {
                        for(Map.Entry<String, Type> entry : nodes.entrySet()) {
                            if (!entry.getKey().equals(startWord)) {
                                Path = shortestPath(startWord, entry.getKey());
                                showPath(Path,k);
                                System.out.println("Path: "+startWord+" -> "+entry.getKey());
                                System.out.print("输入1继续遍历");
                                int word = scanner.nextInt();
                                if(word!=1){
                                    break;
                                }
                                k++;
                            }
                        }
                        System.out.println("遍历结束 ");
                    }}
                }else
                if(work==4){
                    System.out.println("请输入随机游走次数:");
                    String randomPath="";
                    int ifwalk = scanner.nextInt();
                    scanner.nextLine();
                    int j=0;
                    while (true){
                        if(j>=ifwalk)
                            break;
                        randomPath=randomWalk();
                        showPath(randomPath,j);
                        j++;
                    }
                }else
                if(work==5){

                    System.out.print("请输入图片文件名：");
                    String graphPath = scanner.nextLine();
                    // Create a FileSinkImages instance for saving as PNG
                    FileSinkImages fileSink = new FileSinkImages(FileSinkImages.OutputType.PNG, FileSinkImages.Resolutions.HD1080);
                    // Write the graph to the PNG file
                    try {
                        fileSink.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
                        fileSink.writeAll(graph, "./"+graphPath+".png");
                        System.out.println("图片保存成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else
                    break;
            }
            System.exit(0);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}