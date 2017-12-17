import java.io.*;
import java.util.*;
import static java.lang.Math.abs;
import static java.lang.Math.min;

public class Proj2 {

    static char[] pretrav = new char[256];
    static char[] posttrav = new char[256];
    static String[] queries = new String[256];

    static Node root;
    static Tree T;
    static int size = 0;

    public static void main(String args[]) {

        BufferedReader br;
        BufferedWriter bw;

        // ### comment this
//        String inputFilePath = "C:\\Users\\driedell\\Desktop\\CSC316\\Proj2\\medium-input.txt";
//        String outputFilePath = "C:\\Users\\driedell\\Desktop\\CSC316\\Proj2\\medium-output.txt";
        String inputFilePath;
        String outputFilePath;

        try {
            br = new BufferedReader(new InputStreamReader(System.in));

// ### uncomment this
            System.out.print("Enter input file: ");
            inputFilePath = br.readLine();
            if (inputFilePath.contains("\"")) {
                inputFilePath = inputFilePath.replace("\"", "");
            }

            System.out.println("Enter output file: ");
            outputFilePath = br.readLine();
            if (outputFilePath.contains("\"")) {
                outputFilePath = outputFilePath.replace("\"", "");
            }

            System.out.println("Input File: " + inputFilePath);
            System.out.println("Output File: " + outputFilePath);

            parse(inputFilePath);

            System.out.print("pretrav:  ");
            for (char c : pretrav) {
                System.out.print(c);
            }

            System.out.print("\nposttrav: ");
            for (char c : posttrav) {
                System.out.print(c);
            }

            System.out.println();

            while (pretrav[size] != '\0') {
                size++;
            }
            System.out.println("size: " + size);

            root = buildTree(size, 0, size - 1);
            T = new Tree(root);


            bw = new BufferedWriter(new FileWriter(outputFilePath));

            for (int i = 0; i < queries.length; i++) {
                if (queries[i] == null) {
                    break;
                }

                char a = queries[i].charAt(0);
                char b = queries[i].charAt(1);

//                System.out.println(T.findRelationship(a, b));

                bw.write(T.findRelationship(a, b) + "\n");

            }

            bw.write("Level Order: ");

            System.out.print("level order: ");
            T.printLevelOrder(bw);

            br.close();
            bw.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    static void parse(String inputFilePath) {
        String line = null;

//        System.out.println(inputFilePath);

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFilePath));

            int j = 0;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("<")) {
                    j = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == '<' && i == 0) {
                            continue;
                        }
                        if (line.charAt(i) == ' ' || line.charAt(i) == ',') {
                            continue;
                        }
                        if (line.charAt(i) == '.') {
                            break;
                        }
                        pretrav[j] = line.charAt(i);
                        j++;
                    }
                    j = 0;
                }
                else if(line.startsWith(">")) {
                    j = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == '>' && i == 0) {
                            continue;
                        }
                        if (line.charAt(i) == ' ' || line.charAt(i) == ',') {
                            continue;
                        }
                        if (line.charAt(i) == '.') {
                            break;
                        }
                        posttrav[j] = line.charAt(i);
                        j++;
                    }
                    j = 0;
                }
                else if (line.startsWith("?")) {
                    System.out.print("query: ");
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == '?' && i == 0) {
                            queries[j] = "";
                            continue;
                        }
                        if (line.charAt(i) == ' ' || line.charAt(i) == ',') {
                            continue;
                        }
                        if (line.charAt(i) == '.') {
                            break;
                        }
                        queries[j] += line.charAt(i);
                    }
                    System.out.println(queries[j]);
                    j++;
                }
//                for (int i = 0; i < queries.length; i++) {
//                    if (queries[i] != null) {
//                        System.out.println(i + ": " + queries[i]);
//                    }
//                }

            }
            br.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    static int preindex;

    static Node buildTree(int size, int prestart, int poststart) {

        // Base case
        if (preindex >= size || prestart > poststart)
            return null;

        Node parent = new Node(pretrav[preindex]);
        preindex++;

        if (prestart == poststart || preindex >= size)
            return parent;
        int i = 0;

        while (prestart < poststart) {

            // Search the next element of pretrav[] in posttrav[]
            for (i = prestart; i <= poststart; i++) {
                if (posttrav[i] == pretrav[preindex])
                    break;
            }

            parent.addChild(buildTree(size, prestart, i));

            prestart = i + 1;
        }

        return parent;
    }
}



class Tree {
    private Node root;

    public Node getRoot() {
        return this.root;
    }

    public Tree(Node n) {
        root = n;
    }


    public void printTree(Node n) {
        if (n.parent == null) {
            System.out.println(n.data + " root mark: " + n.mark);
        } else {
            System.out.println(n.data + " parent: " + n.getParent().data + " mark: " + n.mark);
        }

        if (n.children == null) {
            return;
        } else {
            for (Node myNode : n.children) {
                printTree(myNode);
            }
        }
    }


