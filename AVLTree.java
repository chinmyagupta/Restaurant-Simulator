
class AVL_node {
    int value, balance, height, idx;
    AVL_node left, right, parent;

    AVL_node(int key, AVL_node papa) {
        value = key;
        parent = papa;
    }
}

public class AVLTree {
    private AVL_node root;


    private AVL_node search_customer_help(AVL_node sub_root, int val) {
        if (sub_root == null) return null;
        else if (sub_root.value == val) return sub_root;
        if (sub_root.value > val) return search_customer_help(sub_root.left, val);
        return search_customer_help(sub_root.right, val);
    }

    public AVL_node get_customer(int val) {
        return search_customer_help(this.root, val);
    }

    public boolean search_customer(int val) {
        return search_customer_help(this.root, val) != null;
    }


    public void insert(int val) {
        if (root == null) {
            root = new AVL_node(val, null);
            return;
        }
        AVL_node root1 = root;
        AVL_node papa;
        while (root1.value != val) {
            papa = root1;
            boolean greater = root1.value > val;
            if (root1.value < val) root1 = root1.right;
            else root1 = root1.left;
            if (root1 == null) {
                if (greater) {
                    papa.left = new AVL_node(val, papa);
                } else {
                    papa.right = new AVL_node(val, papa);
                }
                tree_bal(papa);
                return;
            }
        }
    }

    private void set_height(AVL_node node) {
        if (node == null) return;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
    }

    private int height(AVL_node node) {
        if (node != null) return node.height;
        return -1;
    }

    private void setBalance(AVL_node... nodes) {
        for (AVL_node node : nodes) {
            set_height(node);
            node.balance = height(node.right) - height(node.left);
        }
    }

    private void tree_bal(AVL_node node) {
        setBalance(node);
        if (node.balance < -1 && height(node.left.left) >= height(node.left.right))
            node = rotateRight(node);
        else if (node.balance < -1)
            node = rotateLeftRight(node);
        if (node.balance > 1 && height(node.right.right) >= height(node.right.left))
            node = rotateLeft(node);
        else if (node.balance > 1)
            node = rotateRightLeft(node);

        if (node.parent != null) {
            tree_bal(node.parent);
            return;
        }
        root = node;
    }

    private AVL_node rotateLeft(AVL_node node) {
        AVL_node right = node.right;
        right.parent = node.parent;
        node.right = right.left;
        if (node.right != null) node.right.parent = node;
        right.left = node;
        node.parent = right;
        if (right.parent != null && right.parent.right == node) right.parent.right = right;
        else if (right.parent != null) right.parent.left = right;
        setBalance(node, right);
        return right;
    }

    private AVL_node rotateRight(AVL_node node) {
        AVL_node left = node.left;
        left.parent = node.parent;
        node.left = left.right;
        if (node.left != null) node.left.parent = node;
        left.right = node;
        node.parent = left;
        if (left.parent != null && left.parent.right == node) left.parent.right = left;
        else if (left.parent != null) left.parent.left = left;
        setBalance(node, left);
        return left;
    }

    private AVL_node rotateRightLeft(AVL_node n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }

    private AVL_node rotateLeftRight(AVL_node node) {
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }
}