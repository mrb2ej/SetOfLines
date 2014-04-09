package redblacktree;

public class RedBlackTree<E extends Comparable> {
	Node nil;
	Node root;

	public RedBlackTree() {
		nil = new Node();
		nil.color = Color.BLACK;
		root = nil;
	}

	public boolean isEmpty() {
		return root == nil;
	}

	private E find(E element, Node current) {
		if (current == nil)
			return null;
		int c = current.data.compareTo(element);
		if (c > 0) {
			return find(element, current.right);
		} else if (c < 0) {
			return find(element, current.left);
		}
		return current.data;
	}

	public E find(E element) {
		return find(element, root);
	}

	public void insert(E data) {
		Node newNode = new Node();
		newNode.data = data;
		newNode.color = Color.RED;
		newNode.left = nil;
		newNode.right = nil;

		Node x = root;
		Node y = nil;
		while (x != nil) {
			y = x;
			if (x.data.compareTo(data) >= 0) {
				x = x.left;
			} else {
				x = x.right;
			}
		}

		if (y == nil) {
			root = newNode;
			newNode.color = Color.BLACK;
			return;
		}
		newNode.p = y;
		if (y.data.compareTo(data) >= 0) {
			y.left = newNode;

		} else {
			y.right = newNode;
		}
		insertFix(newNode);
	}

	private void insertFix(Node x) {

	}

	private void rightRotate(Node x) throws RotateException {
		if (x == nil)
			throw new RotateException("Error: Attempted to rotate nil");
		if (x.left == nil)
			throw new RotateException(
					"Error: Attempted to right-rotate a node without a left child");
		
		Node y = x.left;
		x.left = y.right;
		x.left.p = x;
		y.p = x.p;
		x.p = y;
		
		if (y.p == nil) {
			root = y;
		} else if (y.p.left == x) {
			y.p.left = y;
		} else {
			y.p.right = y;
		}
	}

	private void rotateLeft(Node x) throws RotateException {
		if (x == nil)
			throw new RotateException("Error: Attempted to rotate nil");
		if (x.right == nil)
			throw new RotateException(
					"Error: Attempted to left-rotate a node without a right child");
		
		Node y = x.right;
		x.right = y.left;
		x.right.p = x;
		y.p = x.p;
		x.p = y;
		
		if (y.p == nil) {
			root = y;
		} else if (y.p.left == x) {
			y.p.left = y;
		} else {
			y.p.right = y;
		}
	}

	private class Node {
		E data;
		Color color;
		Node p;
		Node left;
		Node right;
	}

	private enum Color {
		RED, BLACK;
	}

}