    public String findRelationship(char a, char b) {
        clearMarks(this.getRoot());

        markAncestors(this.getRoot(), a);

        Node ancestor = findAncestor(this.getRoot(), b, false);

        String path1String = "";
        ArrayList<Node> path1 = new ArrayList<Node>();
        findPath(ancestor, a, path1);
        for (Node n : path1) {
            path1String += n.data;
        }
//        System.out.println("Path1: " + path1String);

        String path2String = "";
        ArrayList<Node> path2 = new ArrayList<Node>();
        findPath(ancestor, b, path2);
        for (Node n : path2) {
            path2String += n.data;
        }
//        System.out.println("Path2: " + path2String);


        int aDist = path1String.length()-1;
        int bDist = path2String.length()-1;

//        System.out.println("a dist: " + aDist);
//        System.out.println("b dist: " + bDist);

        String relationship = "not defined";


        if (aDist >= 3 && bDist == 0) {
            relationship = a + " is " + b + "'s great^" + Integer.toString(aDist-2) + " grandchild";
            return relationship;
        }
        if (aDist > 2 && bDist == 1) {
            relationship = a + " is " + b + "'s great^" + Integer.toString(aDist-2) + " nephew";
            return relationship;
        }
        if (aDist >=2 && bDist >=2) {
            relationship = a + " is " + b + "'s " + (min(aDist, bDist)-1) + "th cousin " + abs(aDist - bDist) + " times removed";
            return relationship;
        }


        switch (aDist) {
            case 0:
                switch (bDist) {
                    case 0: relationship = a + " is " + b; break;
                    case 1: relationship = a + " is " + b + "'s parent"; break;
                    case 2: relationship = a + " is " + b + "'s grandparent"; break;
                    case 3: relationship = a + " is " + b + "'s great-grandparent"; break;
                    default: relationship = a + " is " + b + "'s great^" + Integer.toString(bDist-2) + " grandparent"; break;
                }
                break;
            case 1:
                switch (bDist) {
                    case 0: relationship = a + " is " + b + "'s child"; break;
                    case 1: relationship = a + " is " + b + "'s sibling"; break;
                    case 2: relationship = a + " is " + b + "'s uncle"; break;
                    default: relationship = a + " is " + b + "'s great^" + Integer.toString(bDist-2) + " uncle"; break;
                }
                break;
            case 2:
                switch (bDist) {
                    case 0: relationship = a + " is " + b + "'s grandchild"; break;
                    case 1: relationship = a + " is " + b + "'s nephew"; break;
                }
                break;
            default:
                relationship = "not defined";
                break;
        }

        return relationship;
    }

    public boolean markAncestors(Node n, char c) {

        if (n != null) {
            if (n.data == c) {
                n.mark = true;
                return true;
            } else {
                for (Node child : n.children) {
                    n.mark = markAncestors(child, c);
                    if (n.mark) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void clearMarks(Node n) {
        n.mark = false;

        if (n.children != null) {
            for (Node child : n.children) {
                clearMarks(child);
            }
        }

    }

    public Node findAncestor(Node n, char c, boolean found) {
        if (found) {
            if (n.mark) {
                return n;
            } else {
                return findAncestor(n.getParent(), c, found);
            }
        } else {
            if (n != null) {
                if (n.data == c) {

                    return findAncestor(n, c, true);

                } else {
                    for (Node child : n.children) {
                        Node myNode = findAncestor(child, c, false);
                        if (myNode != null) {
                            return myNode;
                        }
                    }
                }
            }
            return null;
        }
    }


    public static String findPath(Node n, char c, String path) {
        if (n == null) {
            return path;
        }

        if (n.data == c) {
            path += n.data;
            return path;
        }

        for (Node child : n.children) {
            path = findPath(child, c, path);
            if (path.startsWith(Character.toString(c))) {
                path += n.data;
                return path;
            }
        }

        return path;


    }

    public static boolean findPath(Node n, char c, ArrayList<Node> path) {
        if (n == null) {
            return false;
        }
        if (n.data == c) {
            path.add(n);
            return true;
        }

        boolean inPath = false;

        for (Node child : n.children) {
            inPath = findPath(child, c, path);
            if (inPath) {
                path.add(0, n);
                return inPath;
            }
        }

        return false;
    }

    void printLevelOrder(BufferedWriter bw) {
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(root);
        while (!queue.isEmpty())
        {
            Node tempNode = queue.poll();
            System.out.print(tempNode.data + ", ");

            try {
                bw.write(tempNode.data + ", ");
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Node child : tempNode.children) {
                queue.add(child);
            }

        }
    }
}


class Node {
    char data;
    Node parent;
    List<Node> children;
    boolean mark;


    public Node(char data) {
        this.data = data;
        this.children = new ArrayList<Node>();
    }

    public Node(Node parent, char data) {
        this.parent = parent;
        this.data = data;
        this.children = new ArrayList<Node>();
    }

    public void addChild(Node child) {
        this.children.add(child);
        child.setParent(this);
    }

    public Node getParent() {
        return this.parent;
    }

    private Node setParent(Node n) {
        return this.parent = n;
    }
}
