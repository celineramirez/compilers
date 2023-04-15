public class AbstractSyntaxTree {
    public Node root;

    public class Node {
        public String data;
        public Node left;
        public Node right;

        public Node(String data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }

        public void printNode() {
            System.out.printf(data);
        }
    }

    public AbstractSyntaxTree() {
        root = null;
    }

    public void insert(String data) {
        root = insert(root, data);
    }

    private Node insert(Node node, String data) {
        if (node == null) {
            node = new Node(data);
        } else {
            if (data.compareTo(node.data) <= 0) {
                node.left = insert(node.left, data);
            } else {
                node.right = insert(node.right, data);
            }
        }

        return node;
    }

    public void traverseInOrder() {
        traverseInOrder(root);
    }

    private void postOrder(Node localRoot) { // LRN

        if (localRoot != null) {
            postOrder(localRoot.left);
            postOrder(localRoot.right);
            localRoot.printNode();
        }
    }

    public void printPostorder() {
        postOrder(root);
    }
    private void traverseInOrder(Node node) {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(node.data + " ");
            traverseInOrder(node.right);
        }
    }
}
